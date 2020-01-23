package com.divyanshu.draw.widget.container

import android.content.Context
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.ICanvas
import com.divyanshu.draw.widget.contract.IPaint
import com.divyanshu.draw.widget.mode.DoubleHeadArrowMode

class DoubleHeadArrowContainer(override val context: Context, override val drawing: ICanvas) : GenericShapeContainer<DoubleHeadArrowMode>(drawing) {
    private val listener: InteractionListener

    init {
        val ctx = this.context
        if (ctx !is InteractionListener) {
            throw ClassCastException("context must implement InteractionListener")
        }

        listener = ctx
    }

    override fun instantiateDraw() = DoubleHeadArrowMode(DrawingMode.SHAPE_DOUBLE_ARROW)

    override fun attachDrawingTool() = listener.attachComponent(this)

    override fun detachDrawingTool() = listener.detachComponent()

    interface InteractionListener {
        fun attachComponent(paint: IPaint)
        fun detachComponent()
    }
}