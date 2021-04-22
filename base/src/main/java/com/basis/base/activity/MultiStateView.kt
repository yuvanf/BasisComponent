package com.basis.base.activity

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout

import com.basis.base.R

/**
 *
 * @描述 多状态View
 */
class MultiStateView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context!!, attrs, defStyleAttr) {
    private var mLoadingViewResId = 0
    private var mEmptyViewResId = 0
    private var mErrorViewResId = 0
    private var mLoadingView: View? = null
    private var mEmptyView: View? = null
    private var mErrorView: View? = null
    private var mViewState = 0
    private fun init(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MultiStateView)
        mLoadingViewResId = a.getResourceId(R.styleable.MultiStateView_state_loadingView, -1)
        mEmptyViewResId = a.getResourceId(R.styleable.MultiStateView_state_emptyView, -1)
        mErrorViewResId = a.getResourceId(R.styleable.MultiStateView_state_errorView, -1)
        val state = a.getInt(
            R.styleable.MultiStateView_state_viewState,
            STATE_LOADING
        )
        initView(state)
        a.recycle()
    }

    fun initView(state: Int) {
        if (mLoadingViewResId != -1) {
            mLoadingView = LayoutInflater.from(context).inflate(mLoadingViewResId, null)
            addView(mLoadingView)
        }
        if (mEmptyViewResId != -1) {
            mEmptyView = LayoutInflater.from(context).inflate(mEmptyViewResId, null)
            addView(mEmptyView)
        }
        if (mErrorViewResId != -1) {
            mErrorView = LayoutInflater.from(context).inflate(mErrorViewResId, null)
            mErrorView?.setOnClickListener(OnClickListener {
                setViewState(STATE_LOADING)
                mOnStateViewListener?.reload()
            })
            addView(mErrorView)
        }
        mViewState = state
        setView()
    }

    fun setViewState(state: Int) {
        if (state != mViewState) {
            mViewState = state
            setView()
        }
    }

    private fun setView() {
        when (mViewState) {
            STATE_LOADING -> {
                if (mLoadingView == null) {
                    return
                }
                mLoadingView?.visibility = View.VISIBLE
                mErrorView?.visibility = View.GONE
                mEmptyView?.visibility = View.GONE
                mOnStateViewListener?.stateLoad()
            }
            STATE_EMPTY -> {
                if (mEmptyView == null) {
                    return
                }
                mEmptyView?.visibility = View.VISIBLE
                mLoadingView?.visibility = View.GONE
                mErrorView?.visibility = View.GONE
                mOnStateViewListener?.stateEmpty()
            }
            STATE_ERROR -> {
                if (mErrorView == null) {
                    return
                }
                mErrorView?.visibility = View.VISIBLE
                mLoadingView?.visibility = View.GONE
                mEmptyView?.visibility = View.GONE
                mOnStateViewListener?.stateError()
            }
            STATE_CONTENT -> {
                mLoadingView?.visibility = View.GONE
                mErrorView?.visibility = View.GONE
                mEmptyView?.visibility = View.GONE
                mOnStateViewListener?.stateContent()
            }
            else -> {
                mLoadingView?.visibility = View.GONE
                mErrorView?.visibility = View.GONE
                mEmptyView?.visibility = View.GONE
                mOnStateViewListener?.stateContent()
            }
        }
    }

    fun setLoadingResId(resId: Int) {
        mLoadingViewResId = resId
    }

    fun setEmptyViewResId(resId: Int) {
        mEmptyViewResId = resId
    }

    fun setErrorViewResId(resId: Int) {
        mErrorViewResId = resId
    }

    private var mOnStateViewListener: OnStateViewListener? = null

    fun setOnStateViewListener(mOnStateViewListener: OnStateViewListener?) {
        this.mOnStateViewListener = mOnStateViewListener
    }

    interface OnStateViewListener {
        // 点击重新加载
        fun reload()

        // 加载中
        fun stateLoad()

        // 空布局
        fun stateEmpty()

        // 错误页面
        fun stateError()

        // 内容页
        fun stateContent()
    }

    companion object {
        const val STATE_CONTENT = 0
        const val STATE_LOADING = 1
        const val STATE_EMPTY = 2
        const val STATE_ERROR = 3
    }

    init {
        init(attrs)
    }
}