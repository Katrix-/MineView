package net.katsstuff.mineview.shared.block

import net.katsstuff.mineview.shared.world.World
import net.katsstuff.mineview.shared.{BlockPos, DomainName}

trait Block {

	def name: DomainName
	def model(state: BlockState): String
	def isAir(state: BlockState): Boolean
	def baseState: BlockState
	def defaultState: BlockState

	def stateFromData(data: Byte): BlockState
	def extendedState(state: BlockState, pos: BlockPos, world: World): BlockState

}
