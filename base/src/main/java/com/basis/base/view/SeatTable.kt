package com.basis.base.view

import android.animation.Animator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast

import com.basis.base.R
import java.util.*

/**
 * Created by baoyunlong on 16/6/16.
 */
class SeatTable : View {
    private val DBG = false
    var paint = Paint()
    var overviewPaint = Paint()
    var lineNumberPaint: Paint? = null

    var lineNumberTxtHeight = 0f



    /**
     * 用来保存所有行号
     */
    var lineNumbers: ArrayList<String>? = ArrayList()
    var lineNumberPaintFontMetrics: Paint.FontMetrics? = null
    var mMatrix = Matrix()

    /**
     * 座位水平间距
     */
    var spacing = 0

    /**
     * 座位垂直间距
     */
    var verSpacing = 0

    /**
     * 行号宽度
     */
    var numberWidth = 0

    /**
     * 行数
     */
    var row = 0

    /**
     * 列数
     */
    var column = 0

    /**
     * 可选时座位的图片
     */
    var seatBitmap: Bitmap? = null

    /**
     * 选中时座位的图片
     */
    var checkedSeatBitmap: Bitmap? = null

    /**
     * 座位已经售出时的图片
     */
    var seatSoldBitmap: Bitmap? = null
    var overviewBitmap: Bitmap? = null
    var lastX = 0
    var lastY = 0

    /**
     * 整个座位图的宽度
     */
    var seatBitmapWidth = 0

    /**
     * 整个座位图的高度
     */
    var seatBitmapHeight = 0

    /**
     * 标识是否需要绘制座位图
     */
    var isNeedDrawSeatBitmap = true

    /**
     * 概览图白色方块高度
     */
    var rectHeight = 0f

    /**
     * 概览图白色方块的宽度
     */
    var rectWidth = 0f

    /**
     * 概览图上方块的水平间距
     */
    var overviewSpacing = 0f

    /**
     * 概览图上方块的垂直间距
     */
    var overviewVerSpacing = 0f

    /**
     * 概览图的比例
     */
    var overviewScale = 4.8f

    /**
     * 荧幕高度
     */
    var screenHeight = 0f

    /**
     * 荧幕默认宽度与座位图的比例
     */
    var screenWidthScale = 0.5f

    /**
     * 荧幕最小宽度
     */
    var defaultScreenWidth = 0

    /**
     * 标识是否正在缩放
     */
    var isScaling = false
    var mScaleX = 0f
    var mScaleY = 0f

    /**
     * 是否是第一次缩放
     */
    var firstScale = true

    /**
     * 最多可以选择的座位数量
     */
    var mMaxSelected = Int.MAX_VALUE
    private var seatChecker: SeatChecker? = null

    /**
     * 荧幕名称
     */
    private var screenName = ""

    /**
     * 整个概览图的宽度
     */
    var rectW = 0f

    /**
     * 整个概览图的高度
     */
    var rectH = 0f
    var headPaint: Paint? = null
    var headBitmap: Bitmap? = null

    /**
     * 是否第一次执行onDraw
     */
    var isFirstDraw = true

    /**
     * 标识是否需要绘制概览图
     */
    var isDrawOverview = false

    /**
     * 标识是否需要更新概览图
     */
    var isDrawOverviewBitmap = true
    var overview_checked = 0
    var overview_sold = 0
    var txt_color = 0
    var seatCheckedResID = 0
    var seatSoldResID = 0
    var seatAvailableResID = 0
    var isOnClick = false
    private var downX = 0
    private var downY = 0
    private var pointer = false

    /**
     * 顶部高度,可选,已选,已售区域的高度
     */
    var headHeight = 0f
    var pathPaint: Paint? = null
    var rectF: RectF? = null

    /**
     * 头部下面横线的高度
     */
    var borderHeight = 1
    var redBorderPaint: Paint? = null

    /**
     * 默认的座位图宽度,如果使用的自己的座位图片比这个尺寸大或者小,会缩放到这个大小
     */
    private val defaultImgW = 40f

    /**
     * 默认的座位图高度
     */
    private val defaultImgH = 34f

    /**
     * 座位图片的宽度
     */
    private var seatWidth = 0

    /**
     * 座位图片的高度
     */
    private var seatHeight = 0

