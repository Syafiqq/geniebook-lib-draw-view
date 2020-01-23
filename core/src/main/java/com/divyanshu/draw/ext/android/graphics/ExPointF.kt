package com.divyanshu.draw.ext.android.graphics

import android.graphics.PointF

fun PointF.centerPoint(x1: Float, y1: Float, x2: Float, y2: Float) {
    x = (x1 + x2) / 2F
    y = (y1 + y2) / 2F
}

/**
 * https://math.stackexchange.com/questions/175896/finding-a-point-along-a-line-a-certain-distance-away-from-another-point
 * https://math.stackexchange.com/a/1630886
 * */
fun PointF.arrowHeadPivot(x1: Float, y1: Float, x2: Float, y2: Float, dt: Float, d: Float) {
    x = x2 - (dt * (x2 - x1) / d)
    y = y2 - (dt * (y2 - y1) / d)
}

/**
 * https://math.stackexchange.com/questions/656500/given-a-point-slope-and-a-distance-along-that-slope-easily-find-a-second-p
 * https://math.stackexchange.com/a/656512
 * */
fun PointF.calculatePoint(p: PointF, dt: Float, r: Float, m: Float) {
    if(r == Float.POSITIVE_INFINITY) {
        x = p.x
        y = p.y + dt
    } else {
        x = p.x + (dt / r)
        y = p.y + (dt * m / r)
    }
}
