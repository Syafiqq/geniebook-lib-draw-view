package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.IMode

open class SingleShapeMode(override val mode: DrawingMode): IMode {
    var color = 0
    var strokeWidth = 0F

    protected var endX = 0F
        private set
    protected var endY = 0F
        private set
    protected var initX = 0F
        private set
    protected var initY = 0F
        private set

    constructor(parcel: Parcel) : this(DrawingMode.valueOf(parcel.readString() ?: "")) {
        color = parcel.readInt()
        strokeWidth = parcel.readFloat()
        initX = parcel.readFloat()
        initY = parcel.readFloat()
        endX = parcel.readFloat()
        endY = parcel.readFloat()
    }


    open fun onFingerDown(x: Float, y: Float) {
        initialPos(x, y)
        currentPos(x, y)
    }

    open fun onFingerMove(x: Float, y: Float) {
        currentPos(x, y)
    }

    open fun onFingerUp(x: Float, y: Float) {
        currentPos(x, y)
    }

    private fun currentPos(x: Float, y: Float) {
        endX = x
        endY = y
    }

    private fun initialPos(x: Float, y: Float) {
        initX = x
        initY = y
    }

    protected open fun decorate(paint: Paint) {
        paint.strokeWidth = strokeWidth
        paint.color = color
    }

    open fun onDraw(canvas: Canvas, paint: Paint) {
        decorate(paint)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mode.toString())
        parcel.writeInt(color)
        parcel.writeFloat(strokeWidth)
        parcel.writeFloat(initX)
        parcel.writeFloat(initY)
        parcel.writeFloat(endX)
        parcel.writeFloat(endY)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<SingleShapeMode> {
        override fun createFromParcel(parcel: Parcel) = SingleShapeMode(parcel)

        override fun newArray(size: Int): Array<SingleShapeMode?> = arrayOfNulls(size)
    }
}