package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Paint
import com.divyanshu.draw.widget.contract.DrawingMode

class SingleHeadArrowMode(override val mode: DrawingMode): SingleShapeMode(mode) {
    override fun onDraw(canvas: Canvas, paint: Paint) {
        super.onDraw(canvas, paint)
        canvas.drawLine(initX, initY, endX, endY, paint)
    }
}