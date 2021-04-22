package com.basis.component

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.basis.base.activity.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun init() {

    }

    override fun showActionBar(): Boolean {
        actionBarTitle="首页"
        return true
    }
}