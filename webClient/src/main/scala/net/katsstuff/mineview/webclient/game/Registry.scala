package net.katsstuff.mineview.webclient.game

import scala.collection.mutable

import net.katsstuff.mineview.shared.block.Block
import net.katsstuff.mineview.shared.world.Biome

class Registry {

	private val idToBlock = new mutable.HashMap[Int, Block]()
	private val idToBiome = new mutable.HashMap[Byte, Biome]()

	def useExternalBlockMap(idToBlock: Map[Int, Block]): Unit = {
		this.idToBlock.clear()
		this.idToBlock ++= idToBlock
	}

	def registerBlock(id: Int, block: Block): Unit = idToBlock.put(id, block)
	def setDefaultBlock(block: Block): Unit = idToBlock.withDefaultValue(block)
	def getBlock(id: Int): Option[Block] = idToBlock.get(id)

	def useExternalBiomeMap(idToBiome: Map[Byte, Biome]): Unit = {
		this.idToBiome.clear()
		this.idToBiome ++= idToBiome
	}

	def registerBiome(id: Byte, biome: Biome): Unit = idToBiome.put(id, biome)
	def setDefaultBiome(biome: Biome): Unit = idToBiome.withDefaultValue(biome)
	def getBiome(id: Byte): Option[Biome] = idToBiome.get(id)

}
