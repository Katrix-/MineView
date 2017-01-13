package net.katsstuff.mineview.shared.world

import net.katsstuff.mineview.shared.block.BlockState
import spire.math.UByte

trait Chunk {

	def x: Int
	def z: Int

	def iterable: Iterable[BlockState]

	def blockAt(x: Byte, y: UByte, z: Byte): BlockState
	def biomeAt(x: Byte, z: Byte): Biome
	def blockLightAt(x: Byte, y: UByte, z: Byte): Byte
	def skyLightAt(x: Byte, y: UByte, z: Byte): Byte
	def heightAt(x: Byte, z: Byte): UByte

	/**
		* Gets the heightmap of a chunk.
		* The values are stored in a single array with z first, and then x.
		* The Bytes in the array are unsigned.
		*/
	def heightMap: Array[Byte] = {
		val heightMap = Array.ofDim[Byte](16 * 16)

		for(x <- 0 until 16; z <- 0 until 16) {
			heightMap(x * 16 + z) = heightAt(x.toByte, z.toByte).signed
		}

		heightMap
	}
}
