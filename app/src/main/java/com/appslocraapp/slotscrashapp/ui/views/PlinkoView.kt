package com.appslocraapp.slotscrashapp.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.appslocraapp.slotscrashapp.R
import com.appslocraapp.slotscrashapp.data.models.Event

data class Od(
    val resource: Int,
    val cef: Double
)

data class OdInfoPosition(
    val x: Float,
    val y: Float,
    val od: Od
)

class PlinkoBoardView : View {

    private var ballImage: Bitmap? = null
    private var pegImage: Bitmap? = null

    private val paint = Paint()

    private val pegs = mutableListOf<Pair<Float, Float>>()
    private val odsPositions = mutableListOf<OdInfoPosition>()
    private val odsBitmaps = hashMapOf<Int, Bitmap>()

    private var endPositionPlinkoBoard = -1f
    private var lastRowPositionPlinkoBoardX = -1f
    private var ballDropped = false
    private var bet: String = "green"

    private var ballX = -1f
    private var ballY = -1f

    private var prevPeg: Pair<Float, Float>? = null

    private var ballDimension: Int? = null
    private var pegDimension: Int? = null

    var iPlinkoEventListener: IPlinkoEventListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        ballDimension = resources.getDimension(com.intuit.sdp.R.dimen._10sdp).toInt()
        pegDimension = resources.getDimension(com.intuit.sdp.R.dimen._10sdp).toInt()
        ballImage = BitmapFactory.decodeResource(resources, R.drawable.ball).toDrawable(resources)
            .toBitmap(ballDimension!!, ballDimension!!)
        pegImage = BitmapFactory.decodeResource(resources, R.drawable.peg).toDrawable(resources)
            .toBitmap(pegDimension!!, pegDimension!!)
    }

    override fun onDraw(canvas: Canvas) {
        drawBoard(canvas)
        drawOds(canvas)

        if (ballDropped) {
            drawBall(canvas)
            ballDown()
        }

        pegs.forEach { coords ->
            if (ballY < endPositionPlinkoBoard) {
                if (ballX in ((coords.first - pegImage!!.width / 2)..(coords.first + pegImage!!.width / 2)) &&
                    ballY in ((coords.second - pegImage!!.height / 2)..(coords.second + pegImage!!.height / 2))
                ) {
                    val isLeft = coords.first <= ballX
                    ValueAnimator.ofFloat(0f, 3f).apply {
                        duration = 100
                        addUpdateListener {
                            if (isLeft) {
                                ballX -= it.animatedValue as Float
                            } else {
                                ballX += it.animatedValue as Float
                            }
                            invalidate()
                        }
                        start()
                    }
                }
                prevPeg = coords
            }
        }

        odsPositions.forEach { odInfo ->
            if (ballX in ((odInfo.x - 63 / 2)..(odInfo.x + 63 / 2)) &&
                ballY in ((odInfo.y - 45 / 2)..(odInfo.y + 45 / 2))
            ) {
                ballDropped = false
                val index = PlinkoAppInfo.ods[0].indexOf(odInfo.od)
                val neededOd = when (bet) {
                    "green" -> PlinkoAppInfo.ods[0][index]
                    "yellow" -> PlinkoAppInfo.ods[1][index]
                    "red" -> PlinkoAppInfo.ods[2][index]
                    else -> PlinkoAppInfo.ods[0][index]
                }
                if (event.getContentIfNotHandled() != null) {
                    iPlinkoEventListener?.onOdRiched(neededOd)
                }
            }
        }
    }

    private var event: Event<Boolean> = Event(false)

    private fun drawBall(canvas: Canvas) {
        val pexSize = pegDimension!! * 1.5
        if (ballX == -1f && ballY == -1f) {
            val centerPoint = (width / 2f + pexSize / 2f).toInt()
            val ballDropStartX = ((centerPoint - 50)..(centerPoint + 50)).random().toFloat()
            ballX = ballDropStartX
            ballY = 0f
            ValueAnimator.ofFloat(0f, 10f).apply {
                duration = 200
                addUpdateListener {
                    ballY += it.animatedValue as Float
                    invalidate()
                }
                repeatCount = 0
                start()
            }
        }
        canvas.drawBitmap(ballImage!!, ballX, ballY, paint)
    }

    private fun ballDown() {
        ballY += 5f
        invalidate()
    }

    private fun drawBoard(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        val numRows = 11

        val pexSize = pegDimension!! * 1.5f
        val centerPoint = width / 2f + pexSize / 2f

        pegs.clear()
        for (row in 3 until numRows + 3) {
            val pegsCount = row - 1
            val totalWidth =
                pegsCount * pexSize
            val startPointX = centerPoint - totalWidth / 2f
            val endPointX = totalWidth / 2f + centerPoint
            val startPointY = (pexSize - 10f) * (row - 2) + 30f
            endPositionPlinkoBoard = startPointY + pexSize
            lastRowPositionPlinkoBoardX = startPointX

            for (peg in 0 until pegsCount) {
                pegs.add(startPointX + (peg * pexSize) to startPointY)
                canvas.drawBitmap(pegImage!!, startPointX + (peg * pexSize), startPointY, paint)
            }
        }
    }

    private fun drawOds(canvas: Canvas) {
        PlinkoAppInfo.ods.forEachIndexed { rowIndex, rowOds ->
            rowOds.forEachIndexed { index, od ->
                val bitmap = if (!odsBitmaps.containsKey(od.resource)) {
                    val bitmapWidth = resources.getDimension(com.intuit.sdp.R.dimen._16sdp).toInt()
                    val bitmapHeight = resources.getDimension(com.intuit.sdp.R.dimen._11sdp).toInt()
                    val b =
                        BitmapFactory.decodeResource(resources, od.resource).toDrawable(resources)
                            .toBitmap(bitmapWidth, bitmapHeight)
                    odsBitmaps[od.resource] = b
                    b
                } else {
                    odsBitmaps[od.resource]
                }
                odsPositions.add(
                    OdInfoPosition(
                        lastRowPositionPlinkoBoardX + bitmap!!.width * index.toFloat(),
                        endPositionPlinkoBoard + bitmap.height * rowIndex.toFloat(),
                        od
                    )
                )
                canvas.drawBitmap(
                    bitmap!!,
                    lastRowPositionPlinkoBoardX + bitmap.width * index.toFloat(),
                    endPositionPlinkoBoard + bitmap.height * rowIndex.toFloat(),
                    paint
                )
            }
        }
    }

    fun dropBall() {
        ballX = -1f
        ballY = -1f
        ballDropped = true
        invalidate()
    }

}

interface IPlinkoEventListener {
    fun onOdRiched(od: Od)
}