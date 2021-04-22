package com.basis.base.base

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

open class BaseApplication: Application(), ViewModelStoreOwner {

    lateinit var mAppViewModelStore:ViewModelStore

    override fun onCreate() {
        super.onCreate()
        context=this
    }

    companion object{
      lateinit var   context:Context
    }

    override fun getViewModelStore(): ViewModelStore {
       if (!this::mAppViewModelStore.isInitialized){
           mAppViewModelStore=ViewModelStore()
       }
        return mAppViewModelStore
    }

}