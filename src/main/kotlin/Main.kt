package me.mars

//import arc.Core.graphics
import androidx.compose.ui.ComposeScene
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import arc.ApplicationListener
import arc.Core
import arc.backend.sdl.SdlApplication
import arc.backend.sdl.SdlConfig
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.SortedSpriteBatch
import arc.graphics.g2d.TextureAtlas
import arc.graphics.gl.FrameBuffer
import arc.graphics.gl.GLFrameBuffer
import arc.util.Log
import arc.util.Reflect
import arc.util.Threads
import org.jetbrains.skia.*
import org.jetbrains.skia.DirectContext
import org.jetbrains.skia.FramebufferFormat.Companion.GR_GL_RGBA8
import org.jetbrains.skiko.FrameDispatcher

fun main() {
    Log.info("Start")
//    Class.forName("sun.misc.Cleaner")
    val w = 640
    val h = 480
    val arcGlContext = GlContext()
    val skikoGlContext = GlContext()

    val config: SdlConfig = SdlConfig().apply {
        disableAudio = true
        decorated = true
        width = w
        height = h
    }
    SdlApplication(object : ApplicationListener {
        lateinit var framebuffer: FrameBuffer

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
            framebuffer = "Create fb" { FrameBuffer(w, h) }
//            framebuffer = Reflect.get(GLFrameBuffer::class.java, "")


//            framebuffer.begin()
            // DirectContext
            val contextProxy: io.github.humbleui.skija.DirectContext = "Create proxy" { io.github.humbleui.skija.DirectContext.makeGL() }
            val contextConstructor = DirectContext::class.java.getDeclaredConstructor(Long::class.java)
            context = "Construct context from $contextProxy" { contextConstructor.newInstance(contextProxy._ptr) }
            // DirectContext end
            // Surface creation
            surface = createSurface(w, h, framebuffer.getHandle(), context)
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
            Draw.color(arc.graphics.Color.red)
            Fill.rect(0f, 0f, 100f, 100f)
            Draw.flush()

            dispatcher.started = true

            arcGlContext.save()
            // Skiko rendering
            SharedBoolState.bound = true
//            framebuffer.begin()
            dispatcher.runAll()
            SharedBoolState.bound = false
//            framebuffer.end()
            // Skiko rendering end
            arcGlContext.restore()

            assert(!SharedBoolState.bound) { "shouldnt be bound" }
            Draw.rect(Draw.wrap(framebuffer.texture), 0f, 0f, w.toFloat(), h.toFloat())
            // The framedispatcher is responsible for keeping track if a frame should be dispatched and how it is dispatched
            // I am unsure what is responsible for scheduling frames however
            Threads.sleep(100)

        }

        fun render() {
            assert(SharedBoolState.bound)
            surface.canvas.clear(Color.CYAN)
            composeScene.constraints = Constraints(maxWidth = w, maxHeight =  h) // TODO: Figure out what the constraints are for
            composeScene.render(surface.canvas, System.nanoTime())
            context.flush()
        }

        override fun exit() {
            dispatcher.tasks.clear()
        }
    }, config)


}

fun createSurface(width: Int, height: Int, fboHandle: Int, context: DirectContext):Surface {
    val renderTarget = "Create render" { BackendRenderTarget.makeGL(width, height, 0, 8, fboHandle, GR_GL_RGBA8) }
    return "Create surface Context:$context Target: $renderTarget" {
        Surface.makeFromBackendRenderTarget(context, renderTarget, SurfaceOrigin.BOTTOM_LEFT, SurfaceColorFormat.RGBA_8888, ColorSpace.sRGB)!!
    }
}

infix operator fun <T: Any> String.invoke(fn: () -> T): T {
    Log.info("Log: $this")
    return fn()
}

fun FrameBuffer.getHandle(): Int {
    return Reflect.get(GLFrameBuffer::class.java, this, "framebufferHandle")
}

class SharedBoolState {
    companion object {
        var bound: Boolean = false
    }
}