package net.katsstuff.mineview.webclient.impl.block

import scala.scalajs.js.annotation.JSExportAll

import net.katsstuff.mineview.shared.block.{Block, BlockProperty, BlockState}

import scalajs.js

@JSExportAll
case class JsBlockState(block: Block, properties: Map[BlockProperty[_], _] = Map()) extends BlockState {

	override def getValue[A](property: BlockProperty[A]): Option[A] = {
		properties.get(property).asInstanceOf[Option[A]]
	}

	def getValueJs[A](property: BlockProperty[A]): js.UndefOr[A] = {
		properties.get(property).map(any => any.asInstanceOf[A]: js.UndefOr[A]).getOrElse(js.undefined)
	}

	override def withProperty[A, B <: A](property: BlockProperty[A], value: B): Option[BlockState] = {
		if(allProperties.contains(property) && property.allowedValues.contains(value))
			Some(JsBlockState(block, properties.updated(property, value)))
		else None
	}

	def withPropertyJs[A, B <: A](property: BlockProperty[A], value: B): js.UndefOr[BlockState] = {
		if(allProperties.contains(property) && property.allowedValues.contains(value))
			JsBlockState(block, properties.updated(property, value))
		else js.undefined
	}

	override def allProperties: Set[BlockProperty[_]] = properties.keySet
}
