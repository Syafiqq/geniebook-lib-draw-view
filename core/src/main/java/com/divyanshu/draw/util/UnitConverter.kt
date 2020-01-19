package com.divyanshu.draw.util

import android.content.Context
import android.util.TypedValue

object UnitConverter {
    fun spToPx(context: Context, sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
    }
}