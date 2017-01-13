package net.katsstuff.mineview.webclient.render

import scala.scalajs.js.Dynamic.{global => g, literal => lit, newInstance => jsnew}

import org.denigma.threejs.extensions.controls.CameraControls
import org.denigma.threejs.{Camera, Scene, WebGLRenderer, WebGLRendererParameters}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement

import net.katsstuff.mineview.webclient.helper.DynamicHelper._

trait Scene3D {

	protected val stats = jsnew(g.Stats)()

	def container: HTMLElement
	def width: Double
	def height: Double

	def pixelRatio: Double = window.devicePixelRatio

	protected val scene = new Scene

	type UsedCamera <: Camera
	type UsedControls <: CameraControls

	protected lazy val camera = initCamera
	protected lazy val renderer = initRenderer
	protected lazy val controls = initControls

	protected def initCamera: UsedCamera
	protected def initRenderer: WebGLRenderer = {
		val params = lit(
			antialias = true,
			alpha = true
		).asInstanceOf[ WebGLRendererParameters]
		val renderer = new WebGLRenderer(params)

		renderer.domElement.style.position = "absolute"
		renderer.domElement.style.top = "0"
		renderer.domElement.style.margin = "0"
		renderer.domElement.style.padding= "0"
		renderer.dyn.setPixelRatio(window.devicePixelRatio)
		renderer.setSize(width, height)
		renderer
	}
	protected def initControls: UsedControls

	def initScene(): Unit = {
		container.style.width = width.toString
		container.style.height = height.toString
		container.style.position = "relative"
		container.appendChild(renderer.domElement)
		container.appendChild(stats.dom.asInstanceOf[Node])
	}

	protected def renderFuc(double: Double): Unit = {
		renderer.render(scene, camera)
		controls.update()
		stats.update()
		render()
	}

	def render(): Unit = dom.window.requestAnimationFrame(renderFuc _)

}
