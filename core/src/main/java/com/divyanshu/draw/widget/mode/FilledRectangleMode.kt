package com.divyanshu.draw.widget.mode

import android.graphics.Paint
import com.divyanshu.draw.widget.contract.DrawingMode

class FilledRectangleMode(override val mode: DrawingMode): OutlineRectangleMode(mode) {
    override fun decorate(paint: Paint) {
        super.decorate(paint)
        paint.style = Paint.Style.FILL_AND_STROKE
    }
}
