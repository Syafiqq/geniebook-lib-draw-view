package com.divyanshu.draw.widget.container

import android.content.Context
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.ICanvas
import com.divyanshu.draw.widget.mode.SingleHeadArrowMode

class SingleHeadArrowContainer(override val context: Context, override val drawing: ICanvas) : GenericShapeContainer<SingleHeadArrowMode>(context, drawing) {
    override fun instantiateDraw() = SingleHeadArrowMode(DrawingMode.SHAPE_SINGLE_ARROW)
}