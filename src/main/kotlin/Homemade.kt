package me.mars

import arc.backend.sdl.SdlApplication.SdlError
import arc.backend.sdl.SdlConfig
import arc.backend.sdl.jni.SDL.*
import arc.func.Intp
import arc.util.ArcNativesLoader
import arc.util.Log
import org.jetbrains.skia.DirectContext


fun main(args: Array<String>) {
    DirectContext.makeGL()
    val config = obtainConfig()
    // INIT
    ArcNativesLoader.load()
    SDL_Init(SDL_INIT_VIDEO or SDL_INIT_EVENTS)

    //set up openGL 2.0 profile
    check { SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, if (config.gl30) config.gl30Major else 2) }
    check {
        SDL_GL_SetAttribute(
            SDL_GL_CONTEXT_MINOR_VERSION,
            if (config.gl30) config.gl30Minor else 0
        )
    }
    check { SDL_GL_SetAttribute(SDL_GL_RED_SIZE, config.r) }
    check { SDL_GL_SetAttribute(SDL_GL_GREEN_SIZE, config.g) }
    check { SDL_GL_SetAttribute(SDL_GL_BLUE_SIZE, config.b) }
    check { SDL_GL_SetAttribute(SDL_GL_DEPTH_SIZE, config.depth) }
    check { SDL_GL_SetAttribute(SDL_GL_STENCIL_SIZE, config.stencil) }
    check { SDL_GL_SetAttribute(SDL_GL_DOUBLEBUFFER, 1) }

    var flags = SDL_WINDOW_OPENGL
    if (config.initialVisible) flags = flags or SDL_WINDOW_SHOWN
    if (!config.decorated) flags = flags or SDL_WINDOW_BORDERLESS
    if (config.resizable) flags = flags or SDL_WINDOW_RESIZABLE
    if (config.maximized) flags = flags or SDL_WINDOW_MAXIMIZED
    if (config.fullscreen) flags = flags or SDL_WINDOW_FULLSCREEN

    val window: WindowPtr = SDL_CreateWindow(config.title, config.width, config.height, flags)
    if (window == 0L) throw SdlError()

    val context: SdlContextPtr = SDL_GL_CreateContext(window)
    if (context == 0L) throw SdlError()

    if (config.vSyncEnabled) {
        SDL_GL_SetSwapInterval(1)
    }

    val ver = IntArray(3)
    SDL_GetVersion(ver)
    Log.info("[Core] Initialized SDL v@.@.@", ver[0], ver[1], ver[2])
}

fun obtainConfig(): SdlConfig {
    return SdlConfig().apply {

    }
}

// Ensure no error code
fun check(fn: Intp) {
    if (fn.get() != 0) throw SdlError()
}

// Type aliases for convenience
typealias WindowPtr = Long
typealias SdlContextPtr = Long