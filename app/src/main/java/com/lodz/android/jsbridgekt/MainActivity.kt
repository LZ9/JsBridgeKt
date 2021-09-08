package com.lodz.android.jsbridgekt

import android.os.Bundle
import android.view.View
import com.lodz.android.corekt.anko.getColorCompat
import com.lodz.android.jsbridgekt.databinding.ActivityMainBinding
import com.lodz.android.pandora.base.activity.BaseActivity
import com.lodz.android.pandora.utils.viewbinding.bindingLayout

class MainActivity : BaseActivity() {

    private val mBinding: ActivityMainBinding by bindingLayout(ActivityMainBinding::inflate)

    override fun getViewBindingLayout(): View = mBinding.root

    override fun findViews(savedInstanceState: Bundle?) {
        super.findViews(savedInstanceState)
        getTitleBarLayout().setTitleName(R.string.app_name)
        getTitleBarLayout().needBackButton(false)
        getTitleBarLayout().setBackgroundColor(getColorCompat(R.color.purple_700))
    }

    override fun initData() {
        super.initData()
        showStatusCompleted()
    }

}