    constructor(context: Context?) : super(context) {}
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.SeatTableView)
        overview_checked = typedArray.getColor(
            R.styleable.SeatTableView_overview_checked,
            Color.parseColor("#5A9E64")
        )
        overview_sold =
            typedArray.getColor(R.styleable.SeatTableView_overview_sold, Color.RED)
        txt_color =
            typedArray.getColor(R.styleable.SeatTableView_txt_color, Color.WHITE)
        seatCheckedResID = typedArray.getResourceId(R.styleable.SeatTableView_seat_checked, R.mipmap.seat_green)
        seatSoldResID = typedArray.getResourceId(R.styleable.SeatTableView_overview_sold, R.mipmap.seat_sold)
        seatAvailableResID = typedArray.getResourceId(R.styleable.SeatTableView_seat_available, R.mipmap.seat_gray)
        typedArray.recycle()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    var xScale1 = 1f
    var yScale1 = 1f
    private fun init() {
        spacing = dip2Px(5f).toInt()
        verSpacing = dip2Px(10f).toInt()
        defaultScreenWidth = dip2Px(80f).toInt()
        seatBitmap = BitmapFactory.decodeResource(resources, seatAvailableResID)
        seatBitmap?.let {
            val scaleX = defaultImgW / it.getWidth()
            val scaleY = defaultImgH / it.getHeight()
            xScale1 = scaleX
            yScale1 = scaleY
            seatHeight = (it.getHeight() * yScale1).toInt()
            seatWidth = (it.getWidth() * xScale1).toInt()
            checkedSeatBitmap = BitmapFactory.decodeResource(resources, seatCheckedResID)
            seatSoldBitmap = BitmapFactory.decodeResource(resources, seatSoldResID)
            seatBitmapWidth =
                (column * it.getWidth() * xScale1 + (column - 1) * spacing).toInt()
            seatBitmapHeight = (row * it.getHeight() * yScale1 + (row - 1) * verSpacing).toInt()
        }

        paint.color = Color.RED
        numberWidth = dip2Px(50f).toInt()
        screenHeight = dip2Px(20f)
        headHeight = dip2Px(30f)
        headPaint = Paint()
        headPaint?.style = Paint.Style.FILL
        headPaint?.textSize = 24f
        headPaint?.color = Color.WHITE
        headPaint?.isAntiAlias = true
        pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pathPaint?.style = Paint.Style.FILL
        pathPaint?.color = Color.parseColor("#e2e2e2")
        redBorderPaint = Paint()
        redBorderPaint?.isAntiAlias = true
        redBorderPaint?.color = Color.RED
        redBorderPaint?.style = Paint.Style.STROKE
        redBorderPaint?.strokeWidth = resources.displayMetrics.density * 1
        rectF = RectF()
        rectHeight = seatHeight / overviewScale
        rectWidth = seatWidth / overviewScale
        overviewSpacing = spacing / overviewScale
        overviewVerSpacing = verSpacing / overviewScale
        rectW = column * rectWidth + (column - 1) * overviewSpacing + overviewSpacing * 2
        rectH = row * rectHeight + (row - 1) * overviewVerSpacing + overviewVerSpacing * 2
        overviewBitmap = Bitmap.createBitmap(rectW.toInt(), rectH.toInt(), Bitmap.Config.ARGB_4444)
        lineNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        lineNumberPaint?.color = bacColor
        lineNumberPaint?.textSize = resources.displayMetrics.density * 16
        lineNumberTxtHeight = lineNumberPaint!!.measureText("4")
        lineNumberPaintFontMetrics = lineNumberPaint!!.fontMetrics
        lineNumberPaint?.textAlign = Paint.Align.CENTER
        if (lineNumbers == null) {
            lineNumbers = ArrayList()
        } else if (lineNumbers!!.size <= 0) {
            for (i in 0 until row) {
                lineNumbers?.add((i + 1).toString() + "")
            }
        }
        mMatrix.postTranslate(
            numberWidth + spacing.toFloat(),
            headHeight + screenHeight + borderHeight + verSpacing
        )
    }

    override fun onDraw(canvas: Canvas) {
        val startTime = System.currentTimeMillis()
        if (row <= 0 || column == 0) {
            return
        }
        drawSeat(canvas)
        drawNumber(canvas)
        if (headBitmap == null) {
            headBitmap = drawHeadInfo()
        }
        canvas.drawBitmap(headBitmap!!, 0f, 0f, null)
        drawScreen(canvas)
        if (isDrawOverview) {
            val s = System.currentTimeMillis()
            if (isDrawOverviewBitmap) {
                drawOverview()
            }
            canvas.drawBitmap(overviewBitmap!!, 0f, 0f, null)
            drawOverview(canvas)
            Log.d(
                "drawTime",
                "OverviewDrawTime:" + (System.currentTimeMillis() - s)
            )
        }
        if (DBG) {
            val drawTime = System.currentTimeMillis() - startTime
            Log.d("drawTime", "totalDrawTime:$drawTime")
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val y = event.y.toInt()
        val x = event.x.toInt()
        super.onTouchEvent(event)
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        val pointerCount = event.pointerCount
        if (pointerCount > 1) {
            pointer = true
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pointer = false
                downX = x
                downY = y
                isDrawOverview = true
                mHandler.removeCallbacks(hideOverviewRunnable)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> if (!isScaling && !isOnClick) {
                val downDX = Math.abs(x - downX)
                val downDY = Math.abs(y - downY)
                if ((downDX > 10 || downDY > 10) && !pointer) {
                    val dx = x - lastX
                    val dy = y - lastY
                    mMatrix.postTranslate(dx.toFloat(), dy.toFloat())
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                mHandler.postDelayed(hideOverviewRunnable, 1500)
                autoScale()
                val downDX = Math.abs(x - downX)
                val downDY = Math.abs(y - downY)
                if ((downDX > 10 || downDY > 10) && !pointer) {
                    autoScroll()
                }
            }
        }
        isOnClick = false
        lastY = y
        lastX = x
        return true
    }

    private val hideOverviewRunnable = Runnable {
        isDrawOverview = false
        invalidate()
    }

    fun drawHeadInfo(): Bitmap {
        val txt = "已售"
        val txtY = getBaseLine(headPaint, 0f, headHeight)
        val txtWidth = headPaint!!.measureText(txt).toInt()
        val spacing = dip2Px(10f)
        val spacing1 = dip2Px(5f)
        val y = (headHeight - seatBitmap!!.height) / 2
        val width =
            seatBitmap!!.width + spacing1 + txtWidth + spacing + seatSoldBitmap!!.width + txtWidth + spacing1 + spacing + checkedSeatBitmap!!.height + spacing1 + txtWidth
        val bitmap =
            Bitmap.createBitmap(getWidth(), headHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        //绘制背景
        canvas.drawRect(0f, 0f, getWidth().toFloat(), headHeight, headPaint!!)
        headPaint!!.color = Color.BLACK
        val startX = (getWidth() - width) / 2
        tempMatrix.setScale(xScale1, yScale1)
        tempMatrix.postTranslate(startX, (headHeight - seatHeight) / 2)
        canvas.drawBitmap(seatBitmap!!, tempMatrix, headPaint)
        canvas.drawText("可选", startX + seatWidth + spacing1, txtY, headPaint!!)
        val soldSeatBitmapY =
            startX + seatBitmap!!.width + spacing1 + txtWidth + spacing
        tempMatrix.setScale(xScale1, yScale1)
        tempMatrix.postTranslate(soldSeatBitmapY, (headHeight - seatHeight) / 2)
        canvas.drawBitmap(seatSoldBitmap!!, tempMatrix, headPaint)
        canvas.drawText("已售", soldSeatBitmapY + seatWidth + spacing1, txtY, headPaint!!)
        val checkedSeatBitmapX =
            soldSeatBitmapY + seatSoldBitmap!!.width + spacing1 + txtWidth + spacing
        tempMatrix.setScale(xScale1, yScale1)
        tempMatrix.postTranslate(checkedSeatBitmapX, y)
        canvas.drawBitmap(checkedSeatBitmap!!, tempMatrix, headPaint)
        canvas.drawText("已选", checkedSeatBitmapX + spacing1 + seatWidth, txtY, headPaint!!)

        //绘制分割线
        headPaint!!.strokeWidth = 1f
        headPaint!!.color = Color.GRAY
        canvas.drawLine(0f, headHeight, getWidth().toFloat(), headHeight, headPaint!!)
        return bitmap
    }

    /**
     * 绘制中间屏幕
     */
    fun drawScreen(canvas: Canvas) {
        pathPaint!!.style = Paint.Style.FILL
        pathPaint!!.color = Color.parseColor("#e2e2e2")
        val startY = headHeight + borderHeight
        val centerX = seatBitmapWidth * matrixScaleX / 2 + translateX
        var screenWidth = seatBitmapWidth * screenWidthScale * matrixScaleX
        if (screenWidth < defaultScreenWidth) {
            screenWidth = defaultScreenWidth.toFloat()
        }
        val path = Path()
        path.moveTo(centerX, startY)
        path.lineTo(centerX - screenWidth / 2, startY)
        path.lineTo(centerX - screenWidth / 2 + 20, screenHeight * matrixScaleY + startY)
        path.lineTo(centerX + screenWidth / 2 - 20, screenHeight * matrixScaleY + startY)
        path.lineTo(centerX + screenWidth / 2, startY)
        canvas.drawPath(path, pathPaint!!)
        pathPaint!!.color = Color.BLACK
        pathPaint!!.textSize = 20 * matrixScaleX
        canvas.drawText(
            screenName,
            centerX - pathPaint!!.measureText(screenName) / 2,
            getBaseLine(pathPaint, startY, startY + screenHeight * matrixScaleY),
            pathPaint!!
        )
    }

    var tempMatrix = Matrix()
    fun drawSeat(canvas: Canvas) {
        zoom = matrixScaleX
        val startTime = System.currentTimeMillis()
        val translateX = translateX
        val translateY = translateY
        val scaleX = zoom
        val scaleY = zoom
        for (i in 0 until row) {
            val top =
                i * seatBitmap!!.height * yScale1 * scaleY + i * verSpacing * scaleY + translateY
            val bottom = top + seatBitmap!!.height * yScale1 * scaleY
            if (bottom < 0 || top > height) {
                continue
            }
            for (j in 0 until column) {
                val left =
                    j * seatBitmap!!.width * xScale1 * scaleX + j * spacing * scaleX + translateX
                val right = left + seatBitmap!!.width * xScale1 * scaleY
                if (right < 0 || left > width) {
                    continue
                }
                val seatType = getSeatType(i, j)
                tempMatrix.setTranslate(left, top)
                tempMatrix.postScale(xScale1, yScale1, left, top)
                tempMatrix.postScale(scaleX, scaleY, left, top)
                when (seatType) {
                    SEAT_TYPE_AVAILABLE -> canvas.drawBitmap(
                        seatBitmap!!,
                        tempMatrix,
                        paint
                    )
                    SEAT_TYPE_NOT_AVAILABLE -> {
                    }
                    SEAT_TYPE_SELECTED -> {
                        canvas.drawBitmap(checkedSeatBitmap!!, tempMatrix, paint)
                        drawText(canvas, i, j, top, left)
                    }
                    SEAT_TYPE_SOLD -> canvas.drawBitmap(
                        seatSoldBitmap!!,
                        tempMatrix,
                        paint
                    )
                }
            }
        }
        if (DBG) {
            val drawTime = System.currentTimeMillis() - startTime
            Log.d("drawTime", "seatDrawTime:$drawTime")
        }
    }

    private fun getSeatType(row: Int, column: Int): Int {
        if (isHave(getID(row, column)) >= 0) {
            return SEAT_TYPE_SELECTED
        }
        if (seatChecker != null) {
            if (!seatChecker!!.isValidSeat(row, column)) {
                return SEAT_TYPE_NOT_AVAILABLE
            } else if (seatChecker!!.isSold(row, column)) {
                return SEAT_TYPE_SOLD
            }
        }
        return SEAT_TYPE_AVAILABLE
    }

    private fun getID(row: Int, column: Int): Int {
        return row * this.column + (column + 1)
    }

    /**
     * 绘制选中座位的行号列号
     *
     * @param row
     * @param column
     */
    private fun drawText(
        canvas: Canvas,
        row: Int,
        column: Int,
        top: Float,
        left: Float
    ) {
        var txt = (row + 1).toString() + "排"
        var txt1: String? = (column + 1).toString() + "座"
        if (seatChecker != null) {
            val strings = seatChecker!!.checkedSeatTxt(row, column)
            if (strings != null && strings.size > 0) {
                if (strings.size >= 2) {
                    txt = strings[0]
                    txt1 = strings[1]
                } else {
                    txt = strings[0]
                    txt1 = null
                }
            }
        }
        val txtPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        txtPaint.color = txt_color
        txtPaint.typeface = Typeface.DEFAULT_BOLD
        val seatHeight = seatHeight * matrixScaleX
        val seatWidth = seatWidth * matrixScaleX
        txtPaint.textSize = seatHeight / 3

        //获取中间线
        val center = seatHeight / 2
        val txtWidth = txtPaint.measureText(txt)
        val startX = left + seatWidth / 2 - txtWidth / 2

        //只绘制一行文字
        if (txt1 == null) {
            canvas.drawText(txt, startX, getBaseLine(txtPaint, top, top + seatHeight), txtPaint)
        } else {
            canvas.drawText(txt, startX, getBaseLine(txtPaint, top, top + center), txtPaint)
            canvas.drawText(
                txt1,
                startX,
                getBaseLine(txtPaint, top + center, top + center + seatHeight / 2),
                txtPaint
            )
        }
        if (DBG) {
            Log.d("drawTest:", "top:$top")
        }
    }

    var bacColor = Color.parseColor("#7e000000")

    /**
     * 绘制行号
     */
    fun drawNumber(canvas: Canvas) {
        val startTime = System.currentTimeMillis()
        lineNumberPaint!!.color = bacColor
        val translateY = translateY.toInt()
        val scaleY = matrixScaleY
        rectF?.top = translateY - lineNumberTxtHeight / 2
        rectF?.bottom = translateY + seatBitmapHeight * scaleY + lineNumberTxtHeight / 2
        rectF?.left = 0f
        rectF?.right = numberWidth.toFloat()
        canvas.drawRoundRect(
            rectF!!,
            numberWidth / 2.toFloat(),
            numberWidth / 2.toFloat(),
            lineNumberPaint!!
        )
        lineNumberPaint!!.color = Color.WHITE
        for (i in 0 until row) {
            val top = (i * seatHeight + i * verSpacing) * scaleY + translateY
            val bottom =
                (i * seatHeight + i * verSpacing + seatHeight) * scaleY + translateY
            val baseline =
                (bottom + top - lineNumberPaintFontMetrics!!.bottom - lineNumberPaintFontMetrics!!.top) / 2
            canvas.drawText(
                lineNumbers!![i],
                numberWidth / 2.toFloat(),
                baseline,
                lineNumberPaint!!
            )
        }
        if (DBG) {
            val drawTime = System.currentTimeMillis() - startTime
            Log.d("drawTime", "drawNumberTime:$drawTime")
        }
    }

    /**
     * 绘制概览图
     */
    fun drawOverview(canvas: Canvas) {

        //绘制红色框
        var left = (-translateX).toInt()
        if (left < 0) {
            left = 0
        }
        left /= overviewScale.toInt()
        left /= matrixScaleX.toInt()
        var currentWidth =
            (translateX + (column * seatWidth + spacing * (column - 1)) * matrixScaleX).toInt()
        currentWidth = if (currentWidth > width) {
            currentWidth - width
        } else {
            0
        }
        val right = (rectW - currentWidth / overviewScale / matrixScaleX).toInt()
        var top = -translateY + headHeight
        if (top < 0) {
            top = 0f
        }
        top /= overviewScale
        top /= matrixScaleY
        if (top > 0) {
            top += overviewVerSpacing
        }
        var currentHeight =
            (translateY + (row * seatHeight + verSpacing * (row - 1)) * matrixScaleY).toInt()
        currentHeight = if (currentHeight > height) {
            currentHeight - height
        } else {
            0
        }
        val bottom = (rectH - currentHeight / overviewScale / matrixScaleY).toInt()
        canvas.drawRect(left.toFloat(), top, right.toFloat(), bottom.toFloat(), redBorderPaint!!)
    }

    fun drawOverview(): Bitmap? {
        isDrawOverviewBitmap = false
        val bac = Color.parseColor("#7e000000")
        overviewPaint.color = bac
        overviewPaint.isAntiAlias = true
        overviewPaint.style = Paint.Style.FILL
        overviewBitmap!!.eraseColor(Color.TRANSPARENT)
        val canvas = Canvas(overviewBitmap!!)
        //绘制透明灰色背景
        canvas.drawRect(0f, 0f, rectW, rectH, overviewPaint)
        overviewPaint.color = Color.WHITE
        for (i in 0 until row) {
            val top = i * rectHeight + i * overviewVerSpacing + overviewVerSpacing
            for (j in 0 until column) {
                val seatType = getSeatType(i, j)
                when (seatType) {
                    SEAT_TYPE_AVAILABLE -> overviewPaint.color = Color.WHITE
                   // SEAT_TYPE_NOT_AVAILABLE -> continue
                    SEAT_TYPE_SELECTED -> overviewPaint.color = overview_checked
                    SEAT_TYPE_SOLD -> overviewPaint.color = overview_sold
                }
                var left: Float
                left = j * rectWidth + j * overviewSpacing + overviewSpacing
                canvas.drawRect(left, top, left + rectWidth, top + rectHeight, overviewPaint)
            }
        }
        return overviewBitmap
    }

    /**
     * 自动回弹
     * 整个大小不超过控件大小的时候:
     * 往左边滑动,自动回弹到行号右边
     * 往右边滑动,自动回弹到右边
     * 往上,下滑动,自动回弹到顶部
     *
     *
     * 整个大小超过控件大小的时候:
     * 往左侧滑动,回弹到最右边,往右侧滑回弹到最左边
     * 往上滑动,回弹到底部,往下滑动回弹到顶部
     */
    private fun autoScroll() {
        val currentSeatBitmapWidth = seatBitmapWidth * matrixScaleX
        val currentSeatBitmapHeight = seatBitmapHeight * matrixScaleY
        var moveYLength = 0f
        var moveXLength = 0f

        //处理左右滑动的情况
        if (currentSeatBitmapWidth < width) {
            if (translateX < 0 || matrixScaleX < numberWidth + spacing) {
                //计算要移动的距离
                moveXLength = if (translateX < 0) {
                    -translateX + numberWidth + spacing
                } else {
                    numberWidth + spacing - translateX
                }
            }
        } else {
            if (translateX < 0 && translateX + currentSeatBitmapWidth > width) {
            } else {
                //往左侧滑动
                moveXLength = if (translateX + currentSeatBitmapWidth < width) {
                    width - (translateX + currentSeatBitmapWidth)
                } else {
                    //右侧滑动
                    -translateX + numberWidth + spacing
                }
            }
        }
        val startYPosition =
            screenHeight * matrixScaleY + verSpacing * matrixScaleY + headHeight + borderHeight

        //处理上下滑动
        if (currentSeatBitmapHeight + headHeight < height) {
            moveYLength = if (translateY < startYPosition) {
                startYPosition - translateY
            } else {
                -(translateY - startYPosition)
            }
        } else {
            if (translateY < 0 && translateY + currentSeatBitmapHeight > height) {
            } else {
                //往上滑动
                moveYLength = if (translateY + currentSeatBitmapHeight < height) {
                    height - (translateY + currentSeatBitmapHeight)
                } else {
                    -(translateY - startYPosition)
                }
            }
        }
        val start = Point()
        start.x = translateX.toInt()
        start.y = translateY.toInt()
        val end = Point()
        end.x = (start.x + moveXLength).toInt()
        end.y = (start.y + moveYLength).toInt()
        moveAnimate(start, end)
    }

    private fun autoScale() {
        if (matrixScaleX > 2.2) {
            zoomAnimate(matrixScaleX, 2.0f)
        } else if (matrixScaleX < 0.98) {
            zoomAnimate(matrixScaleX, 1.0f)
        }
    }

    var mHandler = Handler()
    var selects = ArrayList<Int>()
    val selectedSeat: ArrayList<String>
        get() {
            val results = ArrayList<String>()
            for (i in 0 until row) {
                for (j in 0 until column) {
                    if (isHave(getID(i, j)) >= 0) {
                        results.add("$i,$j")
                    }
                }
            }
            return results
        }

    private fun isHave(seat: Int): Int {
        return Collections.binarySearch(selects, seat)
    }

    private fun remove(index: Int) {
        selects.removeAt(index)
    }

    var m = FloatArray(9)
    private val translateX: Float
        private get() {
            mMatrix.getValues(m)
            return m[2]
        }

    private val translateY: Float
        private get() {
            mMatrix.getValues(m)
            return m[5]
        }

    private val matrixScaleY: Float
        private get() {
            mMatrix.getValues(m)
            return m[4]
        }

    private val matrixScaleX: Float
        private get() {
            mMatrix.getValues(m)
            return m[Matrix.MSCALE_X]
        }

    private fun dip2Px(value: Float): Float {
        return resources.displayMetrics.density * value
    }

    private fun getBaseLine(
        p: Paint?,
        top: Float,
        bottom: Float
    ): Float {
        val fontMetrics = p!!.fontMetrics
        val baseline = ((bottom + top - fontMetrics.bottom - fontMetrics.top) / 2).toInt()
        return baseline.toFloat()
    }

    private fun moveAnimate(
        start: Point,
        end: Point
    ) {
        val valueAnimator = ValueAnimator.ofObject(MoveEvaluator(), start, end)
        valueAnimator.interpolator = DecelerateInterpolator()
        val moveAnimation = MoveAnimation()
        valueAnimator.addUpdateListener(moveAnimation)
        valueAnimator.duration = 400
        valueAnimator.start()
    }

    private fun zoomAnimate(cur: Float, tar: Float) {
        val valueAnimator = ValueAnimator.ofFloat(cur, tar)
        valueAnimator.interpolator = DecelerateInterpolator()
        val zoomAnim = ZoomAnimation()
        valueAnimator.addUpdateListener(zoomAnim)
        valueAnimator.addListener(zoomAnim)
        valueAnimator.duration = 400
        valueAnimator.start()
    }

    private var zoom = 0f
    private fun zoom(zoom: Float) {
        val z = zoom / matrixScaleX
        mMatrix.postScale(z, z, mScaleX, mScaleY)
        invalidate()
    }

    private fun move(p: Point) {
        val x = p.x - translateX
        val y = p.y - translateY
        mMatrix.postTranslate(x, y)
        invalidate()
    }

    internal inner class MoveAnimation : AnimatorUpdateListener {
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val p = animation.animatedValue as Point
            move(p)
        }
    }

    internal inner class MoveEvaluator :
        TypeEvaluator<Any?> {
        override fun evaluate(
            fraction: Float,
            startValue: Any?,
            endValue: Any?
        ): Any? {
            val startPoint = startValue as Point?
            val endPoint = endValue as Point?
            val x = (startPoint!!.x + fraction * (endPoint!!.x - startPoint.x)).toInt()
            val y = (startPoint.y + fraction * (endPoint.y - startPoint.y)).toInt()
            return Point(x, y)
        }
    }

    internal inner class ZoomAnimation : AnimatorUpdateListener,
        Animator.AnimatorListener {
        override fun onAnimationUpdate(animation: ValueAnimator) {
            zoom = animation.animatedValue as Float
            zoom(zoom)
            if (DBG) {
                Log.d("zoomTest", "zoom:$zoom")
            }
        }

        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
        override fun onAnimationStart(animation: Animator) {}
    }

    fun setData(row: Int, column: Int) {
        this.row = row
        this.column = column
        init()
        invalidate()
    }

    var scaleGestureDetector = ScaleGestureDetector(context, object : OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                isScaling = true
                var scaleFactor = detector.scaleFactor
                if (matrixScaleY * scaleFactor > 3) {
                    scaleFactor = 3 / matrixScaleY
                }
                if (firstScale) {
                    mScaleX = detector.currentSpanX
                    mScaleY = detector.currentSpanY
                    firstScale = false
                }
                if (matrixScaleY * scaleFactor < 0.5) {
                    scaleFactor = 0.5f / matrixScaleY
                }
                mMatrix.postScale(scaleFactor, scaleFactor, mScaleX, mScaleY)
                invalidate()
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                isScaling = false
                firstScale = true
            }
        })
    var gestureDetector =
        GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                isOnClick = true
                val x = e.x.toInt()
                val y = e.y.toInt()
                for (i in 0 until row) {
                    for (j in 0 until column) {
                        val tempX =
                            ((j * seatWidth + (j + 1) * spacing) * matrixScaleX + translateX).toInt()
                        val maxTemX = (tempX + seatWidth * matrixScaleX).toInt()
                        val tempY =
                            ((i * seatHeight + i * verSpacing) * matrixScaleY + translateY).toInt()
                        val maxTempY = (tempY + seatHeight * matrixScaleY).toInt()
                        if (seatChecker != null && seatChecker!!.isValidSeat(
                                i,
                                j
                            ) && !seatChecker!!.isSold(i, j)
                        ) {
                            if (x >= tempX && x <= maxTemX && y >= tempY && y <= maxTempY) {
                                val id = getID(i, j)
                                val index = isHave(id)
                                if (index >= 0) {
                                    remove(index)
                                    if (seatChecker != null) {
                                        seatChecker!!.unCheck(i, j)
                                    }
                                } else {
                                    if (selects.size >= mMaxSelected) {
                                        Toast.makeText(
                                            context,
                                            "最多只能选择" + mMaxSelected + "个",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return super.onSingleTapConfirmed(e)
                                    } else {
                                        addChooseSeat(i, j)
                                        if (seatChecker != null) {
                                            seatChecker!!.checked(i, j)
                                        }
                                    }
                                }
                                isNeedDrawSeatBitmap = true
                                isDrawOverviewBitmap = true
                                val currentScaleY = matrixScaleY
                                if (currentScaleY < 1.7) {
                                    mScaleX = x.toFloat()
                                    mScaleY = y.toFloat()
                                    zoomAnimate(currentScaleY, 1.9f)
                                }
                                invalidate()
                                break
                            }
                        }
                    }
                }
                return super.onSingleTapConfirmed(e)
            }
        })

    private fun addChooseSeat(row: Int, column: Int) {
        val id = getID(row, column)
        for (i in selects.indices) {
            val item = selects[i]
            if (id < item) {
                selects.add(i, id)
                return
            }
        }
        selects.add(id)
    }

    interface SeatChecker {
        /**
         * 是否可用座位
         *
         * @param row
         * @param column
         * @return
         */
        fun isValidSeat(row: Int, column: Int): Boolean

        /**
         * 是否已售
         *
         * @param row
         * @param column
         * @return
         */
        fun isSold(row: Int, column: Int): Boolean
        fun checked(row: Int, column: Int)
        fun unCheck(row: Int, column: Int)

        /**
         * 获取选中后座位上显示的文字
         * @param row
         * @param column
         * @return 返回2个元素的数组,第一个元素是第一行的文字,第二个元素是第二行文字,如果只返回一个元素则会绘制到座位图的中间位置
         */
        fun checkedSeatTxt(row: Int, column: Int): Array<String>?
    }

    fun setScreenName(screenName: String) {
        this.screenName = screenName
    }

    fun setMaxSelected(maxSelected: Int) {
        this.mMaxSelected = maxSelected
    }

    fun setSeatChecker(seatChecker: SeatChecker?) {
        this.seatChecker = seatChecker
        invalidate()
    }

    private fun getRowNumber(row: Int): Int {
        var result = row
        if (seatChecker == null) {
            return -1
        }
        for (i in 0 until row) {
            for (j in 0 until column) {
                if (seatChecker!!.isValidSeat(i, j)) {
                    break
                }
                if (j == column - 1) {
                    if (i == row) {
                        return -1
                    }
                    result--
                }
            }
        }
        return result
    }

    private fun getColumnNumber(row: Int, column: Int): Int {
        var result = column
        if (seatChecker == null) {
            return -1
        }
        for (i in row..row) {
            for (j in 0 until column) {
                if (!seatChecker!!.isValidSeat(i, j)) {
                    if (j == column) {
                        return -1
                    }
                    result--
                }
            }
        }
        return result
    }

    companion object {
        /**
         * 座位已售
         */
        private const val SEAT_TYPE_SOLD = 1

        /**
         * 座位已经选中
         */
        private const val SEAT_TYPE_SELECTED = 2

        /**
         * 座位可选
         */
        private const val SEAT_TYPE_AVAILABLE = 3

        /**
         * 座位不可用
         */
        private const val SEAT_TYPE_NOT_AVAILABLE = 4
    }
}