package com.divyanshu.draw.util

import android.content.Context
import android.util.TypedValue

object UnitConverter {
    fun spToPx(context: Context, sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
    }

    fun <K, V, X> List<X>.convertToMap(key: (X) -> K, value: (X, i: Int) -> V): Map<K, V> {
        val map = mutableMapOf<K, V>()
        this.forEachIndexed { i, it ->
            map[key(it)] = value(it, i)
        }
        return map
    }
}