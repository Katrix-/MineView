package net.katsstuff.mineview.shared.block

trait BlockProperty[A] {

	def name: String
	def allowedValues: Set[A]
	def valueName(value: A): String
}
