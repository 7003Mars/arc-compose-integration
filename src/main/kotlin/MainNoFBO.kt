package me.mars

//import arc.Core.graphics
import androidx.compose.ui.ComposeScene
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import arc.ApplicationListener
import arc.Core
import arc.backend.sdl.SdlApplication
import arc.backend.sdl.SdlConfig
import arc.files.Fi
import arc.graphics.Gl
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.SortedSpriteBatch
import arc.graphics.g2d.TextureAtlas
import arc.util.Log
import arc.util.ScreenUtils
import arc.util.Threads
import org.jetbrains.skia.Color
import org.jetbrains.skia.DirectContext
import org.jetbrains.skia.Surface
import org.jetbrains.skiko.FrameDispatcher

fun main() {
    Log.info("Start: FBOLESS")
//    Class.forName("sun.misc.Cleaner")
    val w = 640
    val h = 480
    var frames: Int = 0

    val config: SdlConfig = SdlConfig().apply {
        disableAudio = true
        decorated = true
        width = w
        height = h
    }
    SdlApplication(object : ApplicationListener {
        lateinit var context: DirectContext
        lateinit var surface: Surface
        lateinit var dispatcher: AppCoroutineDispatcher
        lateinit var composeScene: ComposeScene


        override fun init() {
            // Arc init
            Core.batch = SortedSpriteBatch()
            Core.atlas = TextureAtlas.blankAtlas()
            // Arc init end
            // Framebuffer creation
            val fboHandle = Gl.getInt(Gl.framebufferBinding)
            println("FBo handle is $fboHandle")


//            framebuffer.begin()
            // DirectContext
            val contextProxy: io.github.humbleui.skija.DirectContext = "Create proxy" { io.github.humbleui.skija.DirectContext.makeGL() }
            val contextConstructor = DirectContext::class.java.getDeclaredConstructor(Long::class.java)
            context = "Construct context from $contextProxy" { contextConstructor.newInstance(contextProxy._ptr) }
            // DirectContext end
            // Surface creation
            surface = createSurface(w, h, fboHandle, context)
            // Surface creation end
//            framebuffer.end()
            // Coroutine dispatcher
            dispatcher = AppCoroutineDispatcher()
            // Coroutine dispatcher end
            // Window close callback implemented in exit()
            // render() implemented as object method
            // Frame dispatcher
            val frameDispatcher = FrameDispatcher(this.dispatcher) { render() }
            // Frame dispatcher end
            // Compose scene
            val density = Density(1f) // NOt exactly sure where to find this in sdl
            composeScene = ComposeScene(this.dispatcher, density, invalidate = frameDispatcher::scheduleFrame)
            // Compose scene end
            // TODO: Window resize callback
            // TODO: Compose event subscription
            // Set content
            composeScene.setContent { App() }
            // Set content end
        }

        override fun update() {
            Fill.rect(0f, 0f, 100f, 100f)
            dispatcher.started = true
            dispatcher.runAll()
            Fill.rect(0f, 0f, 100f, 100f)
            Draw.flush()
            assert(!SharedBoolState.bound) { "shouldnt be bound" }
//            Draw.rect(Draw.wrap(framebuffer.texture), 0f, 0f, w.toFloat(), h.toFloat())
            // The framedispatcher is responsible for keeping track if a frame should be dispatched and how it is dispatched
            // I am unsure what is responsible for scheduling frames however
//            Draw.flush()
            Threads.sleep(100)

        }

        fun render() {
            Log.info("Rendering")
            assert(SharedBoolState.bound)
            surface.canvas.clear(Color.CYAN)
            composeScene.constraints = Constraints(maxWidth = w, maxHeight =  h) // TODO: Figure out what the constraints are for
            composeScene.render(surface.canvas, System.nanoTime())
            context.flush()
            ScreenUtils.saveScreenshot(Fi("buf-${++frames}.png"))
//            SDL.SDL_GL_SwapWindow((Core.app as SdlApplication).window)
//            Threads.sleep(1000)
        }

        override fun exit() {
            dispatcher.tasks.clear()
        }
    }, config)


}