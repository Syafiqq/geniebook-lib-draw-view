package com.divyanshu.draw.widget.contract

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent


interface IDrawingContainer<T> {
    fun createDrawingObject(x: Float, y: Float, event:MotionEvent)
    fun destroyDrawingObject()
    fun onDraw(canvas: Canvas, draw: IMode)
    fun onDraw(canvas: Canvas)
    fun onTouchEvent(event: MotionEvent): Boolean
    fun attachDrawingTool()
    fun detachDrawingTool()

    val context: Context
    val drawing: ICanvas
    var draw: T?
}