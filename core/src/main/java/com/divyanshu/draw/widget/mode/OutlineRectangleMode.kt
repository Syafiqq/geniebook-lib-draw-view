package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Paint
import com.divyanshu.draw.widget.contract.DrawingMode

open class OutlineRectangleMode(override val mode: DrawingMode): SingleShapeMode(mode) {
    override fun decorate(paint: Paint) {
        super.decorate(paint)
        paint.strokeCap = Paint.Cap.SQUARE
        paint.strokeJoin = Paint.Join.MITER
    }

    override fun onDraw(canvas: Canvas, paint: Paint) {
        super.onDraw(canvas, paint)
        canvas.drawRect(initX, initY, endX, endY, paint)
    }
}
