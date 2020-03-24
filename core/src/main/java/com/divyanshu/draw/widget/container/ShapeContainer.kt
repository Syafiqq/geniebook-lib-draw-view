package com.divyanshu.draw.widget.container

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.divyanshu.draw.widget.contract.*
import com.divyanshu.draw.widget.mode.*

sealed class ShapeContainer<T: SingleShapeMode>(override val context: Context, override val drawing: ICanvas) : IDrawingContainer<T>, IPaint {
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
            color = this@ShapeContainer.color
            strokeWidth = this@ShapeContainer.strokeWidth
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

    private fun resetPaint() {
        with(paint) {
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    interface InteractionListener {
        fun attachComponent(paint: IPaint)
        fun detachComponent()
    }
}

class ShapeLineContainer(override val context: Context, override val drawing: ICanvas) : ShapeContainer<LineMode>(context, drawing) {
    override fun instantiateDraw() = LineMode(DrawingMode.SHAPE_LINE)
}

class SingleHeadArrowContainer(override val context: Context, override val drawing: ICanvas) : ShapeContainer<SingleHeadArrowMode>(context, drawing) {
    override fun instantiateDraw() = SingleHeadArrowMode(DrawingMode.SHAPE_SINGLE_ARROW)
}

class DoubleHeadArrowContainer(override val context: Context, override val drawing: ICanvas) : ShapeContainer<DoubleHeadArrowMode>(context, drawing) {
    override fun instantiateDraw() = DoubleHeadArrowMode(DrawingMode.SHAPE_DOUBLE_ARROW)
}

class OutlineRectangleContainer(override val context: Context, override val drawing: ICanvas) : ShapeContainer<OutlineRectangleMode>(context, drawing) {
    override fun instantiateDraw() = OutlineRectangleMode(DrawingMode.SHAPE_OUTLINE_RECTANGLE)
}

class FilledRectangleContainer(override val context: Context, override val drawing: ICanvas) : ShapeContainer<FilledRectangleMode>(context, drawing) {
    override fun instantiateDraw() = FilledRectangleMode(DrawingMode.SHAPE_FILLED_RECTANGLE)
}

class OutlineEllipseContainer(override val context: Context, override val drawing: ICanvas) : ShapeContainer<OutlineEllipseMode>(context, drawing) {
    override fun instantiateDraw() = OutlineEllipseMode(DrawingMode.SHAPE_OUTLINE_ELLIPSE)
}

class FilledEllipseContainer(override val context: Context, override val drawing: ICanvas) : ShapeContainer<FilledEllipseMode>(context, drawing) {
    override fun instantiateDraw() = FilledEllipseMode(DrawingMode.SHAPE_FILLED_ELLIPSE)
}