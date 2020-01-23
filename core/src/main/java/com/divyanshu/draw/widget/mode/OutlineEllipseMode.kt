package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.divyanshu.draw.widget.contract.DrawingMode

open class OutlineEllipseMode(override val mode: DrawingMode): SingleShapeMode(mode) {
    val rect = RectF()

    override fun onFingerDown(x: Float, y: Float) {
        super.onFingerDown(x, y)
        defineArrow()
    }

    override fun onFingerMove(x: Float, y: Float) {
        super.onFingerMove(x, y)
        defineArrow()
    }

    override fun onFingerUp(x: Float, y: Float) {
        super.onFingerUp(x, y)
        defineArrow()
    }

    private fun defineArrow() {
        rect.left = initX
        rect.top = initY
        rect.right = endX
        rect.bottom = endY
    }

    override fun onDraw(canvas: Canvas, paint: Paint) {
        super.onDraw(canvas, paint)
        canvas.drawOval(rect, paint)
    }
}
