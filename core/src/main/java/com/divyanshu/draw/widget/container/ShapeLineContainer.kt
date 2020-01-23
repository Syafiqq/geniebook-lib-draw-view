package com.divyanshu.draw.widget.container

import android.content.Context
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.ICanvas
import com.divyanshu.draw.widget.mode.LineMode

class ShapeLineContainer(override val context: Context, override val drawing: ICanvas) : GenericShapeContainer<LineMode>(context, drawing) {
    override fun instantiateDraw() = LineMode(DrawingMode.SHAPE_LINE)
}