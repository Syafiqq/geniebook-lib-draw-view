package com.divyanshu.draw.widget.contract

interface ICanvas {
    fun attachToCanvas(draw: IMode)
    fun requestInvalidate()
}