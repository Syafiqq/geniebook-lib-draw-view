package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import com.divyanshu.draw.util.MathUtil
import com.divyanshu.draw.widget.contract.DrawingMode
import kotlin.math.pow
import kotlin.math.sqrt

class SingleHeadArrowMode(override val mode: DrawingMode): SingleShapeMode(mode) {
    private val paint = Paint()
    private val endPivot = PointF()
    private val endPivot1 = PointF()
    private val endPivot2 = PointF()

    var d = 0F
    var m = 0F

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

    /**
     * https://math.stackexchange.com/questions/175896/finding-a-point-along-a-line-a-certain-distance-away-from-another-point
     * https://math.stackexchange.com/questions/656500/given-a-point-slope-and-a-distance-along-that-slope-easily-find-a-second-p
     * */
    private fun defineArrow() {
        calculateDistance()
        calculateSlope()
        // https://www.varsitytutors.com/act_math-help/how-to-find-the-slope-of-a-perpendicular-line
        val pm = -1F/m
        val pr = sqrt(1F + pm.pow(2))

        if(strokeWidth * 2 > d) {
            val centerX = (initX + endX) / 2F
            val centerY = (initY + endY) / 2F
            endPivot.x = centerX
            endPivot.y = centerY
            endPivot1.x = centerX + (strokeWidth / pr)
            endPivot1.y = centerY + (strokeWidth * pm / pr)
            endPivot2.x = centerX + (-strokeWidth / pr)
            endPivot2.y = centerY + (-strokeWidth * pm / pr)
        } else {
            val centerX = endX - (strokeWidth * (endX - initX) / d)
            val centerY = endY - (strokeWidth * (endY - initY) / d)
            endPivot.x = centerX
            endPivot.y = centerY
            endPivot1.x = centerX + (strokeWidth / pr)
            endPivot1.y = centerY + (strokeWidth * pm / pr)
            endPivot2.x = centerX + (-strokeWidth / pr)
            endPivot2.y = centerY + (-strokeWidth * pm / pr)
        }
    }

    private fun calculateDistance() {
        d = MathUtil.distanceTwoPoint(initX, initY, endX, endY)
    }

    private fun calculateSlope() {
        m = (endY - initY) / (endX - initX)
    }

    override fun onDraw(canvas: Canvas, paint: Paint) {
        super.onDraw(canvas, paint)
        canvas.drawLine(initX, initY, endX, endY, paint)
        canvas.drawCircle(endPivot.x, endPivot.y, 3F, this.paint)
        canvas.drawCircle(endPivot1.x, endPivot1.y, 3F, this.paint)
        canvas.drawCircle(endPivot2.x, endPivot2.y, 3F, this.paint)
    }
}