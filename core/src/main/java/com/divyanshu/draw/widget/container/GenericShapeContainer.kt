package com.divyanshu.draw.widget.container

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.divyanshu.draw.widget.contract.ICanvas
import com.divyanshu.draw.widget.contract.IDrawingContainer
import com.divyanshu.draw.widget.contract.IMode
import com.divyanshu.draw.widget.contract.IPaint
import com.divyanshu.draw.widget.mode.SingleShapeMode

abstract class GenericShapeContainer<T: SingleShapeMode>(override val context: Context, override val drawing: ICanvas) : IDrawingContainer<T>, IPaint {
    override var draw: T? = null

    private val listener by lazy<InteractionListener> {
        val ctx = context
        if (ctx !is InteractionListener) {
            throw ClassCastException("context must implement InteractionListener")
        }

        ctx
    }

    private var _color = 0
    private var _strokeWidth = 0F
    private var _alpha = 0

    private val paint = Paint()

    override var color: Int
        get() = _color
        set(value) {
            @ColorInt
            val alphaColor = ColorUtils.setAlphaComponent(value, alpha)
            _color = alphaColor
            draw?.color = alphaColor
        }
    override var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            _strokeWidth = value
            draw?.strokeWidth = value
        }
    override var alpha: Int
        get() = _alpha
        set(value) {
            _alpha = (value * 255) / 100
            color = color
        }
    override var textSize = 0F

    init {
        resetPaint()
    }

    private fun resetPaint() {
        with(paint) {
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onDraw(canvas: Canvas, draw: IMode) {
        if (draw !is SingleShapeMode) return
        draw.onDraw(canvas, paint)
    }

    override fun onDraw(canvas: Canvas) {
        draw?.let { onDraw(canvas, it) }
    }

    override fun createDrawingObject(x: Float, y: Float, event: MotionEvent) {
        if (draw != null || event.pointerCount > 1) return

        resetPaint()
        attachDrawingTool()
        draw = instantiateDraw().apply {
            color = this@GenericShapeContainer.color
            strokeWidth = this@GenericShapeContainer.strokeWidth
            onFingerDown(x, y)
        }
        drawing.requestInvalidate()
    }

    abstract fun instantiateDraw(): T

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

    override fun attachDrawingTool() = listener.attachComponent(this)

    override fun detachDrawingTool() = listener.detachComponent()

    interface InteractionListener {
        fun attachComponent(paint: IPaint)
        fun detachComponent()
    }
}