package com.example.scaleview.test

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

fun View.dp2Px(dp: Float): Int {
    return (dp * resources.displayMetrics.density + 0.5f).toInt()
}
class ViewScale @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var rect: RectF
    val padding = dp2Px(15f)
    val paddingRect = dp2Px(16f)

    var left = 0f
    var top = 0f
    var bottom = 0f
    var right = 0f

    var isTouch = Orient.DEFAULT
    val paint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#4D1A1A1A")
        style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initRect(w, h)
        rect = RectF(left, top, right, bottom)
    }


    var oldPoint = PointF(0f, 0f)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawViews(canvas)
    }

    fun initRect(w: Int, h: Int) {
        left = paddingRect.toFloat()
        top = paddingRect.toFloat()
        bottom = (h.toFloat() - paddingRect)
        right = (w.toFloat() - paddingRect)
    }

    private fun drawViews(canvas: Canvas) {
        canvas.drawRect(rect, paint)

        drawCircle(canvas, rect.left, rect.top)
        drawCircle(canvas, rect.right, rect.top)
        drawCircle(canvas, rect.left, rect.bottom)
        drawCircle(canvas, rect.right, rect.bottom)
    }

    fun scaleUp(ratio: Float) {
        if (checkValidScale()) {
            logn(msg = "scale up  $rect")
            rect.left = rect.left - ratio
            rect.top = rect.top - ratio
            rect.right = rect.right + ratio
            rect.bottom = rect.bottom + ratio
        } else {
            logn(msg = "not scale  $rect")
            rect.left = paddingRect.toFloat()
            rect.top = paddingRect.toFloat()
            rect.right = (width.toFloat() - paddingRect)
            rect.bottom = (height.toFloat() - paddingRect)
        }
        invalidate()
    }


    fun scaleDown(ratio: Float) {
        logn(msg = "down")
        rect.left = rect.left + ratio
        rect.top = rect.top + ratio
        rect.right = rect.right - ratio
        rect.bottom = rect.bottom - ratio

        invalidate()
    }

    fun moveRectHorizontal(w: Float) {
        rect.left += w
        rect.right += w
        invalidate()
    }

    fun moveRectVertical(w: Float) {
        rect.top += w
        rect.bottom += w
        invalidate()
    }

    fun checkValidScale(): Boolean {
        return rect.width() < width - paddingRect && rect.height() < height - paddingRect
    }


    fun drawCircle(canvas: Canvas, x: Float, y: Float) {
        canvas.drawCircle(x, y, dp2Px(10f).toFloat(), Paint().apply {
            color = Color.RED
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                when (isTouch) {
                    Orient.TOPLEFT -> {
                        if (x < oldPoint.x || y < oldPoint.y) {
                            //up
                            logn(msg = "TOPLEFT-UP")
                            scaleUp(oldPoint.x - x)
                        } else {
                            //down
                            logn(msg = "TOPLEFT-DOWN")
                            scaleDown(x - oldPoint.x)
                        }
                        oldPoint = PointF(event.x, event.y)
                    }

                    Orient.TOPRIGHT -> {
                        if (x > oldPoint.x || y < oldPoint.y) {
                            //up
                            scaleUp(x - oldPoint.x)
                            logn(msg = "TOPRIGHT-UP")
                        } else {
                            //down
                            logn(msg = "TOPRIGHT-down")
                            scaleDown(oldPoint.x - x)
                        }
                        oldPoint = PointF(event.x, event.y)
                    }

                    Orient.BOTTOMLEFT -> {
                        if (x < oldPoint.x && y > oldPoint.y) {
                            //up
                            scaleUp(oldPoint.x - x)
                            logn(msg = "BOTTOMLEFT-UP")
                        } else {
                            //down
                            scaleDown(x - oldPoint.x)
                            logn(msg = "BOTTOMLEFT-down")
                        }
                        oldPoint = PointF(event.x, event.y)
                    }

                    Orient.BOTTOMRIGHT -> {
                        if (x > oldPoint.x && y > oldPoint.y) {
                            logn(msg = "BOTTOMRIGHT-UP")
                            scaleUp(x - oldPoint.x)
                            //up
                        } else {
                            logn(msg = "BOTTOMRIGHT-down")
                            scaleDown(oldPoint.x - x)
                            //down
                        }
                        oldPoint = PointF(event.x, event.y)
                    }

                    else -> {
                        if (x != oldPoint.x) {
                            moveRectHorizontal(x - oldPoint.x)
                        }

                        if (y != oldPoint.y) {
                            moveRectVertical(y - oldPoint.y)
                        }

                        oldPoint = PointF(event.x, event.y)
                    }

                }

            }

            MotionEvent.ACTION_UP -> {
                isTouch = Orient.DEFAULT
            }

            MotionEvent.ACTION_DOWN -> {
                oldPoint = PointF(event.x, event.y)
                when {
                    x > (rect.left - padding) && x < rect.left + padding && y > (rect.top - padding) && y < (rect.top + padding) -> {
                        logn("ManhNQ1", "TOPLEFT")
                        isTouch = Orient.TOPLEFT
                    }
                    x > (rect.right - padding) && x < rect.right + padding && y > (rect.top - padding) && y < (rect.top + padding) -> {
                        logn("ManhNQ1", "TOPRIGHT")
                        isTouch = Orient.TOPRIGHT
                    }
                    x > (rect.left - padding) && x < rect.left + padding && y > (rect.bottom - padding) && y < (rect.bottom + padding) -> {
                        logn("ManhNQ1", "BOTTOMLEFT")
                        isTouch = Orient.BOTTOMLEFT
                    }
                    x > (rect.right - padding) && x < rect.right + padding && y > (rect.bottom - padding) && y < (rect.bottom + padding) -> {
                        logn("ManhNQ1", "BOTTOMRIGHT")
                        isTouch = Orient.BOTTOMRIGHT
                    }

                }

                return true
            }
        }
        return false
    }


    fun logn(tag: String = "ManhNQ", msg: String) {
        Log.d(tag, msg)
    }

}

enum class Orient {
    TOPLEFT, TOPRIGHT, BOTTOMRIGHT, BOTTOMLEFT, DEFAULT
}