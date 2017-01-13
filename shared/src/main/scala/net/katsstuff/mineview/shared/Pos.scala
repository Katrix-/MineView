package net.katsstuff.mineview.shared

import scala.ref.WeakReference

import net.katsstuff.mineview.shared.world.World

case class Pos(x: Double, y: Double, z: Double, world: WeakReference[World])
