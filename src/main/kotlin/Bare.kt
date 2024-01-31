package me.mars

import arc.ApplicationListener
import arc.Core
import arc.backend.sdl.SdlApplication
import arc.backend.sdl.SdlConfig
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.SortedSpriteBatch
import arc.graphics.g2d.TextureAtlas
import arc.util.Log

fun main() {
    Log.info("Start")
    val w = 640
    val h = 480

    val config: SdlConfig = SdlConfig().apply {
        disableAudio = true
        decorated = true
        width = w
        height = h
    }

    SdlApplication(object : ApplicationListener {
        override fun init() {
            Core.batch = SortedSpriteBatch()
            Core.atlas = TextureAtlas.blankAtlas()
        }

        override fun update() {
            Draw.color(Color.cyan)
            Fill.rect(25f, 25f, 50f, 50f)
            Draw.flush()
        }
    }, config)
}