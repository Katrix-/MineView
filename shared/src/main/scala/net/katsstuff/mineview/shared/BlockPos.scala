package net.katsstuff.mineview.shared

import scala.ref.WeakReference
import scala.scalajs.js.annotation.JSExportAll

import net.katsstuff.mineview.shared.world.World

@JSExportAll
case class BlockPos(x: Int, y: Int, z: Int, world: WeakReference[World])
