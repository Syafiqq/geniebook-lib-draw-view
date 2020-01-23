package com.divyanshu.draw.widget.container

import android.content.Context
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.ICanvas
import com.divyanshu.draw.widget.mode.OutlineRectangleMode

class OutlineRectangleContainer(override val context: Context, override val drawing: ICanvas) : GenericShapeContainer<OutlineRectangleMode>(context, drawing) {
    override fun instantiateDraw() = OutlineRectangleMode(DrawingMode.SHAPE_OUTLINE_RECTANGLE)
}