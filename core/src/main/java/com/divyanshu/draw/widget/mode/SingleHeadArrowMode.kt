package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import com.divyanshu.draw.ext.android.graphics.arrowHeadPivot
import com.divyanshu.draw.ext.android.graphics.calculatePoint
import com.divyanshu.draw.ext.android.graphics.centerPoint
import com.divyanshu.draw.util.MathUtil
import com.divyanshu.draw.widget.contract.DrawingMode
import kotlin.math.pow
import kotlin.math.sqrt

class SingleHeadArrowMode(override val mode: DrawingMode): SingleShapeMode(mode) {
    private val paint = Paint()
    private val endPivot = PointF()
    private val endPivot1 = PointF()
    private val endPivot2 = PointF()

    init {
        with(paint) {
            style = Paint.Style.FILL_AND_STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            color = Color.RED
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
        val pm = MathUtil.perpendicularSlope(MathUtil.slopeTwoPoint(initX, initY, endX, endY))
        val pr = sqrt(1F + pm.pow(2))

        if(strokeWidth * 2 > d) {
            endPivot.centerPoint(initX, initY, endX, endY)
            endPivot1.calculatePoint(endPivot, strokeWidth, pr, pm)
            endPivot2.calculatePoint(endPivot, -strokeWidth, pr, pm)
        } else {
            endPivot.arrowHeadPivot(initX, initY, endX, endY, strokeWidth, d)
            endPivot1.calculatePoint(endPivot, strokeWidth, pr, pm)
            endPivot2.calculatePoint(endPivot, -strokeWidth, pr, pm)
        }
    }

    override fun onDraw(canvas: Canvas, paint: Paint) {
        super.onDraw(canvas, paint)
        canvas.drawLine(initX, initY, endX, endY, paint)
        canvas.drawCircle(endPivot.x, endPivot.y, 3F, this.paint)
        canvas.drawCircle(endPivot1.x, endPivot1.y, 3F, this.paint)
        canvas.drawCircle(endPivot2.x, endPivot2.y, 3F, this.paint)
    }
}