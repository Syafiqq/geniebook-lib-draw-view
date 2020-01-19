package com.divyanshu.draw.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
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
        if (recordF.isEmpty()) return

        val command = recordF.pop()
        command.down()
        recordB.push(command)

        requestInvalidate()
    }

    fun redo() {
        if (recordB.isEmpty()) return

        val command = recordB.pop()
        command.up()
        recordF.push(command)

        requestInvalidate()
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
}