package net.katsstuff.mineview.webclient.jshelper

import scala.collection.immutable
import scala.scalajs.js.annotation.JSExportAll

import net.katsstuff.mineview.shared.BlockPos
import scalajs.js

import net.katsstuff.mineview.webclient.impl.world.ClientWorld

@JSExportAll
object JSHelper {

	def blockPosWorld(blockPos: BlockPos): js.UndefOr[ClientWorld] = blockPos.world.get.filter {
		case _: ClientWorld => true
		case _ => false
	}.map(world => world.asInstanceOf[ClientWorld]: js.UndefOr[ClientWorld]).getOrElse(js.undefined)

	def mapConstructor[A, B](array: js.Array[js.Tuple2[A, B]]): Map[A, B] = {
		val b = immutable.Map.newBuilder[A, B]
		for (t <- array) {
			b += ((t._1, t._2))
		}

		b.result()
	}

}
