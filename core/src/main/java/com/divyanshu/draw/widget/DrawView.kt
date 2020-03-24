package com.divyanshu.draw.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.divyanshu.draw.widget.container.*
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.ICanvas
import com.divyanshu.draw.widget.contract.IDrawingContainer
import com.divyanshu.draw.widget.contract.IMode
import com.divyanshu.draw.widget.contract.design.command.ICommand
import com.divyanshu.draw.widget.contract.design.command.ICommandManager
import com.divyanshu.draw.widget.impl.command.ClearCommand
import com.divyanshu.draw.widget.impl.command.DrawCommand
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet
import com.divyanshu.draw.util.UnitConverter.convertToMap


class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs), ICanvas, ICommandManager {
    override val recordF = Stack<ICommand>()
    override val recordB = Stack<ICommand>()
    private val holder = ArrayList<IMode>()
    private val container = LinkedHashSet<IMode>()

    private val linePath = PenContainer(context, this)
    private val eraserPath = EraserContainer(context, this)
    private val textContainer = TextContainer(context, this)
    private val imageContainer = ImageContainer(context, this)
    private val shapeLineContainer = ShapeLineContainer(context, this)
    private val singleArrowContainer = SingleHeadArrowContainer(context, this)
    private val doubleArrowContainer = DoubleHeadArrowContainer(context, this)
    private val outlineRectContainer = OutlineRectangleContainer(context, this)
    private val filledRectContainer = FilledRectangleContainer(context, this)
    private val outlineEllipseContainer = OutlineEllipseContainer(context, this)
    private val filledEllipseContainer = FilledEllipseContainer(context, this)

    private var drawingTool: IDrawingContainer<*>? = null
    private var _drawingMode: DrawingMode? = null
    var drawingMode: DrawingMode?
        get() = _drawingMode
        set(value) {
            if (_drawingMode == value) return
            _drawingMode = value
            drawingTool?.destroyDrawingObject()
            drawingTool?.detachDrawingTool()
            requestInvalidate()
            drawingTool = when (value) {
                DrawingMode.LINE -> linePath
                DrawingMode.ERASE -> eraserPath
                DrawingMode.TEXT -> textContainer
                DrawingMode.IMAGE -> imageContainer
                DrawingMode.SHAPE_LINE -> shapeLineContainer
                DrawingMode.SHAPE_SINGLE_ARROW -> singleArrowContainer
                DrawingMode.SHAPE_DOUBLE_ARROW -> doubleArrowContainer
                DrawingMode.SHAPE_OUTLINE_RECTANGLE -> outlineRectContainer
                DrawingMode.SHAPE_FILLED_RECTANGLE -> filledRectContainer
                DrawingMode.SHAPE_OUTLINE_ELLIPSE -> outlineEllipseContainer
                DrawingMode.SHAPE_FILLED_ELLIPSE -> filledEllipseContainer
                else -> null
            }
            drawingTool?.attachDrawingTool()
        }

    init {
        isSaveEnabled = true
        val paint = Paint().apply {
            alpha = 0xFF
            color = Color.WHITE
            maskFilter = null
            xfermode = null
        }
        setLayerType(LAYER_TYPE_HARDWARE, paint)
    }

    override fun attachToCanvas(draw: IMode) {
        drawingTool?.destroyDrawingObject()
        doDraw(draw)
        requestInvalidate()
    }

    override fun requestInvalidate() {
        invalidate()
    }

    fun clearCanvas() {
        if (holder.isEmpty()) return
        doClear()
        requestInvalidate()
    }

    fun undo() {
        doUndo()
        requestInvalidate()
    }

    fun redo() {
        doRedo()
        requestInvalidate()
    }

    private fun doUndo() {
        if (recordF.isEmpty()) return

        val command = recordF.pop()
        command.down()
        recordB.push(command)
    }

    private fun doRedo() {
        if (recordB.isEmpty()) return

        val command = recordB.pop()
        command.up()
        recordF.push(command)
    }

    private fun doDraw(draw: IMode) {
        val command = DrawCommand(holder, draw)
        command.up()

        container.add(draw)
        recordF.push(command)
        recordB.forEach {
            if (it is DrawCommand) {
                container.remove(it.draw)
            }
        }
        recordB.clear()
    }

