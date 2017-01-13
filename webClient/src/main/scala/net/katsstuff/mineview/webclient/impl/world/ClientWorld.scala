package net.katsstuff.mineview.webclient.impl.world

import java.util.UUID

import net.katsstuff.mineview.shared.world.{Chunk, Dimension, World}

class ClientWorld(val name: String, val uuid: UUID, val dimension: Dimension) extends World {
	var time: Long = 0L
	var rainTime: Long = 0L
	var thunderTime: Long = 0L
	override def chunkAt(x: Int, z: Int): Chunk = ???
}
