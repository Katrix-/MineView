package net.katsstuff.mineview.webclient.render.model

import scala.concurrent.{Future, Promise}

import org.denigma.threejs.{BufferGeometry, LoadingManager, XHRLoader}
import scalajs.js
import scalajs.js.Dynamic.{global => g, literal => lit, newInstance => jsnew}

import net.katsstuff.mineview.shared.DomainName
import net.katsstuff.mineview.webclient.helper.DynamicHelper._

class JSONLoader(loadingManager: LoadingManager = g.THREE.DefaultLoadingManager.asInstanceOf[LoadingManager]) {

	var path = ""

	def loadAll(url: DomainName, onProgress: js.Any => Unit = any => ()): Future[JSONModel] = {
		val loader = new XHRLoader(loadingManager)
		loader.dyn.setPath(path)
		val promise = Promise[JSONModel]()

		loader.load(url.toLocatedPath("blocks"), (text: String) => {
			promise.completeWith(parseAll(text))
			()
		}, onProgress, (any: js.Any) => {
			promise.failure(js.JavaScriptException(any))
			()
		})

		promise.future
	}

	def parseAll(string: String): Future[JSONModel] = {

		val top = OptionPickler.read[JSONModel](string)
		top.parent match {
			case Some(parent) => loadAll(parent).map(top.merge)(scalajs.concurrent.JSExecutionContext.queue)
			case None => Future.successful(top)
		}
	}

	def toGeometry(model: JSONModel): BufferGeometry = {
		val geometry = new BufferGeometry

		def addElement(element: Element): Unit = {
			val Element(from, to, rotation, shade /*TODO*/, faces) = element
			val faceSeq = Seq(
				Position.Down -> faces.down,
				Position.Up -> faces.up,
				Position.North -> faces.north,
				Position.South -> faces.south,
				Position.West -> faces.west,
				Position.East -> faces.east
			)
			faceSeq.foreach{
				case (position, Some(face)) => addFace(face, position, from, to)
				case (position, None) =>
			}
		}

		def addFace(face: Face, position: Position, from: Vec3, to: Vec3): Unit = {
			val Face(uv /*TODO*/, texture /*TODO*/, cullFace, rotation /*TODO*/, tintIndex /*TODO*/) = face

		}

		geometry
	}
}
