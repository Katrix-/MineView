package net.katsstuff.mineview.shared

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
case class DomainName(domain: String, path: String) {

	def toPath: String = s"assets/$domain/$path"
	def toLocatedPath(located: String): String = s"assets/$domain/$located/$path"
}
object DomainName {

	def apply(domainName: String): DomainName = {
		val splitted = domainName.split(":", 1)
		if(splitted.length == 1) DomainName("minecraft", domainName)
		else DomainName(splitted.head, splitted(1))
	}

	import upickle.Js

	implicit val domainNameReader: upickle.default.Reader[DomainName] = upickle.default.Reader[DomainName] {
		case Js.Str(domainName) => DomainName(domainName)
	}

	implicit val domainNameWriter: upickle.default.Writer[DomainName] = upickle.default.Writer[DomainName] {
		case DomainName(domain, path) => Js.Str(s"$domain:$path")
	}
}
