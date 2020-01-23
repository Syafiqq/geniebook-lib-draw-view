package com.divyanshu.draw.widget.container

import android.content.Context
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.ICanvas
import com.divyanshu.draw.widget.mode.FilledRectangleMode

class FilledRectangleContainer(override val context: Context, override val drawing: ICanvas) : GenericShapeContainer<FilledRectangleMode>(context, drawing) {
    override fun instantiateDraw() = FilledRectangleMode(DrawingMode.SHAPE_FILLED_RECTANGLE)
}