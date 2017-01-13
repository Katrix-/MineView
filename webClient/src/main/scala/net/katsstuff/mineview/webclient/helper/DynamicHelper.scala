package net.katsstuff.mineview.webclient.helper

import scala.scalajs.js

object DynamicHelper {

	implicit class ToDynamic(val any: Any) extends AnyVal {
		def dyn: js.Dynamic = any.asInstanceOf[js.Dynamic]
	}

	implicit class FromDynamic(val dynamic: js.Dynamic) extends AnyVal {
		def byte: Byte = dynamic.asInstanceOf[Byte]
		def short: Short = dynamic.asInstanceOf[Short]
		def int: Int = dynamic.asInstanceOf[Int]
		def long: Long = dynamic.asInstanceOf[Long]
		def float: Float = dynamic.asInstanceOf[Float]
		def double: Double = dynamic.asInstanceOf[Double]
		def string: String = dynamic.asInstanceOf[String]
	}
}
