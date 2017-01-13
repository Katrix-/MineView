package net.katsstuff.mineview.webclient.impl.block

import scala.scalajs.js.annotation.JSExportAll

import net.katsstuff.mineview.shared.block.{Block, BlockState}
import net.katsstuff.mineview.shared.world.World
import net.katsstuff.mineview.shared.{BlockPos, DomainName}

@JSExportAll
class AbstractBlock(val name: DomainName) extends Block {
	override def model(state: BlockState): String = s"assets/${name.domain}/blockstates/${name.path}"
	override def isAir(state: BlockState): Boolean = false
	override def baseState: BlockState = JsBlockState(this)
	override def defaultState: BlockState = baseState
	override def stateFromData(data: Byte): BlockState = defaultState
	override def extendedState(state: BlockState, pos: BlockPos, world: World): BlockState = state
}
