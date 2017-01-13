package net.katsstuff.mineview.webclient.impl.world.biomes

import net.katsstuff.mineview.shared.world.Biome

class BiomePlains extends Biome {
	override def temperature: Float = 0.8F
	override def humidity: Float = 0.4F

	override def grassColor: Int = 0x00FF00 //TODO
	override def skyColor: Int = 0x00FF00 //TODO
	override def waterColor: Int = 0x00FF00 //TODO
	override def leavesColor: Int = 0x00FF00 //TODO
}
