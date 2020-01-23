package com.divyanshu.draw.widget.mode

import android.graphics.*
import com.divyanshu.draw.ext.android.graphics.arrowHeadPivot
import com.divyanshu.draw.ext.android.graphics.calculatePoint
import com.divyanshu.draw.ext.android.graphics.centerPoint
import com.divyanshu.draw.util.MathUtil
import com.divyanshu.draw.widget.contract.DrawingMode

class SingleHeadArrowMode(override val mode: DrawingMode): SingleShapeMode(mode) {
    private val paint = Paint()
    private val endPivot = PointF()
    private val endPivot1 = PointF()
    private val endPivot2 = PointF()
    private val endPath = Path()

    init {
        with(paint) {
            style = Paint.Style.FILL
            color = Color.BLACK
        }
    }

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
        val d = MathUtil.distanceTwoPoint(initX, initY, endX, endY)
        val m = MathUtil.slopeTwoPoint(initX, initY, endX, endY)
        val pm = MathUtil.perpendicularSlope(m)
        val pr = MathUtil.calculateR(pm)

        if(strokeWidth * 4 > d) {
            endPivot.centerPoint(initX, initY, endX, endY)
            endPivot1.calculatePoint(endPivot, strokeWidth * 2, pr, pm)
            endPivot2.calculatePoint(endPivot, -strokeWidth * 2, pr, pm)
            endPivot.calculatePoint(endPivot, MathUtil.positiveSignum(endX - initX) * strokeWidth * 4, MathUtil.calculateR(m), m)
        } else {
            endPivot.arrowHeadPivot(initX, initY, endX, endY, strokeWidth * 2, d)
            endPivot1.calculatePoint(endPivot, strokeWidth * 2, pr, pm)
            endPivot2.calculatePoint(endPivot, -strokeWidth * 2, pr, pm)
            endPivot.calculatePoint(endPivot, MathUtil.positiveSignum(endX - initX) * strokeWidth * 4, MathUtil.calculateR(m), m)
        }

        endPath.composePath(endPivot1, endPivot2, endPivot)
    }

    override fun decorate(paint: Paint) {
        super.decorate(paint)
        this.paint.color = color
        this.paint.strokeWidth = strokeWidth
    }

    override fun onDraw(canvas: Canvas, paint: Paint) {
        super.onDraw(canvas, paint)
        canvas.drawLine(initX, initY, endX, endY, paint)
        canvas.drawPath(endPath, this.paint)
    }
}

private fun Path.composePath(p1: PointF, p2: PointF, endX: Float, endY: Float) {
    reset()
    moveTo(endX, endY)
    lineTo(p1.x, p1.y)
    lineTo(p2.x, p2.y)
    lineTo(endX, endY)
    close()
}

private fun Path.composePath(p1: PointF, p2: PointF, p3: PointF) {
    reset()
    moveTo(p3.x, p3.y)
    lineTo(p1.x, p1.y)
    lineTo(p2.x, p2.y)
    lineTo(p3.x, p3.y)
    close()
}
