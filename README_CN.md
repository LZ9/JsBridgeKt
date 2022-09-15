# JsBridgeKt库
我尝试将[lzyzsd](https://github.com/lzyzsd)的[JsBridge](https://github.com/lzyzsd/JsBridge)库转为了kotlin版本。内置的交互协议还是一样的，并且和WebView做了解耦，无须固定继承BridgeWebView，你可以自己决定继承Android原生的WebView或者腾讯X5内核的WebView，实现和配置JsBridgeManager即可。

## 语言
- [English](https://github.com/LZ9/JsBridgeKt)
- [中文](https://github.com/LZ9/JsBridgeKt/blob/master/README_CN.md)

## 目录
- [1、引用方式](https://github.com/LZ9/JsBridgeKt/blob/master/README_CN.md#1引用方式)
- [2、Android端使用方式](https://github.com/LZ9/JsBridgeKt/blob/master/README_CN.md#2android端使用方式)
- [3、JavaScript使用方式](https://github.com/LZ9/JsBridgeKt/blob/master/README_CN.md#3javascript使用方式)
- [扩展](https://github.com/LZ9/JsBridgeKt/blob/master/README_CN.md#扩展)

## 1、引用方式
由于jcenter删库跑路，请大家添加mavenCentral依赖
```
repositories {
    ...
    mavenCentral()
    ...
}
```
在你需要调用的module里的dependencies中加入以下依赖
```
implementation 'ink.lodz:jsbridge-kt:1.0.4'
```

## 2、Android端使用方式
如果你想要快捷的使用Android原生的WebView来加载页面，我提供了SimpleBridgeWebView，内部实现了JS交互逻辑，使用方法如下：
#### 1）在布局文件中使用SimpleBridgeWebView
```
 <com.lodz.android.jsbridgekt.SimpleBridgeWebView
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
#### 2）发送给JS
指定接口名xxxx将数据data发送给js
```
val msg = "12345"
webView.send("xxxx", msg) {data->
    // 这里接收JS返回的数据            
}
```
数据data发送给js，不指定具体接口名，使用内置默认接口名
```
val msg = "12345"
webView.send(data = msg)
```
#### 3）订阅JS来的数据
接收JS发送来的接口名为xxxx的数据
```
webView.register("xxxx") { data, listener ->
    // data是JS发送过来的数据
    // listener是回调对象，可将本次数据的处理结果返回给JS
    listener.callbackJs("app get param")
}
```
接收JS发送来的使用内置默认接口名的数据
```
webView.register { data, listener ->
    // data是JS发送过来的数据
    // listener是回调对象，可将本次数据的处理结果返回给JS
    listener.callbackJs("app get param")
}
```

---

如果你想要自定义自己的WebView，可以使用以下方法来实现JS交互：
```
// 继承你需要的WebView，实现WebViewJavascriptBridge接口
class CustomWebView : WebView, WebViewJavascriptBridge {
    // 声明JsBridgeManager对象
    private lateinit var mJsBridgeManager :JsBridgeManager
    
    init {
        mJsBridgeManager = JsBridgeManager(this) // 对象赋值
        webViewClient = BridgeWebViewClient(this) //这步非常重要，JS的拦截逻辑是在BridgeWebViewClient里实现的
    }
    
    // 实现接口方法
    override fun send(apiName: String, data: String, function: OnCallBackJsListener?) {
        mJsBridgeManager.send(apiName, data, function)
    }

    override fun register(apiName: String, handler: OnReceiveJsListener) {
        mJsBridgeManager.register(apiName, handler)
    }

    override fun handlerJsReturnData(url: String) {
        mJsBridgeManager.handlerJsReturnData(url)
    }

    override fun registerBridgeReceive() {
        mJsBridgeManager.registerBridgeReceive()
    }
}
```

## 3、JavaScript使用方式
前端的调用方式可以参考[JsBridge.js](https://github.com/LZ9/JsBridgeKt/blob/master/app/src/main/assets/JsBridge.js)这个文件，里面同样包含了发送和订阅。

## 扩展

- [更新记录](https://github.com/LZ9/JsBridgeKt/blob/master/jsbridgekt/readme_update.md)
- [回到顶部](https://github.com/LZ9/JsBridgeKt/blob/master/README_CN.md#jsbridgekt库)

## License
- [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

Copyright 2022 Lodz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.