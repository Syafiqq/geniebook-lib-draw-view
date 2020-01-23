package com.divyanshu.draw.util

import kotlin.math.pow
import kotlin.math.sqrt

object MathUtil {
    fun distanceTwoPoint(x1: Double, y1: Double, x2: Double, y2: Double): Double{
        return sqrt((x2 - x1).pow(2.0) + (y2 - y1).pow(2.0))
    }

    fun distanceTwoPoint(x1: Float, y1: Float, x2: Float, y2: Float): Float{
        return sqrt((x2 - x1).pow(2F) + (y2 - y1).pow(2F))
    }
}