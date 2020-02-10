package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Parcel
import android.os.Parcelable
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.IMode
import com.divyanshu.draw.widget.plugin.TextRect
import kotlin.math.abs
import kotlin.math.max

const val SELECT_THRESHOLD = 32
const val WIDTH_REDUCER = 32F
const val WIDTH_THRESHOLD = 16F
const val WIDTH_SCALE_MAX = 30

class TextMode(override val mode: DrawingMode) : IMode, Parcelable {
    private val textRect = TextRect()
    private val dashedPath = Path()

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

    private var widthScale = 0
    private var rectWidth = 200F
    private var rectHeight = 0F

    constructor(parcel: Parcel) : this(DrawingMode.valueOf(parcel.readString() ?: "")) {
        drawBorder = parcel.readByte() != 0.toByte()
        color = parcel.readInt()
        textSize = parcel.readFloat()
        text = parcel.readString()
        isInBound = parcel.readByte() != 0.toByte()
        curX = parcel.readFloat()
        curY = parcel.readFloat()
        difX = parcel.readFloat()
        difY = parcel.readFloat()
        pointerId = parcel.readInt()
        widthScale = parcel.readInt()
        rectWidth = parcel.readFloat()
        rectHeight = parcel.readFloat()
    }

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
        return x > (curX - SELECT_THRESHOLD) && x < (curX + rectWidth + SELECT_THRESHOLD) &&
                y > (curY - SELECT_THRESHOLD) && y < (curY + rectHeight + SELECT_THRESHOLD)
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
                rectWidth += WIDTH_REDUCER + 8
            }
        }
    }

    fun textWidthIncrease(paint: Paint) {
        val _scale = widthScale + 1
        when {
            abs(_scale) > WIDTH_SCALE_MAX -> return
            else -> {
                rectWidth += WIDTH_REDUCER
                ++widthScale
                updateTextDimension(paint)
            }
        }
    }

    fun textWidthDecrease(paint: Paint) {
        when {
            rectWidth - WIDTH_REDUCER < max(textSize, WIDTH_THRESHOLD) -> return
            else -> {
                rectWidth -= WIDTH_REDUCER
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mode.toString())
        parcel.writeByte(if (drawBorder) 1 else 0)
        parcel.writeInt(color)
        parcel.writeFloat(textSize)
        parcel.writeString(text ?: "")
        parcel.writeByte(if (isInBound) 1 else 0)
        parcel.writeFloat(curX)
        parcel.writeFloat(curY)
        parcel.writeFloat(difX)
        parcel.writeFloat(difY)
        parcel.writeInt(pointerId)
        parcel.writeInt(widthScale)
        parcel.writeFloat(rectWidth)
        parcel.writeFloat(rectHeight)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<TextMode> {
        override fun createFromParcel(parcel: Parcel) = TextMode(parcel)

        override fun newArray(size: Int): Array<TextMode?> = arrayOfNulls(size)
    }
}