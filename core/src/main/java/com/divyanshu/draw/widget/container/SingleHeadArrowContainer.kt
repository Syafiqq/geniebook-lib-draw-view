package com.divyanshu.draw.widget.container

import android.content.Context
import com.divyanshu.draw.widget.contract.*
import com.divyanshu.draw.widget.mode.LineMode
import com.divyanshu.draw.widget.mode.SingleHeadArrowMode

class SingleHeadArrowContainer(override val context: Context, override val drawing: ICanvas) : GenericOutlineShapeContainer<SingleHeadArrowMode>(drawing) {
    private val listener: InteractionListener

    init {
        val ctx = this.context
        if (ctx !is InteractionListener) {
            throw ClassCastException("context must implement InteractionListener")
        }

        listener = ctx
    }

    override fun instantiateDraw() = SingleHeadArrowMode(DrawingMode.SHAPE_SINGLE_ARROW)

    override fun attachDrawingTool() = listener.attachComponent(this)

    override fun detachDrawingTool() = listener.detachComponent()

    interface InteractionListener {
        fun attachComponent(paint: IPaint)
        fun detachComponent()
    }
}