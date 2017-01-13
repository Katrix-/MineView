package net.katsstuff.mineview.webclient.render

import org.denigma.threejs.extensions.controls.CameraControls
import org.scalajs.dom.KeyboardEvent

trait KeyboardCameraControls extends CameraControls {

	def onKeyDown(event: KeyboardEvent): Unit
	def onKeyUp(event: KeyboardEvent): Unit

}
