package com.divyanshu.draw.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.divyanshu.draw.widget.container.EraserContainer
import com.divyanshu.draw.widget.container.ImageContainer
import com.divyanshu.draw.widget.container.PenContainer
import com.divyanshu.draw.widget.container.TextContainer
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
import com.divyanshu.draw.util.UnitConverter.convertToMap


class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs), ICanvas, ICommandManager {
    override val recordF = Stack<ICommand>()
    override val recordB = Stack<ICommand>()
    private val holder = ArrayList<IMode>()

    private val linePath = PenContainer(context, this)
    private val eraserPath = EraserContainer(context, this)
    private val textContainer = TextContainer(context, this)
    private val imageContainer = ImageContainer(context, this)

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

        val command = DrawCommand(holder, draw)
        command.up()

        recordF.push(command)
        recordB.clear()

        requestInvalidate()
    }

    override fun requestInvalidate() {
        invalidate()
    }

    fun clearCanvas() {
        if (holder.isEmpty()) return

        val command = ClearCommand(holder)
        command.up()

        recordF.push(command)
        recordB.clear()

        requestInvalidate()
    }

    fun undo() {
        shiftHolder()
        requestInvalidate()
    }

    fun redo() {
        unshiftHolder()
        requestInvalidate()
    }

    private fun shiftHolder() {
        if (recordF.isEmpty()) return

        val command = recordF.pop()
        command.down()
        recordB.push(command)
    }

    private fun unshiftHolder() {
        if (recordB.isEmpty()) return

        val command = recordB.pop()
        command.up()
        recordF.push(command)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        holder.forEach {
            when (it.mode) {
                DrawingMode.LINE -> linePath.onDraw(canvas, it)
                DrawingMode.ERASE -> eraserPath.onDraw(canvas, it)
                DrawingMode.TEXT -> textContainer.onDraw(canvas, it)
                DrawingMode.IMAGE -> imageContainer.onDraw(canvas, it)
                else -> {}
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

        val ss = SavedState(superState)
        ss.holder.addAll(holder)
        ss.recordF.addAll(recordF)
        ss.recordB.addAll(recordB)
        ss.backSize = -1
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if(state is SavedState) {
            holder.addAll(state.holder)
            Log.d("Draw View", "Raw Record ${state.rawRecordF.size}")
            Log.d("Draw View", "Redo ${state.backSize}")
            Log.d("Draw View", "Redo ${state.holder.size}")
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
        val holder = ArrayList<IMode>()
        val recordF = LinkedList<ICommand>()
        val recordB = LinkedList<ICommand>()
        val rawRecordF = LinkedList<Int>()
        var backSize: Int = 0

        constructor(superState: Parcelable): super(superState)
        constructor(`in`: Parcel?) : super(`in`) {
            `in`?.let { storage ->
                storage.readParcelableArray(ClassLoader.getSystemClassLoader())?.forEach {
                    if(it is IMode) {
                        holder.add(it)
                    }
                }
                this.backSize = storage.readInt()
/*                val recordFSize = storage.readInt()
                val rawRecord = IntArray(recordFSize)
                storage.readIntArray(rawRecord)
                rawRecord.forEach {
                    rawRecordF.add(it)
                }*/
            }
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            val holderMapper = holder.convertToMap({ x -> x }, { _, i -> i })

            val recordFHolder = LinkedList<Int>()
/*            LinkedList<ICommand>()
                    .apply {
                        addAll(recordF)
                        addAll(recordB)
                    }
                    .forEach {
                        if(it is DrawCommand) {
                            recordFHolder.add(1)
                            recordFHolder.add(holderMapper[it.draw] ?: -1)
                        } else {
                            recordFHolder.add(0)
                        }
                    }*/
            out?.let { storage ->
                storage.writeParcelableArray(holder.toTypedArray(), 0)
                storage.writeInt(recordB.size)
/*                storage.writeInt(recordFHolder.size)
                storage.writeIntArray(recordFHolder.toIntArray())*/
            }
        }

        companion object CREATOR: Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel?): SavedState = SavedState(source)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}