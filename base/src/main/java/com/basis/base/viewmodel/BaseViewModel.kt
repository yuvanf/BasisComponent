package com.basis.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

 open class BaseViewModel(): ViewModel() {

  /**
   * 是否是刘海屏
   */
  var isRubbishScreen : MutableLiveData<Boolean> = MutableLiveData()

  /**
   * 调出弹窗
   */
  var isShow : MutableLiveData<Boolean> = MutableLiveData()
  /**
   * 取消某种操作 例如弹窗
   */
  var isCancel : MutableLiveData<Boolean> = MutableLiveData()

}