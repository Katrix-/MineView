package net.katsstuff.mineview.webclient.render.model

import derive.key
import net.katsstuff.mineview.shared.DomainName

trait Mergeable[A <: Mergeable[A]] {

	def merge(parent: A): A

	def mergeOption[B <: Mergeable[B]](thisValue: Option[B], parentValue: Option[B]): Option[B] = {
		thisValue.map(thisSome => parentValue.map(parentSome => thisSome.merge(parentSome))).getOrElse(parentValue)
	}
}

case class JSONModel(
		parent: Option[DomainName],
		@key("ambientocclusion") ambientOcclusion: Boolean = true,
		display: Option[TopDisplay],
		textures: Option[Textures],
		elements: Seq[Element] = Seq()
) extends Mergeable[JSONModel] {

	def merge(parent: JSONModel): JSONModel = JSONModel(
		this.parent,
		if(!ambientOcclusion) !ambientOcclusion
		else parent.ambientOcclusion,
		mergeOption(display, parent.display),
		mergeOption(textures, parent.textures),
		if(elements.isEmpty) parent.elements
		else elements
	)
}

case class Display(
		rotation: Vec3,
		translation: Vec3,
		scale: Vec3
) extends Mergeable[Display] {

	def merge(parent: Display): Display = this
}

case class TopDisplay(
		@key("thirdperson_righthand") thirdPersonRightHand: Option[Display] = None,
		@key("thirdperson_lefthand") thirdPersonLeftHand: Option[Display] = None,
		@key("firstperson_righthand") firstPersonRightHand: Option[Display] = None,
		@key("firstperson_lefthand") firstPersonLeftHand: Option[Display] = None,
		gui: Option[Display] = None,
		head: Option[Display] = None,
		ground: Option[Display] = None,
		fixed: Option[Display] = None
) extends Mergeable[TopDisplay] {

	def merge(parent: TopDisplay): TopDisplay = TopDisplay(
		mergeOption(thirdPersonRightHand, parent.thirdPersonRightHand),
		mergeOption(thirdPersonLeftHand, parent.thirdPersonLeftHand),
		mergeOption(firstPersonRightHand, parent.firstPersonRightHand),
		mergeOption(firstPersonLeftHand, parent.firstPersonLeftHand),
		mergeOption(gui, parent.gui),
		mergeOption(head, parent.head),
		mergeOption(ground, parent.ground),
		mergeOption(fixed, parent.fixed)
	)
}

case class Textures(
		particle: Option[TextureOrVariable],
		variables: Seq[TextureVariableDefinition] = Seq()
) extends Mergeable[Textures] {

	override def merge(parent: Textures): Textures = Textures(
		mergeOption(particle, parent.particle),
		variables ++ parent.variables
	)
}

sealed trait TextureOrVariable extends Mergeable[TextureOrVariable]
object TextureOrVariable {

	import OptionPickler._

	implicit val readWriter: ReadWriter[TextureOrVariable] = macroRW[Texture] merge macroRW[TextureVariable]
}

case class Texture(texture: DomainName) extends TextureOrVariable {
	override def merge(parent: TextureOrVariable): TextureOrVariable = this
}
case class TextureVariable(value: String) extends TextureOrVariable {
	override def merge(parent: TextureOrVariable): TextureOrVariable = this
}

case class TextureVariableDefinition(
		name: String,
		value: String
)

case class Element(
		from: Vec3,
		to: Vec3,
		rotation: Option[Rotation],
		shade: Boolean = true,
		faces: Faces
)

object Element {

	//Due to the fact that some values in Face hae defaults that relies on stuff all the way up here, we need a custom serializer here
	import OptionPickler.{Reader, Writer}
	import upickle.{Invalid, Js}

	implicit val elementWriter: OptionPickler.Writer[Element] = Writer[Element] {
		case Element(from, to, rotation, shade, faces) =>
			val vec3Serializer = implicitly[Writer[Vec3]]
			val rotationSerializer = implicitly[Writer[Option[Rotation]]]

			def writeFace(face: Option[Face], from: Vec3, to: Vec3, position: Position): Js.Value = face match {
				case Some(Face(uv, texture, cullFace, rotationTexture, tintIndex)) =>
					val uvSerializer = implicitly[Writer[UVCoord]]
					val positionSerializer = implicitly[Writer[Position]]
					val tintIndexSerializer = implicitly[Writer[Option[Int]]]

					Js.Obj(
						"uv" -> {
							if(???) uvSerializer.write(uv)
							else Js.Null
						},
						"texture" -> Js.Str(texture.value),
						"cullface" -> {
							if(cullFace == position) Js.Null
							else positionSerializer.write(cullFace)
						},
						"rotation" -> {
							if(rotationTexture == 0) Js.Null
							else Js.Num(rotationTexture)
						},
						"tintindex" -> tintIndexSerializer.write(tintIndex)
					)
				case None => Js.Null
			}

			Js.Obj(
				"from" -> vec3Serializer.write(from),
				"to" -> vec3Serializer.write(to),
				"rotation" -> rotationSerializer.write(rotation),
				"shade" -> {
					if(!shade) Js.False
					else Js.Null
				},
				"faces" -> Js.Obj(
					"down" -> writeFace(faces.down, from, to, Position.Down),
					"up" -> writeFace(faces.up, from, to, Position.Up),
					"north" -> writeFace(faces.north, from, to, Position.North),
					"south" -> writeFace(faces.south, from, to, Position.South),
					"west" -> writeFace(faces.west, from, to, Position.West),
					"east" -> writeFace(faces.east, from, to, Position.East)
				)
			)
	}

