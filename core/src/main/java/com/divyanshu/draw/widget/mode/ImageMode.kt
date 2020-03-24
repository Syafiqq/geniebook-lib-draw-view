package com.divyanshu.draw.widget.mode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.IMode
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

private const val SELECT_THRESHOLD = 32
private const val SCALE_SIZE = 64
private const val SCALE_MAX = 10

class ImageMode(override val mode: DrawingMode) : IMode {
    var bitmap: Bitmap? = null
    private var rectScaled: Rect = Rect()

    private var scale: Int = 0
    private var isInBound = false

    private var curX = 0F
    private var curY = 0F
    private var difX = 0
    private var difY = 0
    private var scaledX = 0
    private var scaledY = 0
    private var pointerId = -1

    constructor(parcel: Parcel) : this(DrawingMode.valueOf(parcel.readString() ?: "")) {
        bitmap = parcel.readParcelable(Bitmap::class.java.classLoader)
        (parcel.readParcelable(Rect::class.java.classLoader) as Rect?)?.let {
            with(rectScaled) {
                top = it.top
                bottom = it.bottom
                left = it.left
                right = it.right
            }
        }
        scale = parcel.readInt()
        isInBound = parcel.readByte() != 0.toByte()
        curX = parcel.readFloat()
        curY = parcel.readFloat()
        difX = parcel.readInt()
        difY = parcel.readInt()
        scaledX = parcel.readInt()
        scaledY = parcel.readInt()
        pointerId = parcel.readInt()
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
            currentPos(x, y)
        }
    }

    fun onFingerUp(x: Float, y: Float, pointer: Int) {
        isInBound = false
        updatePointer(-1)
    }

    fun currentPos(x: Float, y: Float) {
        curX = x
        curY = y
        updateRectScale()
    }

    private fun updatePointer(pointer: Int) {
        pointerId = pointer
    }

    private fun diffPos(x: Float, y: Float) {
        difX = (x - rectScaled.left).roundToInt()
        difY = (y - rectScaled.top).roundToInt()
    }

    private fun isInBound(x: Float, y: Float): Boolean {
        val r = rectScaled

        return x > (r.left - SELECT_THRESHOLD) && x < (r.right + SELECT_THRESHOLD) &&
                y > (r.top - SELECT_THRESHOLD) && y < (r.bottom + SELECT_THRESHOLD)
    }

    fun updateBitmapDirectly(image: Bitmap) {
        bitmap = image
        updateRectScale()
    }

    private fun updateRectScale() {
        if (bitmap == null) return
        val x = this.curX - difX
        val y = this.curY - difY
        with(rectScaled)
        {
            left = x.roundToInt()
            top = y.roundToInt()
            right = (x + (bitmap?.width ?: 0) + scaledX).roundToInt()
            bottom = (y + (bitmap?.height ?: 0) + scaledY).roundToInt()
        }
    }

    fun scaledUp() {
        val scaleTest = scale + 1
        when {
            abs(scaleTest) > SCALE_MAX -> return
            else -> ++scale
        }
        updateScale()
        updateRectScale()
    }

    fun scaledDown() {
        val scaleTest = scale - 1
        when {
            abs(scaleTest) > SCALE_MAX -> return
            min(bitmap?.width ?: 0, bitmap?.height ?: 0) + (scaleTest * SCALE_SIZE) < 0 -> return
            else -> --scale
        }
        updateScale()
        updateRectScale()
    }

    private fun updateScale() {
        if (bitmap == null) return
        scaledX = SCALE_SIZE * scale
        scaledY = (SCALE_SIZE * (bitmap?.height ?: 0) / (bitmap?.width ?: 0)) * scale
    }

    fun onDraw(canvas: Canvas, paint: Paint) {
        bitmap?.let {
            canvas.drawBitmap(it, null, rectScaled, paint)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mode.toString())
        parcel.writeParcelable(bitmap, flags)
        parcel.writeParcelable(rectScaled, flags)
        parcel.writeInt(scale)
        parcel.writeByte(if (isInBound) 1 else 0)
        parcel.writeFloat(curX)
        parcel.writeFloat(curY)
        parcel.writeInt(difX)
        parcel.writeInt(difY)
        parcel.writeInt(scaledX)
        parcel.writeInt(scaledY)
        parcel.writeInt(pointerId)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ImageMode> {
        override fun createFromParcel(parcel: Parcel) = ImageMode(parcel)

        override fun newArray(size: Int): Array<ImageMode?> = arrayOfNulls(size)
    }
}