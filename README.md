# JsBridgeKt
I try to refactor [lzyzsd](https://github.com/lzyzsd)的[JsBridge](https://github.com/lzyzsd/JsBridge) to the kotlin. 
I decoupled BridgeWebView(BridgeWebView extends WebView) and developer don't need to extends BridgeWebView.
Developers can freely choose the native WebView or Tencent X5 core WebView(TBS) to extends, just implements WebViewJavascriptBridge and create JsBridgeManager object

## Language
- [English](https://github.com/LZ9/JsBridgeKt)
- [中文](https://github.com/LZ9/JsBridgeKt/blob/master/README_CN.md)

## Directory
- [1、How to dependencies](https://github.com/LZ9/JsBridgeKt#1how-to-dependencies)
- [2、How to use in Android](https://github.com/LZ9/JsBridgeKt#2how-to-use-in-android)
- [3、How to use in JavaScript](https://github.com/LZ9/JsBridgeKt#3how-to-use-in-javascript)
- [Extends](https://github.com/LZ9/JsBridgeKt#extends)

## 1、How to dependencies
Please add mavenCentral() dependencies, because jcenter() is deprecated
```
repositories {
    ...
    mavenCentral()
    ...
}
```
You can add these code in the dependencies(build.gradle)
```
implementation 'ink.lodz:jsbridge-kt:1.0.4'
```

## 2、How to use in Android
I create a SimpleBridgeWebView extends native WebView and you can use it if you don't need to custom WebView. The usage is as follows:
#### 1）Use SimpleBridgeWebView in the layout file
```
 <com.lodz.android.jsbridgekt.SimpleBridgeWebView
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
#### 2）Send data to JS
Assign a API name like xxx to send data to JS
```
val msg = "12345"
webView.send("xxxx", msg) {data->
    // js callback data          
}
```
You can send data to JS by default API name when hasn't custom API name
```
val msg = "12345"
webView.send(data = msg)
```
#### 3）Subscribe to data from JS
Receive data with the custom API name like xxxx sent by JS
```
webView.register("xxxx") { data, listener ->
    // js callback data   
    // listener is callback object,you can send result to JS 
    listener.callbackJs("app get param")
}
```
Receive data with the default API name sent by JS
```
webView.register { data, listener ->
    // js callback data   
    // listener is callback object,you can send result to JS 
    listener.callbackJs("app get param")
}
```

---

If you want to use JSBridge in your custom WebView, just like this:
```
// Extends your own WebView and implements WebViewJavascriptBridge interface
class CustomWebView : WebView, WebViewJavascriptBridge {
    // create a JsBridgeManager object
    private lateinit var mJsBridgeManager :JsBridgeManager
    
    init {
        mJsBridgeManager = JsBridgeManager(this) 
        webViewClient = BridgeWebViewClient(this) //This step is very important because bridge logic is implemented in BridgeWebViewClient
        
    }
    
    // implements WebViewJavascriptBridge function
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

## 3、How to use in JavaScript
The Instructions of H5 can refer to the [JsBridge.js](https://github.com/LZ9/JsBridgeKt/blob/master/app/src/main/assets/JsBridge.js), which includes send and subscribe

## Extends
- [Update record](https://github.com/LZ9/JsBridgeKt/blob/master/jsbridgekt/readme_update.md)
- [Back to the top](https://github.com/LZ9/JsBridgeKt#jsbridgekt)

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