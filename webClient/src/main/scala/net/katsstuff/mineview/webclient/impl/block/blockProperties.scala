package net.katsstuff.mineview.webclient.impl.block

import scala.scalajs.js.annotation.JSExportAll

import net.katsstuff.mineview.shared.block.BlockProperty
import scalajs.js

@JSExportAll
abstract class AbstractBlockProperties[A](val name: String) extends BlockProperty[A]

@JSExportAll
class SetBlockProperty[A](name: String, val _allowedValues: js.Array[A]) extends AbstractBlockProperties[A](name) {
	override def valueName(value: A): String = value.toString
	override def allowedValues: Set[A] = _allowedValues.toSet
}