package com.divyanshu.draw.widget.container

import android.content.Context
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.ICanvas
import com.divyanshu.draw.widget.mode.OutlineEllipseMode

class OutlineEllipseContainer(override val context: Context, override val drawing: ICanvas) : GenericShapeContainer<OutlineEllipseMode>(context, drawing) {
    override fun instantiateDraw() = OutlineEllipseMode(DrawingMode.SHAPE_OUTLINE_ELLIPSE)
}