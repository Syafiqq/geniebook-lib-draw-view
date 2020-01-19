package com.divyanshu.draw.widget.contract

interface ITextDrawCallback {
    fun onTextRetrieved(text: String, textSize: Float, textWidth: Float)
    fun onApply()
    fun onCancel()
    fun onTextSizeChanged(textSize: Float)
    fun onTextWidthIncrease()
    fun onTextWidthDecrease()
}