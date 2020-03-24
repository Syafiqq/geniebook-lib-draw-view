package com.divyanshu.draw.widget.mode

import android.graphics.*
import com.divyanshu.draw.ext.android.graphics.*
import com.divyanshu.draw.util.MathUtil
import com.divyanshu.draw.widget.contract.DrawingMode

class DoubleHeadArrowMode(override val mode: DrawingMode): SingleShapeMode(mode) {
    private val paint = Paint()
    private val endPivot = PointF()
    private val endPivot1 = PointF()
    private val endPivot2 = PointF()
    private val endPath = Path()
    private val headPivot = PointF()
    private val headPivot1 = PointF()
    private val headPivot2 = PointF()
    private val headPath = Path()

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
            endPivot.calculatePoint(endPivot, MathUtil.intSign(endX - initX) * strokeWidth * 4, MathUtil.calculateR(m), m)

            headPivot.centerPoint(initX, initY, endX, endY)
            headPivot1.calculatePoint(headPivot, strokeWidth * 2, pr, pm)
            headPivot2.calculatePoint(headPivot, -strokeWidth * 2, pr, pm)
            headPivot.calculatePoint(headPivot, -MathUtil.intSign(endX - initX) * strokeWidth * 4, MathUtil.calculateR(m), m)
        } else {
            endPivot.arrowHeadPivot(initX, initY, endX, endY, strokeWidth * 2, d)
            endPivot1.calculatePoint(endPivot, strokeWidth * 2, pr, pm)
            endPivot2.calculatePoint(endPivot, -strokeWidth * 2, pr, pm)
            endPivot.calculatePoint(endPivot, MathUtil.intSign(endX - initX) * strokeWidth * 4, MathUtil.calculateR(m), m)

            headPivot.arrowTailPivot(initX, initY, endX, endY, strokeWidth * 2, d)
            headPivot1.calculatePoint(headPivot, strokeWidth * 2, pr, pm)
            headPivot2.calculatePoint(headPivot, -strokeWidth * 2, pr, pm)
            headPivot.calculatePoint(headPivot, -MathUtil.intSign(endX - initX) * strokeWidth * 4, MathUtil.calculateR(m), m)
        }

        endPath.composePath(endPivot1, endPivot2, endPivot)
        headPath.composePath(headPivot1, headPivot2, headPivot)
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
        canvas.drawPath(headPath, this.paint)
    }
}
