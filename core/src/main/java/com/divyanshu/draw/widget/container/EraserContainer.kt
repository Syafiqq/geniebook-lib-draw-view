package com.divyanshu.draw.widget.container

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.MotionEvent
import com.divyanshu.draw.widget.contract.*
import com.divyanshu.draw.widget.mode.PathMode

class EraserContainer(override val context: Context, override val drawing: ICanvas) : IDrawingContainer<PathMode>, IPaint {
    override var draw: PathMode? = null

    private val listener: InteractionListener

    private var _strokeWidth = 0F

    private val paint = Paint()


    override var color = 0xFF
    override var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            _strokeWidth = value
            draw?.strokeWidth = value
        }
    override var alpha = 0x00
    override var textSize = 0F

    init {
        val ctx = this.context
        if (ctx !is InteractionListener) {
            throw ClassCastException("context must implement InteractionListener")
        }

        listener = ctx
        with(paint) {
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            maskFilter = null
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    override fun onDraw(canvas: Canvas, draw: IMode) {
        if (draw !is PathMode) return
        draw.onDraw(canvas, paint)
    }

    override fun onDraw(canvas: Canvas) {
        draw?.let { onDraw(canvas, it) }
    }

    override fun createDrawingObject(x: Float, y: Float, event: MotionEvent) {
        if (draw != null || event.pointerCount > 1) return

        attachDrawingTool()
        draw = PathMode(DrawingMode.ERASE).apply {
            color = this@EraserContainer.color
            strokeWidth = this@EraserContainer.strokeWidth
            onFingerDown(x, y)
        }
        drawing.requestInvalidate()
    }

    override fun attachDrawingTool() {
        listener.attachComponent(this)
    }

    override fun detachDrawingTool() {
        listener.detachComponent()
    }

    override fun destroyDrawingObject() {
        draw = null
        detachDrawingTool()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (draw == null) return true
        if (event.pointerCount > 1) {
            destroyDrawingObject()
            return true
        }

        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_MOVE -> draw?.onFingerMove(x, y)
            MotionEvent.ACTION_UP -> {
                draw?.onFingerUp(x, y)
                draw?.let(drawing::attachToCanvas)
            }
        }

        drawing.requestInvalidate()
        return true
    }

    interface InteractionListener {
        fun attachComponent(paint: IPaint)
        fun detachComponent()
    }
}