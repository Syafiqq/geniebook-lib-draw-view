package com.divyanshu.draw.util

import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

object MathUtil {
    fun distanceTwoPoint(x1: Double, y1: Double, x2: Double, y2: Double): Double{
        return sqrt((x2 - x1).pow(2.0) + (y2 - y1).pow(2.0))
    }

    fun distanceTwoPoint(x1: Float, y1: Float, x2: Float, y2: Float): Float{
        return sqrt((x2 - x1).pow(2F) + (y2 - y1).pow(2F))
    }

    fun slopeTwoPoint(x1: Float, y1: Float, x2: Float, y2: Float): Float{
        return (y2 - y1) / (x2 - x1)
    }

    /**
     * https://www.varsitytutors.com/act_math-help/how-to-find-the-slope-of-a-perpendicular-line
     * */
    fun perpendicularSlope(m: Float): Float = -1F/m

    /**
     * https://www.varsitytutors.com/act_math-help/how-to-find-the-slope-of-a-perpendicular-line
     * */
    fun calculateR(m: Float): Float = sqrt(1F + m.pow(2))

    fun intSign(v: Float): Int = if(sign(v) >= 0) 1 else -1
}