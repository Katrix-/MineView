package net.katsstuff.mineview.shared.world

import java.util.UUID

import net.katsstuff.mineview.shared.block.BlockState
import spire.math.UByte

trait World {

	def name: String
	def uuid: UUID
	def time: Long
	def rainTime: Long
	def thunderTime: Long
	def dimension: Dimension

	def chunkAt(x: Int, z: Int): Chunk
	def blockAt(x: Int, y: UByte, z: Int): BlockState = chunkAt(x / 16, z / 16).blockAt((x % 16).toByte, y, (z % 16).toByte)
	def biomeAt(x: Int, z: Int): Biome = chunkAt(x / 16, z / 16).biomeAt((x % 16).toByte, (z % 16).toByte)
	def blockLightAt(x: Int, y: UByte, z: Int): Int = chunkAt(x / 16, z / 16).blockLightAt((x % 16).toByte, y, (z % 16).toByte)
	def skyLightAt(x: Int, y: UByte, z: Int): Int = chunkAt(x / 16, z / 16).skyLightAt((x % 16).toByte, y, (z % 16).toByte)
	def heightAt(x: Int, z: Int): UByte = chunkAt(x / 16, z / 16).heightAt((x % 16).toByte, (z % 16).toByte)

}
