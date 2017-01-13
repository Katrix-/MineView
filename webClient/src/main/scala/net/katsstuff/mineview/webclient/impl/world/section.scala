package net.katsstuff.mineview.webclient.impl.world

import net.katsstuff.mineview.shared.block.BlockState

trait Section {
	def y: Byte
	def blocksAt(x: Byte, y: Byte, z: Byte): BlockState
	def skyLightAt(x: Byte, y: Byte, z: Byte): Byte
	def blockLightAt(x: Byte, y: Byte, z: Byte): Byte
	def blocks: Array[BlockState]
}
class ClientSection(
		val y: Byte,
		val blocks: Array[BlockState],
		val blockLight: Array[Byte],
		val skyLight: Array[Byte]
) extends Section {

	private def blockPos(x: Byte, y: Byte, z: Byte): Int = y + z * 16 + x

	override def blocksAt(x: Byte, y: Byte, z: Byte): BlockState = blocks(blockPos(x, y, z))
	override def skyLightAt(x: Byte, y: Byte, z: Byte): Byte = skyLight(blockPos(x, y, z))
	override def blockLightAt(x: Byte, y: Byte, z: Byte): Byte = blockLight(blockPos(x, y, z))
}

class EmptySection(val y: Byte) extends Section {
	override def blocksAt(x: Byte, y: Byte, z: Byte): BlockState = ??? //Air
	override def skyLightAt(x: Byte, y: Byte, z: Byte): Byte = 15
	override def blockLightAt(x: Byte, y: Byte, z: Byte): Byte = 0
	override def blocks: Array[BlockState] = EmptySection.emptyBlocks
}

object EmptySection {

	val emptyBlocks: Array[BlockState] = Array.fill(16 * 16 * 16)(???) //Air

}