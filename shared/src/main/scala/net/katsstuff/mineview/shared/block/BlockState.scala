package net.katsstuff.mineview.shared.block

import net.katsstuff.mineview.shared.BlockPos
import net.katsstuff.mineview.shared.world.World

trait BlockState {

	def block: Block
	def model: String = block.model(this)

	def getValue[A](property: BlockProperty[A]): Option[A]
	def withProperty[A, B <: A](property: BlockProperty[A], value: B): Option[BlockState]
	def withExtendedProperties(pos: BlockPos, world: World): BlockState = block.extendedState(this, pos, world)
	def allProperties: Set[BlockProperty[_]]

	def isAir: Boolean = block.isAir(this)

}
