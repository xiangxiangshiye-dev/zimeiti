package com.xiangxiang.bookkeeping

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import android.util.Base64
import java.io.File
import java.io.FileOutputStream

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private var uploadMessage: ValueCallback<Array<Uri>>? = null
    private val FILE_CHOOSER_REQUEST_CODE = 1001

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                uploadMessage?.onReceiveValue(null)
                uploadMessage = filePathCallback

                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                }

                return try {
                    startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE)
                    true
                } catch (e: Exception) {
                    uploadMessage?.onReceiveValue(null)
                    uploadMessage = null
                    false
                }
            }
        }
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(AppBridge(this, webView), "AndroidApp")
        webView.loadUrl("file:///android_asset/index.html")
    }

    @Deprecated("Deprecated in Android")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            val callback = uploadMessage
            uploadMessage = null

            if (resultCode == RESULT_OK && data?.data != null) {
                callback?.onReceiveValue(arrayOf(data.data!!))
            } else {
                callback?.onReceiveValue(null)
            }
        }
    }

    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }

    private class AppBridge(private val context: Context, private val webView: WebView) {
        @JavascriptInterface
        fun saveFile(fileName: String, mimeType: String, base64Data: String) {
            try {
                val safeName = fileName.substringAfterLast('/').substringAfterLast('\\')
                    .replace(Regex("[^\\p{L}\\p{N}._-]"), "_")
                    .ifBlank { "export.dat" }
                val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                val downloads = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
                    ?: context.filesDir
                if (!downloads.exists()) downloads.mkdirs()
                val file = File(downloads, safeName)
                FileOutputStream(file).use { it.write(bytes) }
                (context as? Activity)?.runOnUiThread {
                    Toast.makeText(context, "已导出到应用文件目录：${file.name}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                (context as? Activity)?.runOnUiThread {
                    Toast.makeText(context, "导出失败：" + (e.message ?: "未知错误"), Toast.LENGTH_LONG).show()
                }
            }
        }

        @JavascriptInterface
        fun printPage() {
            (context as? Activity)?.runOnUiThread {
                try {
                    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                    val adapter = webView.createPrintDocumentAdapter("收费单据")
                    printManager.print(
                        "收费单据",
                        adapter,
                        PrintAttributes.Builder().build()
                    )
                } catch (e: Exception) {
                    Toast.makeText(context, "无法打开打印服务，请检查手机是否安装打印服务", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
