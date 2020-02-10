package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Parcel
import android.os.Parcelable
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.IMode
import java.util.*

class PathMode(override val mode: DrawingMode) : Path(), IMode, Parcelable {
    var color = 0
    var strokeWidth = 0F

    private var curX = 0F
    private var curY = 0F
    private var initX = 0F
    private var initY = 0F
    private val paths = LinkedList<Float>()

    constructor(parcel: Parcel) : this(DrawingMode.valueOf(parcel.readString() ?: "")) {
        color = parcel.readInt()
        strokeWidth = parcel.readFloat()
        val paths = FloatArray(parcel.readInt())
        parcel.readFloatArray(paths)
        this.paths.addAll(paths.toList())
    }

    fun onFingerDown(x: Float, y: Float) {
        registerPath(x, y)
        reset()
        moveTo(x, y)
        initialPos(x, y)
        currentPos(x, y)
    }

    fun onFingerMove(x: Float, y: Float) {
        registerPath(x, y)
        quadTo(curX, curY, (x + curX) / 2, (y + curY) / 2)
        currentPos(x, y)
    }

    fun onFingerUp(x: Float, y: Float) {
        registerPath(x, y)
        lineTo(curX, curY)

        if (initX == curX && initY == curY) {
            lineTo(curX, curY + 2)
            lineTo(curX + 1, curY + 2)
            lineTo(curX + 1, curY)
        }
    }

    private fun currentPos(x: Float, y: Float) {
        curX = x
        curY = y
    }

    private fun initialPos(x: Float, y: Float) {
        initX = x
        initY = y
    }

    private fun registerPath(x: Float, y: Float) {
        paths.add(x)
        paths.add(y)
    }

    private fun decorate(paint: Paint) {
        paint.strokeWidth = strokeWidth
        paint.color = color
    }

    fun onDraw(canvas: Canvas, paint: Paint) {
        decorate(paint)
        canvas.drawPath(this, paint)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mode.toString())
        parcel.writeInt(color)
        parcel.writeFloat(strokeWidth)
        parcel.writeInt(paths.size)
        parcel.writeFloatArray(paths.toFloatArray())
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<PathMode> {
        override fun createFromParcel(parcel: Parcel) = PathMode(parcel)

        override fun newArray(size: Int): Array<PathMode?> = arrayOfNulls(size)
    }
}