	implicit val elementReader: OptionPickler.Reader[Element] = Reader[Element] {
		case jsObj@Js.Obj(value@_*) =>
			val vec3Serializer = implicitly[Reader[Vec3]]
			val rotationSerializer = implicitly[Reader[Option[Rotation]]]
			val valueMap = value.toMap

			def getOrThrow(key: String): Js.Value = valueMap.getOrElse(key, throw Invalid.Data(jsObj, s"$key value for Element not found"))

			def getFace(js: Js.Value, from: Vec3, to: Vec3, position: Position): Option[Face] = {
				js match {
					case Js.Null => None
					case faceJsObj@Js.Obj(faceValue@_*) =>
						val uvSerializer = implicitly[Reader[UVCoord]]
						val positionSerializer = implicitly[Reader[Position]]
						val tintIndexSerializer = implicitly[Reader[Option[Int]]]
						val faceValueMap = faceValue.toMap

						def getOrThrowFace(key: String): Js.Value = faceValueMap.getOrElse(key, throw Invalid.Data(jsObj, s"$key value for Face not found"))

						val uv = faceValueMap.get("uv").map(uvSerializer.read).getOrElse(???)
						val textures = faceValueMap.get("texture").map {
							case Js.Str(str) => TextureVariable(str)
							case invalid => throw Invalid.Data(invalid, "Invalid type of data when deserializing Face")
						}.getOrElse(throw Invalid.Data(faceJsObj, "Missing value for texture on Face"))
						val cullFace = faceValueMap.get("cullface").map(positionSerializer.read).getOrElse(position)
						val textureRotation = faceValueMap.get("rotation").map {
							case Js.Num(num) => num.toInt
							case Js.Null => 0
							case invalid => throw Invalid.Data(invalid, "Invalid type of data when deserializing Face")
						}.getOrElse(0)
						val tintIndex = tintIndexSerializer.read(getOrThrowFace("tintindex"))
						Some(Face(uv, textures, cullFace, textureRotation, tintIndex))
					case invalid => throw Invalid.Data(invalid, "Tried to deserialize face, but got wrong data")
				}
			}

			val from = vec3Serializer.read(getOrThrow("from"))
			val to = vec3Serializer.read(getOrThrow("to"))
			val rotation = rotationSerializer.read(getOrThrow("rotation"))
			val shade = valueMap.get("shade").exists {
				case Js.True => true
				case Js.False => false
				case _ => throw Invalid.Data(jsObj, "The shade value for Element was the wrong type")
			}

			val facesRaw = getOrThrow("faces").obj

			val faces = Faces(
				facesRaw.get("down").flatMap(getFace(_, from, to, Position.Down)),
				facesRaw.get("up").flatMap(getFace(_, from, to, Position.Up)),
				facesRaw.get("north").flatMap(getFace(_, from, to, Position.North)),
				facesRaw.get("south").flatMap(getFace(_, from, to, Position.South)),
				facesRaw.get("west").flatMap(getFace(_, from, to, Position.West)),
				facesRaw.get("east").flatMap(getFace(_, from, to, Position.East))
			)

			Element(from, to, rotation, shade, faces)
	}
}

case class Rotation(
		origin: Vec3 = Vec3(8, 8, 8),
		axis: Axis,
		angle: Double = 0,
		rescale: Boolean = false
)

sealed trait Axis
object Axis {

	case object X extends Axis
	case object Y extends Axis
	case object Z extends Axis

	import OptionPickler._

	implicit val readWriter: ReadWriter[Axis] = macroRW[X.type] merge macroRW[Y.type] merge macroRW[Z.type]
}

case class Faces(
		down: Option[Face],
		up: Option[Face],
		north: Option[Face],
		south: Option[Face],
		west: Option[Face],
		east: Option[Face]
)

case class Face(
		uv: UVCoord,
		texture: TextureVariable,
		@key("cullface") cullFace: Position,
		rotation: Int = 0,
		@key("tintindex") tintIndex: Option[Int] = None
)

sealed trait Position
object Position {

	case object Down extends Position
	case object Up extends Position
	case object North extends Position
	case object South extends Position
	case object West extends Position
	case object East extends Position

	import OptionPickler._

	implicit val readWriter: ReadWriter[Position] = {
		macroRW[Down.type] merge macroRW[Up.type] merge macroRW[North.type] merge macroRW[South.type] merge macroRW[West.type] merge macroRW[East.type]
	}
}

case class Vec3(x: Double, y: Double, z: Double)
object Vec3 {

	import upickle.Js

	implicit val vec3dReader: OptionPickler.Reader[Vec3] = OptionPickler.Reader[Vec3] {
		case Js.Arr(Js.Num(x), Js.Num(y), Js.Num(z)) => Vec3(x, y, z)
	}

	implicit val vec3dWriter: upickle.default.Writer[Vec3] = upickle.default.Writer[Vec3] {
		vec => Js.Arr(Js.Num(vec.x), Js.Num(vec.y), Js.Num(vec.z))
	}
}

case class UVCoord(x1: Int, y1: Int, x2: Int, y2: Int)
object UVCoord {

	import upickle.Js

	implicit val uvReader: OptionPickler.Reader[UVCoord] = OptionPickler.Reader[UVCoord] {
		case Js.Arr(Js.Num(x1), Js.Num(y1), Js.Num(x2), Js.Num(y2)) => UVCoord(x1.toInt, y1.toInt, x2.toInt, y2.toInt)
	}

	implicit val uvWriter: OptionPickler.Writer[UVCoord] = OptionPickler.Writer[UVCoord] {
		uv => Js.Arr(Js.Num(uv.x1), Js.Num(uv.y1), Js.Num(uv.x2), Js.Num(uv.y2))
	}
}