package com.divyanshu.draw.widget.container

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import com.divyanshu.draw.util.ImageUtil
import com.divyanshu.draw.widget.contract.*
import com.divyanshu.draw.widget.mode.ImageMode
import java.io.InputStream

class ImageContainer(override val context: Context, override val drawing: ICanvas) : IDrawingContainer<ImageMode>, IImageDrawCallback {
    override var draw: ImageMode? = null
    private val sizeThreshold = 1024

    private val listener: InteractionListener

    private val paint = Paint()
    private var isOnRequest = false

    init {
        val ctx = this.context
        if (ctx !is InteractionListener) {
            throw ClassCastException("context must implement InteractionListener")
        }

        listener = ctx
    }

    override fun onDraw(canvas: Canvas, draw: IMode) {
        if (draw !is ImageMode) return
        draw.onDraw(canvas, paint)
    }

    override fun onDraw(canvas: Canvas) {
        draw?.let { onDraw(canvas, it) }
    }

    override fun createDrawingObject(x: Float, y: Float, event: MotionEvent) {
        if (draw != null || event.pointerCount > 1) return

        createDrawingObject(x, y)
        listener.requestImage()
    }

    private fun createDrawingObject(x: Float, y: Float) {
        if(!isOnRequest) {
            isOnRequest = true
            attachDrawingTool()
            draw = ImageMode(DrawingMode.IMAGE).apply {
                currentPos(x, y)
            }
        }
    }

    override fun attachDrawingTool() {
        listener.attachComponent(this)
    }

    override fun detachDrawingTool() {
        listener.detachComponent()
    }

    override fun destroyDrawingObject() {
        draw = null
        isOnRequest = false
        detachDrawingTool()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (draw?.bitmap == null) return true
        if (event.pointerCount > 1) {
            return false
        }

        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> draw?.onFingerDown(x, y, event.getPointerId(0))
            MotionEvent.ACTION_MOVE -> draw?.onFingerMove(x, y, event.getPointerId(0))
            MotionEvent.ACTION_UP ->  draw?.onFingerUp(x, y, event.getPointerId(0))
        }

        drawing.requestInvalidate()
        return true
    }

    override fun createImageDirectly(image: InputStream, width: Float, height: Float) {
        if(isOnRequest) return

        if(draw == null) {
            val bitmap = ImageUtil.decodeSampledBitmapFromStream(image, sizeThreshold, sizeThreshold)
            bitmap?.let {
                val x = (width / 2f) - (it.width / 2f)
                val y = (height / 2f) - (it.height / 2f)

                createDrawingObject(x, y)
                draw?.run {
                    updateBitmapDirectly(bitmap)
                    drawing.requestInvalidate()
                }
            }
        }
    }

    override fun onImageRetrieved(image: InputStream) {
        draw?.run {
            val bitmap = ImageUtil.decodeSampledBitmapFromStream(image, sizeThreshold, sizeThreshold)
            bitmap?.let {
                updateBitmapDirectly(it)
                drawing.requestInvalidate()
            }
        }
    }

    override fun onApply() {
        draw?.run {
            drawing.attachToCanvas(this)
            drawing.requestInvalidate()
        }
    }

    override fun onCancel() {
        destroyDrawingObject()
        drawing.requestInvalidate()
    }

    override fun onScaleUp() {
        draw?.scaledUp()
        drawing.requestInvalidate()
    }

    override fun onScaleDown() {
        draw?.scaledDown()
        drawing.requestInvalidate()
    }

    interface InteractionListener {
        fun attachComponent(callback: IImageDrawCallback)
        fun requestImage()
        fun detachComponent()
    }
}