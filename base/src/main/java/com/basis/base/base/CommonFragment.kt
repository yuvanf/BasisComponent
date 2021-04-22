package com.basis.base.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.basis.base.dialog.LoadingDialog
import com.basis.base.utils.LogUtils


abstract class CommonFragment : Fragment() {
    lateinit var mActivity: AppCompatActivity
    lateinit var mFragmentProvider: ViewModelProvider
    lateinit var mActivityProvider: ViewModelProvider
    lateinit var mApplicationProvider: ViewModelProvider


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }



    /**
     * @param view 要替换的布局id
     * @param fragment 新的fragment isNew为false 有旧的会直接用旧的
     * @param bundle 携带的bundle
     * @param isNew 是否直接用新的 true 是
     * @param fragmentClass 要移除的f
     *
     * ragment 例如 ConfirmRegFragment.classMutableList
     */
    open fun startFragment(view: Int, fragment: Fragment, bundle: Bundle?, isNew: Boolean, fragmentClass: Class<*>?) {
/*
        val fragmentManager = fragmentManager
        val ft: FragmentTransaction? = fragmentManager?.beginTransaction()
        fragment.arguments = bundle
        ft?.replace(view, fragment, fragment.javaClass.name)
        ft?.addToBackStack(null)
        ft!!.commitAllowingStateLoss()*/

        try {
            val className = fragment.javaClass.name
            val fragmentManager = parentFragmentManager
            val fragments = fragmentManager.fragments
            val ft = fragmentManager.beginTransaction()
//            ft.setCustomAnimations(
//                R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit,
//                R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit);
            var isAdd = false
            if (fragments != null) {
                for (one in fragments) {
                    if (one != null && className != one.javaClass.name) {
                        ft.hide(one)
                    } else if (one != null && className == one.javaClass.name) {
                        isAdd = true
                    }
                }
            }
            var fragmentShow = fragmentManager.findFragmentByTag(className)
            if (isNew && fragmentShow != null) {
                ft.remove(fragmentShow)
                fragmentShow = null
                isAdd = false
            }
            if (fragmentShow == null) {
                if (!isAdd && !fragment.isAdded) {
                    fragment.arguments = bundle
                    ft.add(view, fragment, className)
                }
            } else {
                fragmentShow.arguments = bundle
                ft.show(fragmentShow)
            }
            if (fragmentClass != null) {
                val removeClassName = fragmentClass.name
                val removeFragmentShow = fragmentManager.findFragmentByTag(removeClassName)
                if (removeFragmentShow != null) {
                    ft.remove(removeFragmentShow)
                }
            }
            ft.addToBackStack(null)
            ft.commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        } catch (e: Exception) {
        }
    }


    open fun startFragment(view: Int, fragment: Fragment?) {
        startFragment(view, fragment, null)
    }

    open fun startFragment(view: Int, fragment: Fragment?, bundle: Bundle?) {
        startFragment(view, fragment!!, bundle, true, null)
    }

    open fun startFragment(view: Int ,fragment: Fragment?, bundle: Bundle?, isNew: Boolean) {
        startFragment(view, fragment!!, bundle, isNew, null)
    }

    open fun startFragment(view: Int, fragment: Fragment?, isNew: Boolean) {
        startFragment(view, fragment!!, null, isNew, null)
    }

    open fun startFragment(view: Int, fragment: Fragment?, isNew: Boolean, fragmentClass: Class<*>?) {
        startFragment(view, fragment!!, null, isNew, fragmentClass)
    }

    protected open fun <T : ViewModel?> getFragmentScopeViewModel(modelClass: Class<T>): T {
        if (!this::mFragmentProvider.isInitialized) {
            mFragmentProvider = ViewModelProvider(this)
        }
        return mFragmentProvider.get(modelClass)
    }

    protected open fun <T : ViewModel?> getActivityScopeViewModel(modelClass: Class<T>): T {
        if (!this::mActivityProvider.isInitialized) {
            mActivityProvider = ViewModelProvider(mActivity)
        }
        return mActivityProvider?.get(modelClass)
    }

    fun <T : ViewModel?> getApplicationScopeViewModel(modelClass: Class<T>): T {
        if (!this::mApplicationProvider.isInitialized) {
            mApplicationProvider = ViewModelProvider(
                (mActivity.applicationContext as BaseApplication),
                getApplicationFactory(mActivity)
            )
        }
        return mApplicationProvider[modelClass]
    }

     open fun getApplicationFactory(activity: Activity): ViewModelProvider.Factory {
        checkActivity(this)
        val application = checkApplication(activity)
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

     open fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

     open fun checkActivity(fragment: Fragment) {
        val activity = fragment.activity
            ?: throw java.lang.IllegalStateException("Can't create ViewModelProvider for detached fragment")
    }

    /**
     * 移除Fragment
     */
    open fun removeFragment(fragmentClass: Class<*>) {
        try {
            val className = fragmentClass.name
            val fragmentManager = fragmentManager
            val ft = fragmentManager!!.beginTransaction()
            val fragmentShow =
                fragmentManager.findFragmentByTag(className)
            if (fragmentShow != null) {
                ft.remove(fragmentShow)
                ft.commitAllowingStateLoss()
            }
        } catch (e: java.lang.Exception) {
        }
    }

    var mDialog: LoadingDialog?=null
    fun showLoadingDialog(){
        if (mDialog==null){
            mDialog= LoadingDialog()
        }
        fragmentManager?.let { mDialog?.show(it,this.javaClass.name) }
    }

    fun dismissLoadingDialog(){
        mDialog?.dialog?.let {
            if (it.isShowing){
                mDialog?.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("mine",""+this::class.java.name)
        removeFragment(this::class.java)

    }


}