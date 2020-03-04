package com.divyanshu.draw.widget.container

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.divyanshu.draw.widget.contract.*
import com.divyanshu.draw.widget.mode.TextMode

class TextContainer(override val context: Context, override val drawing: ICanvas) : IDrawingContainer<TextMode>, IPaint, ITextDrawCallback {
    override var draw: TextMode? = null

    private val listener: InteractionListener

    private var _color = 0
    private var _alpha = 0
    private var _textSize = 0F

    private val drawPaint = Paint()
    private val borderPaint = Paint().apply {
        setARGB(255, 0,0 ,0)
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(5f, 10f),0f)
    }

    override var color: Int
        get() = _color
        set(value) {
            @ColorInt
            val alphaColor = ColorUtils.setAlphaComponent(value, alpha)
            _color = alphaColor
            draw?.color = alphaColor
            drawing.requestInvalidate()
        }
    override var strokeWidth = 0F
    override var alpha: Int
        get() = _alpha
        set(value) {
            _alpha = (value * 255) / 100
            color = color
        }
    override var textSize: Float
        get() = _textSize
        set(value) {
            _textSize = value
            draw?.updateTextSize(_textSize, drawPaint)
        }

    init {
        val ctx = this.context
        if (ctx !is InteractionListener) {
            throw ClassCastException("context must implement InteractionListener")
        }

        listener = ctx
    }

    override fun onDraw(canvas: Canvas, draw: IMode) {
        if (draw !is TextMode) return
        draw.onDraw(canvas, drawPaint, borderPaint)
    }

    override fun onDraw(canvas: Canvas) {
        draw?.let { onDraw(canvas, it) }
    }

    override fun createDrawingObject(x: Float, y: Float, event: MotionEvent) {
        if (draw != null || event.pointerCount > 1) return

        attachDrawingTool()
        draw = TextMode(DrawingMode.TEXT).apply {
            color = this@TextContainer.color
            drawBorder = true
            updateTextSize(this@TextContainer.textSize, drawPaint)
            currentPos(x, y)
        }
        listener.requestText()
    }

    override fun assignDraw(draw: IMode, canvas: ICanvas) {
        if (draw !is TextMode || canvas != drawing) return

        attachDrawingTool()
        this.draw = draw
        drawing.requestInvalidate()
        if(draw.text == null) {
            listener.requestText()
        } else {
            listener.showCustomTool()
        }
    }

    override fun destroyDrawingObject() {
        draw?.drawBorder = false
        draw = null
        detachDrawingTool()
    }

    override fun attachDrawingTool() {
        listener.attachComponent(this, this)
    }

    override fun detachDrawingTool() {
        listener.detachComponent()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (draw?.text == null) return true
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

    override fun onTextRetrieved(text: String, textSize: Float, textWidth: Float) {
        draw?.run {
            initializeText(text, textSize, textWidth, drawPaint)
            drawing.requestInvalidate()
        }
        listener.showCustomTool()
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

    override fun onTextSizeChanged(textSize: Float) {
        draw?.updateTextSize(textSize, drawPaint)
        drawing.requestInvalidate()
    }

    override fun onTextWidthIncrease() {
        draw?.textWidthIncrease(drawPaint)
        drawing.requestInvalidate()
    }

    override fun onTextWidthDecrease() {
        draw?.textWidthDecrease(drawPaint)
        drawing.requestInvalidate()
    }

    interface InteractionListener {
        fun attachComponent(paint: IPaint, callback: ITextDrawCallback)
        fun requestText()
        fun showCustomTool()
        fun detachComponent()
    }
}