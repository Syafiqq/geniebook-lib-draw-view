package com.divyanshu.draw.widget.container

import android.content.Context
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.ICanvas
import com.divyanshu.draw.widget.mode.DoubleHeadArrowMode

class DoubleHeadArrowContainer(override val context: Context, override val drawing: ICanvas) : GenericShapeContainer<DoubleHeadArrowMode>(context, drawing) {
    override fun instantiateDraw() = DoubleHeadArrowMode(DrawingMode.SHAPE_DOUBLE_ARROW)
}