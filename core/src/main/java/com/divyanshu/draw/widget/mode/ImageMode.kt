package com.divyanshu.draw.widget.mode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.divyanshu.draw.util.ImageUtil
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.IMode
import java.io.InputStream
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class ImageMode(override val mode: DrawingMode) : IMode {
    private val selThreshold = 32

    var bitmap: Bitmap? = null
    private var rectScaled: Rect = Rect()

    private var scale: Int = 0
    private val scaledMax = 10
    private val scaleSize = 64

    private var isInBound = false

    private var curX = 0F
    private var curY = 0F
    private var difX = 0
    private var difY = 0
    private var scaledX = 0
    private var scaledY = 0
    private var pointerId = -1

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

        return x > (r.left - selThreshold) && x < (r.right + selThreshold) &&
                y > (r.top - selThreshold) && y < (r.bottom + selThreshold)
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
            abs(scaleTest) > scaledMax -> return
            else -> ++scale
        }
        updateScale()
        updateRectScale()
    }

    fun scaledDown() {
        val scaleTest = scale - 1
        when {
            abs(scaleTest) > scaledMax -> return
            min(bitmap?.width ?: 0, bitmap?.height ?: 0) + (scaleTest * scaleSize) < 0 -> return
            else -> --scale
        }
        updateScale()
        updateRectScale()
    }

    private fun updateScale() {
        if (bitmap == null) return
        scaledX = scaleSize * scale
        scaledY = (scaleSize * (bitmap?.height ?: 0) / (bitmap?.width ?: 0)) * scale
    }

    fun onDraw(canvas: Canvas, paint: Paint) {
        bitmap?.let {
            canvas.drawBitmap(it, null, rectScaled, paint)
        }
    }
}