package net.katsstuff.mineview.webclient.render

import org.denigma.threejs.extensions.controls.HoverControls
import org.denigma.threejs.{Camera, OrthographicCamera, Vector3}
import org.scalajs.dom.KeyboardEvent
import org.scalajs.dom.raw.{Event, HTMLElement, MouseEvent}

import net.katsstuff.mineview.webclient.helper.DynamicHelper._

class MineViewControls(camera: Camera, element: HTMLElement, center: Vector3 = new Vector3()) extends HoverControls(camera, element, center) with
	KeyboardCameraControls {

	userPanSpeed = 2D
	userRotateSpeed = 1D
	userZoomSpeed = 1.5D

	minDistance = 10
	maxDistance = 1000D

	var minZoom = 0.4D
	var maxZoom = 5
	var orthoZoomSpeedDivider = 7.5D

	override def onKeyDown(event: KeyboardEvent): Unit = ()
	override def onKeyUp(event: KeyboardEvent): Unit = ()

	def clamp(v: Double, min: Double, max: Double): Double = if(v < min) min else if(v > max) max else v

	override def zoomOut(zScale: Double): Unit = {
		camera match {
			case ortho: OrthographicCamera =>
				ortho.dyn.zoom = clamp(ortho.dyn.zoom.double + zScale / orthoZoomSpeedDivider, minZoom, maxZoom)
				ortho.updateProjectionMatrix()
			case _ => super.zoomOut(zScale)
		}
	}

	override def zoomIn(zScale: Double): Unit =  camera match {
		case ortho: OrthographicCamera =>
			ortho.dyn.zoom = clamp(ortho.dyn.zoom.double - zScale / orthoZoomSpeedDivider, minZoom, maxZoom)
			ortho.updateProjectionMatrix()
		case _ => super.zoomIn(zScale)
	}

	override def onMouseDown(event: MouseEvent): Unit = {
		if(userRotate && enabled) {
			if(state == HoverControls.Calm) {
				state = event.button match {
					case 0 => HoverControls.Pan
					case 1 => HoverControls.Calm
					case 2 => HoverControls.Rotate
				}
			}

			state match {
				case HoverControls.Rotate => rotateStart.set(event.clientX, event.clientY)
				case HoverControls.Pan => //nothing
				case _ =>
			}
		}
	}

	override def attach(el: HTMLElement): Unit = {
		super.attach(el)
		el.addEventListener("keyup", (this.onKeyUp _).asInstanceOf[Function[Event, _]], useCapture = false)
		el.addEventListener("keydown", (this.onKeyDown _).asInstanceOf[Function[Event, _]], useCapture = false)
	}
}
