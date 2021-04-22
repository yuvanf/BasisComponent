package com.basis.base.anim

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

import com.basis.base.R

/**
 * 提供开屏logo动画效果
 * 可以通过调用[.setLogoText]设置logo名称
 * 通过调用[.startAnimation]开启logo动画
 */
class AnimLogoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val mLogoTexts = SparseArray<String>()
    private val mQuietPoints = SparseArray<PointF>()
    private val mRadonPoints = SparseArray<PointF>()
    private var mOffsetAnimator: ValueAnimator? = null
    private var mGradientAnimator: ValueAnimator? = null
    private var mPaint: Paint? = null
    private var mTextPadding: Int
    private var mTextColor: Int
    private var mTextSize: Int
    private var mOffsetAnimProgress = 0f
    private var mOffsetDuration: Int
    private var isOffsetAnimEnd = false
    private var mGradientDuration: Int
    private var mLinearGradient: LinearGradient? = null
    private var mGradientColor: Int
    private var mGradientMatrix: Matrix? = null
    private var mMatrixTranslate = 0
    private val isAutoPlay: Boolean
    private var mWidth = 0
    private var mHeight = 0
    private var isShowGradient: Boolean
    private val mLogoOffset: Int
    private var mGradientListener: Animator.AnimatorListener? = null

    // fill the text to array
    private fun fillLogoTextArray(logoName: String?) {
        if (TextUtils.isEmpty(logoName)) {
            return
        }
        if (mLogoTexts.size() > 0) {
            mLogoTexts.clear()
        }
        for (i in 0 until logoName!!.length) {
            val c = logoName[i]
            val s = c.toString()
            mLogoTexts.put(i, s)
        }
    }

    private fun initPaint() {
        if (mPaint == null) {
            mPaint = Paint()
            mPaint?.isAntiAlias = true
            mPaint?.style = Paint.Style.FILL
            mPaint?.strokeCap = Paint.Cap.ROUND
        }
        mPaint?.textSize = mTextSize.toFloat()
        mPaint?.color = mTextColor
    }


    private fun initOffsetAnimation() {
        if (mOffsetAnimator == null) {
            mOffsetAnimator = ValueAnimator.ofFloat(0f, 1f)
            mOffsetAnimator?.let {
                it.setInterpolator(AccelerateDecelerateInterpolator())
                it.addUpdateListener(AnimatorUpdateListener { animation ->
                    if (mQuietPoints.size() <= 0 || mRadonPoints.size() <= 0) {
                        return@AnimatorUpdateListener
                    }
                    mOffsetAnimProgress = animation.animatedValue as Float
                    invalidate()
                })
                it.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (mGradientAnimator != null && isShowGradient) {
                            isOffsetAnimEnd = true
                            mPaint?.shader = mLinearGradient
                            mGradientAnimator?.start()
                        }
                    }
                })
            }

        }
        mOffsetAnimator?.duration = mOffsetDuration.toLong()
    }

    // init the gradient animation
    private fun initGradientAnimation(width: Int) {
        if (width == 0) {
            Log.w(this.javaClass.simpleName, "The view has not measure, it will auto init later.")
            return
        }
        if (mGradientAnimator == null) {
            mGradientAnimator = ValueAnimator.ofInt(0, 2 * width)
            mGradientAnimator?.let{
                if (mGradientListener != null) {
                    it.addListener(mGradientListener)
                }
                it.addUpdateListener(AnimatorUpdateListener { animation ->
                    mMatrixTranslate = animation.animatedValue as Int
                    invalidate()
                })
            }

            mLinearGradient = LinearGradient(-width.toFloat(), 0f, 0f, 0f, intArrayOf(mTextColor, mGradientColor, mTextColor), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
            mGradientMatrix = Matrix()
        }
        mGradientAnimator!!.duration = mGradientDuration.toLong()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (visibility == VISIBLE && isAutoPlay) {
            mOffsetAnimator!!.start()
        }
    }

    override fun onDetachedFromWindow() {
        // release animation
        if (mOffsetAnimator != null && mOffsetAnimator!!.isRunning) {
            mOffsetAnimator!!.cancel()
        }
        if (mGradientAnimator != null && mGradientAnimator!!.isRunning) {
            mGradientAnimator!!.cancel()
        }
        super.onDetachedFromWindow()
    }

    /**
     * 监听offset动画状态
     *
     * @param listener AnimatorListener
     */
    fun addOffsetAnimListener(listener: Animator.AnimatorListener?) {
        mOffsetAnimator!!.addListener(listener)
    }

    /**
     * 监听gradient动画状态
     *
     * @param listener AnimatorListener
     */
    fun addGradientAnimListener(listener: Animator.AnimatorListener?) {
        mGradientListener = listener
    }

    /**
     * 开启动画
     */
    fun startAnimation() {
        if (visibility == VISIBLE) {
            mOffsetAnimator?.let {
                if (it.isRunning) {
                    it.cancel()
                }
            }

            isOffsetAnimEnd = false
            mOffsetAnimator?.start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        initLogoCoordinate()
        initGradientAnimation(w)
    }

    private fun initLogoCoordinate() {
        if (mWidth == 0 || mHeight == 0) {
            Log.w(this.javaClass.simpleName, "The view has not measure, it will auto init later.")
            return
        }
        val centerY = mHeight / 2f + mPaint!!.textSize / 2 + mLogoOffset
        // calculate the final xy of the text
        var totalLength = 0f
        for (i in 0 until mLogoTexts.size()) {
            val str = mLogoTexts[i]
            val currentLength = mPaint!!.measureText(str)
            totalLength += if (i != mLogoTexts.size() - 1) {
                currentLength + mTextPadding
            } else {
                currentLength
            }
        }
        // the draw width of the logo must small than the width of this AnimLogoView
        check(totalLength <= mWidth) { "The text of logoName is too large that this view can not display all text" }
        var startX = (mWidth - totalLength) / 2
        if (mQuietPoints.size() > 0) {
            mQuietPoints.clear()
        }
        for (i in 0 until mLogoTexts.size()) {
            val str = mLogoTexts[i]
            val currentLength = mPaint!!.measureText(str)
            mQuietPoints.put(i, PointF(startX, centerY))
            startX += currentLength + mTextPadding
        }
        // generate random start xy of the text
        if (mRadonPoints.size() > 0) {
            mRadonPoints.clear()
        }
        for (i in 0 until mLogoTexts.size()) {
            mRadonPoints.put(i, PointF(Math.random().toFloat() * mWidth, Math.random().toFloat() * mHeight))
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (!isOffsetAnimEnd) { // offset animation
            mPaint!!.alpha = Math.min(255f, 255 * mOffsetAnimProgress + 100).toInt()
            for (i in 0 until mQuietPoints.size()) {
                val quietP = mQuietPoints[i]
                val radonP = mRadonPoints[i]
                val x = radonP.x + (quietP.x - radonP.x) * mOffsetAnimProgress
                val y = radonP.y + (quietP.y - radonP.y) * mOffsetAnimProgress
                canvas.drawText(mLogoTexts[i], x, y, mPaint!!)
            }
        } else { // gradient animation
            for (i in 0 until mQuietPoints.size()) {
                val quietP = mQuietPoints[i]
                canvas.drawText(mLogoTexts[i], quietP.x, quietP.y, mPaint!!)
            }
            mGradientMatrix?.setTranslate(mMatrixTranslate.toFloat(), 0f)
            mLinearGradient?.setLocalMatrix(mGradientMatrix)
        }
    }

    /**
     * 设置logo名
     *
     * @param logoName logo名称
     */
    fun setLogoText(logoName: String?) {
        fillLogoTextArray(logoName)
        // if set the new logoName, should refresh the coordinate again
        initLogoCoordinate()
    }

    /**
     * 设置logo文字动效时长
     *
     * @param duration 动效时长
     */
    fun setOffsetAnimDuration(duration: Int) {
        mOffsetDuration = duration
        initOffsetAnimation()
    }

    /**
     * 设置logo文字渐变动效时长
     *
     * @param duration 动效时长
     */
    fun setGradientAnimDuration(duration: Int) {
        mGradientDuration = duration
        initGradientAnimation(mWidth)
    }

    /**
     * 设置logo文字渐变颜色
     *
     * @param gradientColor 渐变颜色
     */
    fun setGradientColor(gradientColor: Int) {
        mGradientColor = gradientColor
    }

    /**
     * 设置是否显示logo文字渐变
     *
     * @param isShowGradient 是否显示logo渐变动效
     */
    fun setShowGradient(isShowGradient: Boolean) {
        this.isShowGradient = isShowGradient
    }

    /**
     * 设置logo字体边距
     *
     * @param padding 字体边距
     */
    fun setTextPadding(padding: Int) {
        mTextPadding = padding
        initLogoCoordinate()
    }

    /**
     * 设置logo字体颜色
     *
     * @param color 字体颜色
     */
    fun setTextColor(color: Int) {
        mTextColor = color
        initPaint()
    }

    /**
     * 设置logo字体大小
     *
     * @param size 字体大小
     */
    fun setTextSize(size: Int) {
        mTextSize = size
        initPaint()
    }

    companion object {
        private const val DEFAULT_LOGO = "SEAGAZER"
        private const val DEFAULT_TEXT_PADDING = 10
        private const val ANIM_LOGO_DURATION = 1500
        private const val ANIM_LOGO_GRADIENT_DURATION = 1500
        private const val ANIM_LOGO_TEXT_SIZE = 30
        private const val ANIM_LOGO_TEXT_COLOR = Color.BLACK
        private const val ANIM_LOGO_GRADIENT_COLOR = Color.YELLOW
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.AnimLogoView)
        var logoName = ta.getString(R.styleable.AnimLogoView_logoName)
        isAutoPlay = ta.getBoolean(R.styleable.AnimLogoView_autoPlay, true)
        isShowGradient = ta.getBoolean(R.styleable.AnimLogoView_showGradient, false)
        mOffsetDuration = ta.getInt(R.styleable.AnimLogoView_offsetAnimDuration, ANIM_LOGO_DURATION)
        mGradientDuration = ta.getInt(R.styleable.AnimLogoView_gradientAnimDuration, ANIM_LOGO_GRADIENT_DURATION)
        mTextColor = ta.getColor(R.styleable.AnimLogoView_textColor, ANIM_LOGO_TEXT_COLOR)
        mGradientColor = ta.getColor(R.styleable.AnimLogoView_gradientColor, ANIM_LOGO_GRADIENT_COLOR)
        mTextPadding = ta.getDimensionPixelSize(R.styleable.AnimLogoView_textPadding, DEFAULT_TEXT_PADDING)
        mTextSize = ta.getDimensionPixelSize(R.styleable.AnimLogoView_textSize, ANIM_LOGO_TEXT_SIZE)
        mLogoOffset = ta.getDimensionPixelOffset(R.styleable.AnimLogoView_verticalOffset, 0)
        ta.recycle()
        if (TextUtils.isEmpty(logoName)) {
            logoName = DEFAULT_LOGO // default logo
        }
        fillLogoTextArray(logoName)
        initPaint()
        initOffsetAnimation()
    }
}