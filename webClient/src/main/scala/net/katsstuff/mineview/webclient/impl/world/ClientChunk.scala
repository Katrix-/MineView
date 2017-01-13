package net.katsstuff.mineview.webclient.impl.world

import net.katsstuff.mineview.shared.block.BlockState
import net.katsstuff.mineview.shared.world.{Biome, Chunk}
import spire.math.UByte

class ClientChunk(
		val x: Int,
		val z: Int,
		val sections: Array[Section],
		val biomes: Array[Biome],
		override val heightMap: Array[Byte]) extends Chunk {

	override def blockAt(x: Byte, y: UByte, z: Byte): BlockState = sections(y.toInt / 16).blocksAt(x, (y.signed % 16).toByte, z)
	override def biomeAt(x: Byte, z: Byte): Biome = biomes(x * 16 + z)
	override def blockLightAt(x: Byte, y: UByte, z: Byte): Byte = sections(y.toInt / 16).blockLightAt(x, (y.signed % 16).toByte, z)
	override def skyLightAt(x: Byte, y: UByte, z: Byte): Byte = sections(y.toInt / 16).skyLightAt(x, (y.signed % 16).toByte, z)
	override def heightAt(x: Byte, z: Byte): UByte = UByte(heightMap(x * 16 + z))
	override def iterable: Iterable[BlockState] = sections.flatMap(_.blocks)
}