    private fun doClear() {
        val command = ClearCommand(holder)
        command.up()

        recordF.push(command)
        recordB.forEach {
            if (it is DrawCommand) {
                container.remove(it.draw)
            }
        }
        recordB.clear()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        holder.forEach {
            when (it.mode) {
                DrawingMode.LINE -> linePath.onDraw(canvas, it)
                DrawingMode.ERASE -> eraserPath.onDraw(canvas, it)
                DrawingMode.TEXT -> textContainer.onDraw(canvas, it)
                DrawingMode.IMAGE -> imageContainer.onDraw(canvas, it)
                DrawingMode.SHAPE_LINE -> shapeLineContainer.onDraw(canvas, it)
                DrawingMode.SHAPE_SINGLE_ARROW -> singleArrowContainer.onDraw(canvas, it)
                DrawingMode.SHAPE_DOUBLE_ARROW -> doubleArrowContainer.onDraw(canvas, it)
                DrawingMode.SHAPE_OUTLINE_RECTANGLE -> outlineRectContainer.onDraw(canvas, it)
                DrawingMode.SHAPE_FILLED_RECTANGLE -> filledRectContainer.onDraw(canvas, it)
                DrawingMode.SHAPE_OUTLINE_ELLIPSE -> outlineEllipseContainer.onDraw(canvas, it)
                DrawingMode.SHAPE_FILLED_ELLIPSE -> filledEllipseContainer.onDraw(canvas, it)
            }
        }

        drawingTool?.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        if (drawingTool == null) return true

        if(event.action == MotionEvent.ACTION_DOWN) {
            drawingTool?.createDrawingObject(event.x, event.y, event)
        }

        return drawingTool?.onTouchEvent(event) ?: true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState() ?: return null
        val containerMapper = container
                .toList()
                .convertToMap({ x -> x }, { _, i -> i })
        val forwardHolder = LinkedList<Int>()
            LinkedList<ICommand>()
                    .apply {
                        addAll(recordF)
                        addAll(recordB.reversed())
                    }
                    .forEach {
                        if(it is DrawCommand && containerMapper.containsKey(it.draw)) {
                            forwardHolder.add(1)
                            forwardHolder.add(containerMapper[it.draw] ?: -1)
                        } else {
                            forwardHolder.add(0)
                        }
                    }

        val ss = SavedState(superState)
        ss.container.addAll(container)
        ss.forwardHolder.addAll(forwardHolder)
        ss.backwardSize = recordB.size
        ss.currentTool = drawingMode?.toString()
        ss.currentDraw = drawingTool?.draw as Parcelable?

        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if(state is SavedState) {
            container.clear()
            holder.clear()
            recordF.clear()
            recordB.clear()
            state.container.forEach { p ->
                if (p is IMode) {
                    container.add(p)
                }
            }
            val _container = container.toList()
            var c = -1
            while ((c + 1) < state.forwardHolder.size) {
                val flag = state.forwardHolder[++c]
                if(flag == 1) {
                    val draw = _container[state.forwardHolder[++c]]
                    doDraw(draw)
                } else {
                    doClear()
                }
            }
            for (i in 1..state.backwardSize) {
                doUndo()
            }
            val currentTool = state.currentTool
            if(currentTool != null) {
                drawingMode = DrawingMode.valueOf(currentTool)
            }
            val currentDraw = state.currentDraw
            if(currentDraw is IMode) {
                drawingTool?.assignDraw(currentDraw, this)
            }
            requestInvalidate()
        }
    }

    fun isBlank(): Boolean {
        return drawingTool !is EraserContainer && drawingTool?.draw == null && holder.size == 0
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        draw(canvas)
        return bitmap
    }

    class SavedState: BaseSavedState {
        val container = LinkedList<Parcelable>()
        val forwardHolder = LinkedList<Int>()
        var backwardSize = -1
        var currentTool: String? = null
        var currentDraw: Parcelable? = null

        constructor(superState: Parcelable): super(superState)
        constructor(`in`: Parcel?) : super(`in`) {
            `in`?.let { storage ->
                storage.readParcelableArray(ClassLoader.getSystemClassLoader())?.forEach {
                    container.add(it)
                }
                val size = storage.readInt()
                IntArray(size)
                        .also { storage.readIntArray(it) }
                        .forEach { forwardHolder.add(it) }
                backwardSize = storage.readInt()
                currentTool = storage.readString()
                currentDraw = storage.readParcelable(ClassLoader.getSystemClassLoader())
            }
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.let { storage ->
                storage.writeParcelableArray(container.toTypedArray(), 0)
                storage.writeInt(forwardHolder.size)
                storage.writeIntArray(forwardHolder.toIntArray())
                storage.writeInt(backwardSize)
                storage.writeString(currentTool)
                storage.writeParcelable(currentDraw, 0)
            }
        }

        companion object CREATOR: Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel?): SavedState = SavedState(source)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}