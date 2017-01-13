package net.katsstuff.mineview.webclient.impl.world

import net.katsstuff.mineview.shared.world.Biome

class ClientBiome(
		val temperature: Float,
		val humidity: Float,
		val grassColor: Int,
		val skyColor: Int,
		val waterColor: Int,
		val leavesColor: Int
) extends Biome
