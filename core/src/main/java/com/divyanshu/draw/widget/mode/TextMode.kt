package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.IMode
import com.divyanshu.draw.widget.plugin.TextRect
import kotlin.math.abs
import kotlin.math.max

class TextMode(override val mode: DrawingMode) : IMode {
    private val textRect = TextRect()
    private val dashedPath = Path()
    private val selThreshold = 32
    private val widthReducer = 32F
    private val widthThreshold = 16F

    var drawBorder = false

    var color = 0
    var textSize = 0F
        private set

    var text: String? = null
        private set

    private var isInBound = false

    private var curX = 0F
    private var curY = 0F
    private var difX = 0F
    private var difY = 0F
    private var pointerId = -1

    private var widthScale:Int = 0
    private val widthScaleMax = 30
    private var rectWidth = 200F
    private var rectHeight = 0F

    fun onFingerDown(x: Float, y: Float, pointer: Int) {
        isInBound = isInBound(x, y)
        if (isInBound) {
            updatePointer(pointer)
            diffPos(x, y)
        }
    }

    fun onFingerMove(x: Float, y: Float, pointer: Int) {
        if (isInBound) {
            if(pointer != pointerId) {
                updatePointer(pointer)
                diffPos(x, y)
            }
            currentPos(x + difX, y + difY)
        }
    }

    fun onFingerUp(x: Float, y: Float, pointer: Int) {
        isInBound = false
        updatePointer(-1)
    }

    private fun updatePointer(pointer: Int) {
        pointerId = pointer
    }

    fun currentPos(x: Float, y: Float) {
        curX = x
        curY = y
    }

    private fun diffPos(x: Float, y: Float) {
        difX = curX - x
        difY = curY - y
    }

    private fun isInBound(x: Float, y: Float): Boolean {
        return x > (curX - selThreshold) && x < (curX + rectWidth + selThreshold) &&
                y > (curY - selThreshold) && y < (curY + rectHeight + selThreshold)
    }

    private fun decorate(paint: Paint) {
        paint.textSize = textSize
        paint.color = color
    }

    fun updateText(text: String, paint: Paint) {
        this.text = text
        updateTextDimension(paint)
    }

    fun updateTextSize(textSize: Float, paint: Paint) {
        this.textSize = textSize
        updateTextDimension(paint)
    }

    fun initializeText(text: String, textSize: Float, textWidth: Float, paint: Paint) {
        this.text = text
        this.textSize = textSize
        decorate(paint)
        textRect.prepare(text, textWidth, Int.MAX_VALUE.toFloat(), paint)
        this.rectWidth = textRect.maximumWidth.toFloat()
        updateTextDimension(paint)
    }

    private fun updateTextDimension(paint: Paint) {
        text?.let {
            decorate(paint)
            while(true) {
                textRect.prepare(it, rectWidth, Int.MAX_VALUE.toFloat(), paint)
                val h = textRect.textHeight
                if(h!=0) {
                    rectHeight = h.toFloat()
                    break
                }
                rectWidth += widthReducer + 8
            }
        }
    }

    fun textWidthIncrease(paint: Paint) {
        val _scale = widthScale + 1
        when {
            abs(_scale) > widthScaleMax -> return
            else -> {
                rectWidth += widthReducer
                ++widthScale
                updateTextDimension(paint)
            }
        }
    }

    fun textWidthDecrease(paint: Paint) {
        when {
            rectWidth - widthReducer < max(textSize, widthThreshold) -> return
            else -> {
                rectWidth -= widthReducer
                --widthScale
                updateTextDimension(paint)
            }
        }
    }

    fun onDraw(canvas: Canvas, textPaint: Paint, borderPaint: Paint) {
        text?.let {
            updateTextDimension(textPaint)
            textRect.draw(canvas, curX, curY)
            if(drawBorder) {
                decorateBorder()
                canvas.drawPath(dashedPath, borderPaint)
            }
        }
    }

    private fun decorateBorder() {
        with(dashedPath) {
            reset()
            moveTo(curX, curY)
            lineTo(curX + rectWidth, curY)
            lineTo(curX + rectWidth,  curY + rectHeight)
            lineTo(curX, curY + rectHeight)
            lineTo(curX, curY)
            close()
        }
    }
}