package net.katsstuff.mineview.webclient.game

import scala.reflect.ClassTag

import net.katsstuff.mineview.shared.block.BlockState
import net.katsstuff.mineview.shared.world.{Biome, Chunk}
import net.katsstuff.mineview.webclient.impl.world.{ClientChunk, ClientSection, EmptySection}
import net.katsstuff.typenbt.nbt.{NBTByte, NBTByteArray, NBTCompound, NBTInt, NBTIntArray, NBTList, NBTTag, NBTType}
import shapeless.Typeable

object WorldLoader {

	private def sequenceOptions[A](seq: Seq[Option[A]]): Option[Seq[A]] = {
		if(seq.forall(_.isDefined)) Some(seq.map(_.get)) else None
	}

	implicit def nbtListTypeable[ERepr, ENBT <: NBTTag.Aux[ERepr]](
			implicit eReprTpe: Typeable[ERepr],
			eNBTTpe: Typeable[ENBT],
			listType: NBTType.Aux[ERepr, ENBT]
	): Typeable[NBTList[ERepr, ENBT]] = new Typeable[NBTList[ERepr, ENBT]] {
		override def cast(t: Any): Option[NBTList[ERepr, ENBT]] = {
			if(t == null) None
			else if(t.isInstanceOf[NBTList[_, _]]) {
				val list = t.asInstanceOf[NBTList[Any, NBTTag.Aux[Any]]]
				if(list.nbtListType == listType) {
					Some(list.asInstanceOf[NBTList[ERepr, ENBT]])
				}
				else None
			}
			else None
		}
		override def describe: String = s"NBTList[${eReprTpe.describe}, ${eNBTTpe.describe}]"
	}

	def loadChunk(compound: NBTCompound)(implicit registry: Registry): Option[Chunk] = {

		for {
			xPos <- compound.getRecursiveValue[Int, NBTInt]("Level", "xPos")
			zPos <- compound.getRecursiveValue[Int, NBTInt]("Level", "zPos")
			biomeBytes <- compound.getRecursiveValue[IndexedSeq[Byte], NBTByteArray]("Level", "Biomes")
			biomes <- sequenceOptions(biomeBytes.map(biomeIdToBiome))
			heightMap <- compound.getRecursiveValue[IndexedSeq[Int], NBTIntArray]("Level", "HeightMap")
			sections <- compound.getRecursiveValue[Seq[NBTCompound], NBTList[NBTCompound#Repr, NBTCompound]]("Level", "Sections")
			loadedSections = sections.flatMap(loadSection(_))
			filledSections = Array.tabulate(16)(i => loadedSections.find(_.y == i).getOrElse(new EmptySection(i.toByte)))
		} yield new ClientChunk(xPos, zPos, filledSections, biomes.toArray, heightMap.map(i => i.toByte).toArray)
	}

	def loadSection(compound: NBTCompound)(implicit registry: Registry): Option[ClientSection] = {
		for {
			y <- compound.getValue[Byte, NBTByte]("Y")
			partialBlocks <- compound.getValue[IndexedSeq[Byte], NBTByteArray]("Blocks")
			add = compound.getValue[IndexedSeq[Byte], NBTByteArray]("Add")
			expandedAdd = add.map(expandNibbles)
			blocks = expandedAdd
				.map(adds => partialBlocks.zip(adds)
					.map{ case (blockPart, addPart) => blockPart + addPart << 8 } )
				.getOrElse(partialBlocks
					.map(_.toInt))
			data <- compound.getValue[IndexedSeq[Byte], NBTByteArray]("Data")
			blockStates <- sequenceOptions(blocks.zip(expandNibbles(data)).map{ case (blockId, dataId) => blockIdToBlockState(blockId, dataId)})
			blockLight <- compound.getValue[IndexedSeq[Byte], NBTByteArray]("BlockLight")
			skyLight <- compound.getValue[IndexedSeq[Byte], NBTByteArray]("SkyLight")
		} yield new ClientSection(y, blockStates.toArray, expandNibbles(skyLight), expandNibbles(blockLight))
	}

	def biomeIdToBiome(byte: Byte)(implicit registry: Registry): Option[Biome] = registry.getBiome(byte)

	def blockIdToBlockState(id: Int, data: Byte)(implicit registry: Registry): Option[BlockState] = {
		registry.getBlock(id).map(_.stateFromData(data))
	}

	def expandNibbles(array: IndexedSeq[Byte]): Array[Byte] = {
		array.flatMap(b => Array((b & 0x0F).toByte, ((b >> 4) & 0x0F).toByte)).toArray
	}
}
