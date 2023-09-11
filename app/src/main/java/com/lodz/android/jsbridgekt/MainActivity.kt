package com.lodz.android.jsbridgekt

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.activity.result.contract.ActivityResultContracts
import com.lodz.android.corekt.anko.append
import com.lodz.android.corekt.anko.getColorCompat
import com.lodz.android.corekt.anko.getVersionCode
import com.lodz.android.corekt.anko.getVersionName
import com.lodz.android.corekt.log.PrintLog
import com.lodz.android.corekt.utils.DateUtils
import com.lodz.android.jsbridgekt.databinding.ActivityMainBinding
import com.lodz.android.pandora.base.activity.BaseActivity
import com.lodz.android.pandora.utils.jackson.toJsonString
import com.lodz.android.pandora.utils.viewbinding.bindingLayout
import com.lodz.android.pandora.widget.base.TitleBarLayout

class MainActivity : BaseActivity() {

    /** JS交互测试页  */
    private val TEST_JS_BRIDGE = "file:///android_asset/JsBridgeDemo"

    /** 文件上传回调  */
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null

    private val mBinding: ActivityMainBinding by bindingLayout(ActivityMainBinding::inflate)

    override fun getViewBindingLayout(): View = mBinding.root

    override fun findViews(savedInstanceState: Bundle?) {
        super.findViews(savedInstanceState)
        initTitleBarLayout(getTitleBarLayout())
        initWebView()
    }

    private fun initTitleBarLayout(titleBarLayout: TitleBarLayout) {
        titleBarLayout.setTitleName(R.string.app_name)
        titleBarLayout.needBackButton(false)
        titleBarLayout.setBackgroundColor(getColorCompat(R.color.purple_700))
    }

    private fun initWebView() {
        mBinding.webView.loadUrl(TEST_JS_BRIDGE)
        mBinding.webView.settings.defaultTextEncodingName = "UTF-8"
        mBinding.webView.settings.loadsImagesAutomatically = true
        mBinding.webView.settings.setSupportZoom(false)
        mBinding.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        mBinding.webView.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                mFilePathCallback = filePathCallback
                mGetImgResult.launch("image/*")
                return true
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                PrintLog.d("console", consoleMessage?.message() ?: "")
                return super.onConsoleMessage(consoleMessage)
            }
        }
    }

    /** 删除图片的ActivityResult回调 */
    private val mGetImgResult = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (mFilePathCallback == null) {
            return@registerForActivityResult
        }
        mFilePathCallback?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        mFilePathCallback = null
    }

    override fun setListeners() {
        super.setListeners()

        mBinding.cleanLogBtn.setOnClickListener {
            mBinding.resultTv.text = ""
        }

        mBinding.sendCustomBtn.setOnClickListener {
            val data = AppInfoBean(getVersionName(), getVersionCode()).toJsonString()
            val log = "发送给web：$data"
            appendLog(log)
            mBinding.webView.send("getAppInfo", data) {
                appendLog(log.append("\n").append("收到web数据：$it"))
            }
        }

        mBinding.sendDefBtn.setOnClickListener {
            val msg = "{\"time\":${DateUtils.getCurrentFormatString(DateUtils.TYPE_4)}}"
            val log = "java 发给web ：$msg"
            appendLog(log)
            mBinding.webView.send(data = msg) {
                appendLog(log.append("\n").append("收到web数据：$it"))
            }
        }

        mBinding.webView.register("login") { data, listener ->
            val result = "{\"code\":500,\"msg\":\"fail\"}"
            appendLog("收到web数据：$data".append("\n").append("返回给web：$result"))
            listener.callbackJs(result)
        }

        mBinding.webView.register { data, listener ->
            val result = "{\"code\":200,\"msg\":\"success\"}"
            appendLog("收到web数据：$data".append("\n").append("返回给web：$result"))
            listener.callbackJs(result)
        }
    }

    override fun initData() {
        super.initData()
        showStatusCompleted()
    }

    override fun finish() {
        super.finish()
        mBinding.webView.loadDataWithBaseURL(null, "","text/html", "utf-8", null)
        mBinding.webView.clearHistory()
        mBinding.webView.clearCache(true)
        mBinding.webView.destroy()
    }

    private fun appendLog(log: String) {
        mBinding.resultTv.text = log
    }
}
