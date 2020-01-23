package com.divyanshu.draw.ext.android.graphics

import android.graphics.Path
import android.graphics.PointF


fun Path.composePath(p1: PointF, p2: PointF, endX: Float, endY: Float) {
    reset()
    moveTo(endX, endY)
    lineTo(p1.x, p1.y)
    lineTo(p2.x, p2.y)
    lineTo(endX, endY)
    close()
}

fun Path.composePath(p1: PointF, p2: PointF, p3: PointF) {
    reset()
    moveTo(p3.x, p3.y)
    lineTo(p1.x, p1.y)
    lineTo(p2.x, p2.y)
    lineTo(p3.x, p3.y)
    close()
}
