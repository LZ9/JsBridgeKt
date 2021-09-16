package com.lodz.android.jsbridgekt

/**
 * @author zhouL
 * @date 2021/8/23
 */
class MessageBean {
    /** 请求id */
    var callbackId: String = ""
    fun callbackIdKey() = "callbackId"

    /** 请求参数 */
    var data: String = ""
    fun dataKey() = "data"

    /** 响应id */
    var responseId: String = ""
    fun responseIdKey() = "responseId"

    /** 响应数据 */
    var responseData: String = ""
    fun responseDataKey() = "responseData"

    /** 回调方法的名称 */
    var handlerName: String = ""
    fun handlerNameKey() = "handlerName"

}