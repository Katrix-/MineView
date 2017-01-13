package net.katsstuff.mineview.webclient.render

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g, literal => lit, newInstance => jsnew}

import org.denigma.threejs.{ArrowHelper, AxisHelper, BoxGeometry, Camera, CameraHelper, CircleGeometry, CylinderGeometry, DirectionalLight, Geometry, HemisphereLight, IcosahedronGeometry, LatheGeometry, Mesh, MeshLambertMaterial, Object3D, OctahedronGeometry, OrthographicCamera, PlaneGeometry, RingGeometry, ShadowMapType, Side, SphereGeometry, TetrahedronGeometry, Texture, TextureLoader, TorusGeometry, TorusKnotGeometry, Vector3, WebGLRenderer, Wrapping}
import org.scalajs.dom.raw.HTMLElement

import net.katsstuff.mineview.webclient.helper.DynamicHelper._

class MineViewScene(val container: HTMLElement, val width: Double, val height: Double) extends Scene3D {

	override type UsedCamera = OrthographicCamera
	override type UsedControls = MineViewControls

	override protected def initCamera: UsedCamera = {
		val usedCamera = new OrthographicCamera(width / -2, width / 2, height / 2, height / -2, -500, 1000)
		usedCamera.position.y = 400
		usedCamera
	}

	override protected def initRenderer: WebGLRenderer = {
		val renderer = super.initRenderer
		renderer.dyn.setClearColor(0xAAF2FF)
		renderer.dyn.shadowMap.enabled = true
		renderer.dyn.shadowMap.`type` = g.THREE.PCFSoftShadowMap.asInstanceOf[ShadowMapType]

		renderer
	}

	override def initScene(): Unit = {
		super.initScene()

		scene.add(new HemisphereLight(0xAAF2FF, 0x7F5247))

		val size = 400D

		val dirLight = new DirectionalLight(0xFFEEA7, 1)
		dirLight.position.set(0D, 500D, 500D)
		dirLight.target.position.set(0, 0, 0)
		dirLight.castShadow = true
		dirLight.dyn.shadow.camera.near = 100D
		dirLight.dyn.shadow.camera.far = 1000D
		dirLight.dyn.shadow.camera.left = -size
		dirLight.dyn.shadow.camera.right = size
		dirLight.dyn.shadow.camera.top = -size
		dirLight.dyn.shadow.camera.bottom = size
		dirLight.dyn.shadow.mapSize.height = 1024
		dirLight.dyn.shadow.mapSize.width = 1024
		dirLight.dyn.shadow.bias = -0.0001
		scene.add(dirLight)

		val helper = new CameraHelper(dirLight.dyn.shadow.camera.asInstanceOf[Camera])
		scene.add(helper)

		addMesh(new PlaneGeometry(1200, 600, 4, 4), new Vector3(0D, -100D, 0D)).rotateX(Math.PI / 2).receiveShadow = true

		addMesh(new SphereGeometry(75, 20, 10), new Vector3(-400D, 0D, 200D))
		addMesh(new IcosahedronGeometry(75, 1), new Vector3(-200D, 0D, 200D))
		addMesh(new OctahedronGeometry(75, 2), new Vector3(0D, 0D, 200D))
		addMesh(new TetrahedronGeometry(75, 0), new Vector3(200D, 0D, 200D))
		addMesh(new PlaneGeometry(100, 100, 4, 4), new Vector3(-400D, 0D, 0D))
		addMesh(new BoxGeometry(100, 100, 100, 4, 4, 4), new Vector3(-200D, 0D, 0D))
		addMesh(new CircleGeometry(50, 20, 0, js.Math.PI * 2), new Vector3(0D, 0D, 0D))
		addMesh(new RingGeometry(10, 50, 20, 5, 0, js.Math.PI * 2), new Vector3(200D, 0D, 0D))
		addMesh(new CylinderGeometry(25, 75, 100, 40, 4), new Vector3(400D, 0D, 0D))

		import js.JSConverters.genTravConvertible2JSRichGenTrav

		val points = (for(i <- 0 until 50) yield {
			new Vector3(js.Math.sin(i * 0.2D) * js.Math.sin(i * 0.1D) * 15 + 50, (i - 5) * 2)
		}).toJSArray

		addMesh(new LatheGeometry(points, 20), new Vector3(-400D, 0D, -200D))
		addMesh(new TorusGeometry(50, 20, 20, 20), new Vector3(-200D, 0D, -200D))
		addMesh(new TorusKnotGeometry(50, 10, 50, 20), new Vector3(0D, 0D, -200D))
		addObject(new AxisHelper(50), new Vector3(200D, 0D, -200D))
		addObject(new ArrowHelper(new Vector3(0D, 1D, 0D), new Vector3(0D, 0D, 0D), 50), new Vector3(400D, 0D, -200D))
	}

	private val map = new TextureLoader().dyn.load("libs/textures/UV_Grid_Sm.jpg").asInstanceOf[Texture] //Why can't I get the texture there?
	//Taking stuff from the THREE package object doesn't work
	map.wrapS = g.THREE.RepeatWrapping.asInstanceOf[Wrapping]
	map.wrapT = g.THREE.RepeatWrapping.asInstanceOf[Wrapping]
	map.anisotropy = 16

	private val material = new MeshLambertMaterial()
	material.map = map
	material.side = g.THREE.DoubleSide.asInstanceOf[Side]

	def addMesh(geometry: Geometry, pos: Vector3): Mesh = addObject(new Mesh(geometry, material), pos)

	def addObject(obj: Object3D, pos: Vector3): obj.type = {
		obj.position.set(pos.x, pos.y, pos.z)
		scene.add(obj)
		obj.castShadow = true
		obj.receiveShadow = true
		obj
	}

	override protected def initControls: UsedControls = new MineViewControls(camera, container)
}