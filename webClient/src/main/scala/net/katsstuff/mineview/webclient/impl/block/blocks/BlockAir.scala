package net.katsstuff.mineview.webclient.impl.block.blocks

import scala.scalajs.js.annotation.JSExportAll

import net.katsstuff.mineview.shared.DomainName
import net.katsstuff.mineview.shared.block.BlockState
import net.katsstuff.mineview.webclient.impl.block.AbstractBlock

@JSExportAll
class BlockAir extends AbstractBlock(DomainName("air")) {
	override def isAir(state: BlockState): Boolean = true
}
