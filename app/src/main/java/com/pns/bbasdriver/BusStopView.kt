package com.pns.bbasdriver

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class BusStopView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    View(context, attrs, defStyle) {

    private var circleBorderSize: Float = 0f
    private var circleSolidColor: Int = ContextCompat.getColor(getContext(), R.color.colorBackground)
    private var circleBorderColor: Int = ContextCompat.getColor(getContext(), R.color.colorText)
    private var isLeftLine: Boolean = true
    private var isRightLine: Boolean = true
    private var innerPaint = Paint()
    private var borderPaint = Paint()
    private var linePaint = Paint()

    init {
        drawSetting(attrs)
        setPaint()
    }

    private fun drawSetting(attrs: AttributeSet?) {
        attrs?.run {
            context.obtainStyledAttributes(this, R.styleable.BusStopView)
        }?.run {
            circleSolidColor = getColor(R.styleable.BusStopView_circleSolidColor, circleSolidColor)
            circleBorderColor = getColor(R.styleable.BusStopView_circleBorderColor, circleBorderColor)
            circleBorderSize = getDimension(R.styleable.BusStopView_circleBorderSize, 0f)
            isLeftLine = getBoolean(R.styleable.BusStopView_leftLine, isLeftLine)
            isRightLine = getBoolean(R.styleable.BusStopView_rightLine, isRightLine)
            recycle()
        }
    }

    private fun setPaint() {
        innerPaint.run {
            color = circleSolidColor
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        borderPaint.run {
            color = circleBorderColor
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeWidth = circleBorderSize
        }
        linePaint.run {
            color = ContextCompat.getColor(context, R.color.colorText)
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeWidth = circleBorderSize
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val mediumXPos = measuredWidth / 2f
        val mediumYPos = measuredHeight / 2f

        when {
            isLeftLine and isRightLine ->
                canvas?.drawLine(0f, mediumYPos, measuredWidth * 2f, mediumYPos, linePaint)

            isLeftLine ->
                canvas?.drawLine(0f, mediumYPos, mediumXPos, mediumYPos, borderPaint)

            isRightLine ->
                canvas?.drawLine(mediumXPos, mediumYPos, measuredWidth * 1f, mediumYPos, linePaint)

        }
        canvas?.drawCircle(mediumXPos, mediumYPos, measuredHeight / 3f, innerPaint)
        canvas?.drawCircle(mediumXPos, mediumYPos, measuredHeight / 3f, borderPaint)
    }

    fun setCircleSolidColor(color: Int) {
        if (innerPaint.color != color) {
            innerPaint.color = color
            invalidate()
        }
    }

    fun setCircleBorderColor(color: Int) {
        if (borderPaint.color != color) {
            borderPaint.color = color
            invalidate()
        }

    }

    fun setLine(left: Boolean, right: Boolean) {
        isLeftLine = left
        isRightLine = right
        invalidate()
    }
}

