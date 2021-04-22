package com.basis.base.activity

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.basis.base.R
import com.basis.base.base.BaseApplication
import com.basis.base.dialog.LoadingDialog
import com.basis.base.utils.LogUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.RomUtils
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil

open abstract class CommonActivity : AppCompatActivity() {

    lateinit var mActivityProvider: ViewModelProvider
    lateinit var mApplicationProvider: ViewModelProvider
    var mDialog: LoadingDialog? = null
    var isUseRubbishScreen = false
    var mIvBack: View? = null
    var mTvTitle: TextView? = null
    var mConstraintLayout: ConstraintLayout? = null

    /**
     * ActionBar 标题
     */
    var actionBarTitle = ""

    /**
     * ActionBar 字体大小
     */
    var actionBarTitleSize = 30f

    /**
     * ActionBar 背景颜色
     */
    var actionBarBackground = R.color.white

    /**
     * ActionBar 是否显示下划线
     */
    var actionBarLineVisibility=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActionBar()
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.color_ffd727))
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        isUseRubbishScreen = setisUseRubbishScreen()
        checkScreen()
    }

    /**
     *   设置actionbar
     *   如果布局被遮挡 在布局最外层加上android:fitsSystemWindows="true"
     */
  private  fun initActionBar(){
        if (showActionBar()) {
            supportActionBar?.apply {
                val view = View.inflate(this@CommonActivity, R.layout.layout_actionbar, null)
                mIvBack = view.findViewById(R.id.iv_back)
                mTvTitle = view.findViewById(R.id.tv_title)
                view.findViewById<View>(R.id.view_line)?.visibility=if (actionBarLineVisibility) View.VISIBLE else View.INVISIBLE
                mConstraintLayout = view.findViewById(R.id.cl_layout)
                mTvTitle?.setText(actionBarTitle)
                mTvTitle?.setTextSize(actionBarTitleSize)
                mConstraintLayout?.setBackgroundColor(resources.getColor(actionBarBackground))
                this.setCustomView(
                    view, ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                this.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
                mIvBack?.setOnClickListener {
                    finish()
                }
                view.parent?.let {
                    if (it is Toolbar) {
                        it.setPadding(0, 0, 0, 0)
                        it.setContentInsetsAbsolute(0, 0)
                    }
                }
            }
        } else {
            supportActionBar?.hide()
        }
    }

    /**
     * 刘海屏模式 true 模式会让屏幕到延申刘海区域中
     *  false 模式不会让屏幕到延申刘海区域中，会留出一片黑色区域
     */
    open fun setisUseRubbishScreen(): Boolean {
        return false
    }

    /**
     * 是否显示actionbar  默认不显示
     * 如果布局被遮挡 在布局最外层加上 android:fitsSystemWindows="true"
     */
    open fun showActionBar(): Boolean {
        return false
    }

    fun showLoadingDialog() {
        if (mDialog == null) {
            mDialog = LoadingDialog()
        }
        mDialog?.show(supportFragmentManager, this.javaClass.name)
    }

    fun dismissLoadingDialog() {
        mDialog?.dialog?.let {
            if (it.isShowing) {
                mDialog?.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("mine", "onDestroy" + this::class.java.name)
        dismissLoadingDialog()
    }

    fun changeFragment(fragment: Fragment, view: Int) {
        val name = fragment.javaClass.name
        val fragments = supportFragmentManager.fragments
        val beginTransaction = supportFragmentManager.beginTransaction()
        fragments?.apply {
            this.forEach {
                if (it != null && !name.equals(it.javaClass.name)) {
                    beginTransaction.hide(it)
                }
            }
        }
        val findFragmentByTag = supportFragmentManager.findFragmentByTag(name)
        if (findFragmentByTag == null) {
            beginTransaction.add(view, fragment, name)
        } else {
            beginTransaction.show(findFragmentByTag)
        }
        beginTransaction?.commitAllowingStateLoss()
        supportFragmentManager.executePendingTransactions()
    }

    /**
     * 移除Fragment
     */
    @Synchronized
    open fun removeFragment(fragment: Fragment) {
        try {
            val className = fragment.javaClass.name
            val fragmentManager = supportFragmentManager
            val ft = fragmentManager.beginTransaction()
            val fragmentShow =
                fragmentManager.findFragmentByTag(className)
            if (fragmentShow != null) {
                ft.remove(fragmentShow)
                ft.commitAllowingStateLoss()
            }
        } catch (e: Exception) {
        }
    }


    //TODO tip 2: Jetpack 通过 "工厂模式" 来实现 ViewModel 的作用域可控，
    //目前我们在项目中提供了 Application、Activity、Fragment 三个级别的作用域，
    //值得注意的是，通过不同作用域的 Provider 获得的 ViewModel 实例不是同一个，
    //所以如果 ViewModel 对状态信息的保留不符合预期，可以从这个角度出发去排查 是否眼前的 ViewModel 实例不是目标实例所致。
    protected open fun <T : ViewModel?> getActivityScopeViewModel(modelClass: Class<T>): T {
        if (!this::mActivityProvider.isInitialized) {
            mActivityProvider = ViewModelProvider(this)
        }
        return mActivityProvider.get(modelClass)
    }

    protected open fun <T : ViewModel?> getApplicationScopeViewModel(modelClass: Class<T>): T {
        if (!this::mApplicationProvider.isInitialized) {
            mApplicationProvider = ViewModelProvider(
                (this.applicationContext as BaseApplication),
                getAppFactory(this)
            )
        }
        return mApplicationProvider[modelClass]
    }

    fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        val application: Application = checkApplication(activity)
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    /**
     * 刘海屏适配
     */

    fun checkScreen() {
        if (DeviceUtils.getSDKVersionCode() >= 28) {
            checkIsCanuseRubbishScreen()
            val lp = window.attributes
            lp?.let {
                if (isUseRubbishScreen) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        it.layoutInDisplayCutoutMode =
                            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        it.layoutInDisplayCutoutMode =
                            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
                    }
                }
                window.attributes = it
            }
        }
    }

    /**
     * 刘海屏只适配华为 小米 ov
     */
    fun checkIsCanuseRubbishScreen() {
        if (isUseRubbishScreen && !RomUtils.isHuawei() && !RomUtils.isXiaomi() && !RomUtils.isOppo() && !RomUtils.isVivo()) {
            isUseRubbishScreen = false
        }
    }

    /**
     * 检查权限
     *
     * @param permission
     * @param listener
     */
    open fun requestCheckPermission(
        listener: PermissionListener?,
        permission: String,
        readExternalStorage: String,
        writeExternalStorage: String
    ) {
        if (PermissionsUtil.hasPermission(
                this,
                permission,
                readExternalStorage,
                writeExternalStorage
            )
        ) {
            if (listener != null) {
                listener.permissionGranted(arrayOf(permission))
            }
        } else {
            PermissionsUtil.requestPermission(
                this,
                listener,
                permission,
                readExternalStorage,
                writeExternalStorage
            )
        }
    }


}