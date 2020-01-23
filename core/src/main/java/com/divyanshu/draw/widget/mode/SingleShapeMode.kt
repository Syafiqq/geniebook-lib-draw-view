package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Paint
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.IMode

open class SingleShapeMode(override val mode: DrawingMode): IMode {
    var color = 0
    var strokeWidth = 0F

    protected var endX = 0F
        private set
    protected var endY = 0F
        private set
    protected var initX = 0F
        private set
    protected var initY = 0F
        private set

    fun onFingerDown(x: Float, y: Float) {
        initialPos(x, y)
        currentPos(x, y)
    }

    fun onFingerMove(x: Float, y: Float) {
        currentPos(x, y)
    }

    fun onFingerUp(x: Float, y: Float) {
        currentPos(x, y)
    }

    private fun currentPos(x: Float, y: Float) {
        endX = x
        endY = y
    }

    private fun initialPos(x: Float, y: Float) {
        initX = x
        initY = y
    }

    protected fun decorate(paint: Paint) {
        paint.strokeWidth = strokeWidth
        paint.color = color
    }

    open fun onDraw(canvas: Canvas, paint: Paint) {
        decorate(paint)
    }
}