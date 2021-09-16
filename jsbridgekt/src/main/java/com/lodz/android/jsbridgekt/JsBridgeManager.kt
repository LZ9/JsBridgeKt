package com.lodz.android.jsbridgekt

import android.os.Looper
import android.webkit.WebView
import com.lodz.android.jsbridgekt.contract.OnBridgeReceiveListener
import com.lodz.android.jsbridgekt.contract.OnCallBackJsListener
import com.lodz.android.jsbridgekt.contract.OnReceiveJsListener
import com.lodz.android.jsbridgekt.contract.WebViewJavascriptBridge
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author zhouL
 * @date 2021/9/7
 */
open class JsBridgeManager(private val webview: WebView) : WebViewJavascriptBridge {

    private val DEFAULT_RECEIVE_API_NAME = "default_receive"

    /** 回调JS接口缓存集合 */
    private val mCallBackJsMap: HashMap<String, OnCallBackJsListener> = HashMap()
    /** 接收JS数据接口缓存集合 */
    private val mReceiveJsMap: HashMap<String, OnReceiveJsListener> = HashMap()
    /** JsBridge接收接口缓存集合 */
    private val mBridgeReceiveMap: HashMap<String, OnBridgeReceiveListener> = HashMap()

    override fun register(apiName: String, handler: OnReceiveJsListener) {
        val name =  if (apiName.isEmpty()) DEFAULT_RECEIVE_API_NAME else apiName
        mReceiveJsMap[name] = handler
    }

    override fun send(apiName: String, data: String, function: OnCallBackJsListener?) {
        val message = MessageBean()
        message.data = data
        if (function != null) {
            val callbackId = String.format(BridgeUtil.CALLBACK_ID_FORMAT, System.currentTimeMillis())
            mCallBackJsMap[callbackId] = function
            message.callbackId = callbackId
        }
        message.handlerName = apiName
        sendMessageToJs(message)
    }

    /** 发送数据给H5 */
    private fun sendMessageToJs(message: MessageBean) {
        val jsonObject = JSONObject()
        jsonObject.put(message.callbackIdKey(), message.callbackId)
        jsonObject.put(message.dataKey(), message.data)
        jsonObject.put(message.responseIdKey(), message.responseId)
        jsonObject.put(message.responseDataKey(), message.responseData)
        jsonObject.put(message.handlerNameKey(), message.handlerName)

        val json = jsonObject.toString()
            .replace("(\\\\)([^utrn])".toRegex(), "\\\\\\\\$1$2")
            .replace("(?<=[^\\\\])(\")".toRegex(), "\\\\\"")

        val javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, json)
        if (Thread.currentThread() == Looper.getMainLooper().thread){
            webview.loadUrl(javascriptCommand)
        }
    }

    override fun registerBridgeReceive() {
        if (Thread.currentThread() != Looper.getMainLooper().thread) {
            return
        }
        webview.loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA)
        mBridgeReceiveMap[BridgeUtil.getJsBridgeName()] = OnBridgeReceiveListener {
            val list = parseArray(it)
            if (list.isNullOrEmpty()) {
                return@OnBridgeReceiveListener
            }
            for (item in list) {
                val responseId = item.responseId
                if (responseId.isNotEmpty()) {
                    val function = mCallBackJsMap[responseId]
                    val responseData = item.responseData
                    function?.callbackJs(responseData)
                    mCallBackJsMap.remove(responseId)
                } else {
                    val responseFunction = OnCallBackJsListener {
                        if (item.callbackId.isNotEmpty()) {
                            val responseMsg = MessageBean()
                            responseMsg.responseId = item.callbackId
                            responseMsg.responseData = it
                            sendMessageToJs(responseMsg)
                        }
                    }
                    val apiName = if (item.handlerName.isEmpty()) DEFAULT_RECEIVE_API_NAME else item.handlerName
                    mReceiveJsMap[apiName]?.onReceive(item.data, responseFunction)
                }
            }
        }
    }

    private fun parseArray(json: String): ArrayList<MessageBean> {
        val list = ArrayList<MessageBean>()
        val jsonArray = JSONArray(json)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val bean = MessageBean()
            bean.callbackId = if (jsonObject.has(bean.callbackIdKey())) jsonObject.getString(bean.callbackIdKey()) else ""
            bean.data = if (jsonObject.has(bean.dataKey())) jsonObject.getString(bean.dataKey()) else ""
            bean.responseId = if (jsonObject.has(bean.responseIdKey())) jsonObject.getString(bean.responseIdKey()) else ""
            bean.responseData = if (jsonObject.has(bean.responseDataKey())) jsonObject.getString(bean.responseDataKey()) else ""
            bean.handlerName = if (jsonObject.has(bean.handlerNameKey())) jsonObject.getString(bean.handlerNameKey()) else ""
            list.add(bean)
        }
        return list
    }

    override fun handlerJsReturnData(url: String) {
        val jsBridgeName = BridgeUtil.getJsBridgeNameFromReturnUrl(url)
        val function = mBridgeReceiveMap[jsBridgeName]
        if (function != null) {
            function.onReceive(BridgeUtil.getDataFromReturnUrl(url) ?: "")
            mBridgeReceiveMap.remove(jsBridgeName)
        }
    }

}