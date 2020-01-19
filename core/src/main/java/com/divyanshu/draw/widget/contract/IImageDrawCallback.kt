package com.divyanshu.draw.widget.contract

import java.io.InputStream

interface IImageDrawCallback {
    fun onImageRetrieved(image: InputStream)
    fun createImageDirectly(image: InputStream, width: Float, height: Float)
    fun onApply()
    fun onCancel()
    fun onScaleUp()
    fun onScaleDown()
}