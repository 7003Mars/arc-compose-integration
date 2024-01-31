package me.mars

import arc.backend.sdl.jni.SDLGL.*
import arc.graphics.GL20.*
import arc.graphics.Gl.getInt
import arc.graphics.Gl.getVertexAttribiv
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Code from https://github.com/semoro/MCCompose/blob/main/src/main/java/com/example/examplemod/MyGui.kt
class GlContext {
    companion object {
        val buf = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder())

        fun checkGL() {

        }
    }

    var shader = 0
    var arrayBuffer = 0
    var activeTexture = 0
    var bindTexture = 0
    var bindSampler = 0
    var enableScissor = false

    val scissorBox = ByteBuffer.allocateDirect(4 * 16).order(ByteOrder.nativeOrder()).asIntBuffer()
    val enableVertexAttribArray: IntArray by lazy { IntArray(getInt(GL_MAX_VERTEX_ATTRIBS)) }

    fun save() {
        this.shader = getInt(GL_CURRENT_PROGRAM)
        this.arrayBuffer = getInt(GL_ARRAY_BUFFER_BINDING)
        this.activeTexture = getInt(GL_ACTIVE_TEXTURE)
        this.bindTexture = getInt(GL_TEXTURE_BINDING_2D)
//        this.enableScissor = getBooleanv(GL_SCISSOR_TEST)
//        if (this.enableScissor) {
//            getInt(GL_SCISSOR_BOX, this.scissorBox.apply { clear() })
//        }
//        this.bindSampler = getInt(GL_SAMPLER_BINDING)
//
//        checkGL()
//
        for (index in this.enableVertexAttribArray.indices) {
            getVertexAttribiv(index, GL_VERTEX_ATTRIB_ARRAY_ENABLED, buf.apply { clear() }.asIntBuffer())
            this.enableVertexAttribArray[index] = buf.getInt()
        }
    }

    fun restore() {
        for (index in this.enableVertexAttribArray.indices) {
            val v = this.enableVertexAttribArray[index]
            if (v == GL_FALSE)
                glDisableVertexAttribArray(index)
            else
                glEnableVertexAttribArray(index)
        }

        checkGL()


        glBindBuffer(GL_ARRAY_BUFFER, this.arrayBuffer)
        checkGL()
        glUseProgram(this.shader)
        checkGL()
        if (this.activeTexture != 0) glActiveTexture(this.activeTexture)
        checkGL()
        glBindTexture(GL_TEXTURE_2D, this.bindTexture)
        checkGL()

//        if (this.enableScissor) {
//            glEnable(GL_SCISSOR_TEST)
//            glScissor(this.scissorBox[0], this.scissorBox[1], this.scissorBox[2], this.scissorBox[3])
//        } else {
//            glDisable(GL_SCISSOR_TEST)
//        }
        checkGL()

        if (this.activeTexture != 0) glBindSampler(this.activeTexture - GL_TEXTURE0, this.bindSampler)

        checkGL()
    }
}