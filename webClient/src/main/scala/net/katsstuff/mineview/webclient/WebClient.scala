package net.katsstuff.mineview.webclient

import scala.scalajs.js.Dynamic.{global => g, literal => lit, newInstance => jsnew}
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.{document, window}

import net.katsstuff.mineview.webclient.game.Registry
import net.katsstuff.mineview.webclient.impl.block.blocks.BlockAir
import net.katsstuff.mineview.webclient.impl.world.biomes.BiomePlains
import net.katsstuff.mineview.webclient.render.MineViewScene

object WebClient extends JSApp {

	@JSExport
	override def main(): Unit = {
		val registry = new Registry
		val air = new BlockAir
		val blocks = Seq(
			0 -> air
		)

		val plains = new BiomePlains
		val biomes = Seq(
			1.toByte -> plains
		)

		registry.setDefaultBlock(air)
		blocks.foreach{ case (id, block) => registry.registerBlock(id, block) }

		registry.setDefaultBiome(plains)
		biomes.foreach{ case (id, biome) => registry.registerBiome(id, biome)}

		val container = document.createElement("div")
		document.body.appendChild(container)

		val mineViewScene = new MineViewScene(container.asInstanceOf[HTMLElement], window.innerWidth, window.innerHeight)
		mineViewScene.initScene()
		mineViewScene.render()
	}
}

