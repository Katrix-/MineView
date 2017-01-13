package net.katsstuff.mineview.shared.world

trait Region {

	def x: Short
	def y: Short

	def iterable: Iterable[Chunk]

	def getChunkAt(x: Int, y: Int): Chunk

}
