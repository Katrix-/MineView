package net.katsstuff.mineview.shared.world

trait Biome {

	def temperature: Float
	def humidity: Float

	def grassColor: Int
	def skyColor: Int
	def waterColor: Int
	def leavesColor: Int

}
