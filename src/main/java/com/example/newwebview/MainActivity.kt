package com.example.newwebview

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.browser_toolbar.*

class MainActivity : AppCompatActivity(), HistoryDialogFragment.WebHistory {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uriText.setOnEditorActionListener { textView, i, keyEvent ->
            if(i.equals(EditorInfo.IME_ACTION_SEND)) {
                loadWebPage()
                true
            } else false
        }

        backButton.setOnClickListener() {
            if(webView.canGoBack()) webView.goBack()
        }
        fowardButton.setOnClickListener() {
            if(webView.canGoForward()) webView.goForward()
        }
        backButton.setOnLongClickListener() {
            getHistoryDialog(getBackHistory())
            true
        }
        fowardButton.setOnLongClickListener() {
            getHistoryDialog(getForwardHistory())
            true
        }
    }

    fun getHistoryDialog(historyList: ArrayList<String>) {
        val historyDialogFragment = HistoryDialogFragment()
        val bundle = Bundle()
        bundle.putStringArrayList(historyDialogFragment.history, historyList)
        historyDialogFragment.arguments = bundle
        historyDialogFragment.show(supportFragmentManager, "HistoryDialog")

    }
    @Throws(UnsupportedOperationException::class)
    fun buildUri(authority: String) : Uri {
        val builder = Uri.Builder()
        builder.scheme("https")
            .authority(authority)
        return builder.build()

    }

    fun getBackHistory() : ArrayList<String> {

        val webBackHistory = webView.copyBackForwardList()
        val historyList = ArrayList<String>()

        for (i in 0 until webBackHistory.currentIndex)
            historyList.add(webBackHistory.getItemAtIndex(i).title)

        historyList.reverse()
        return historyList
    }

    fun getForwardHistory() : ArrayList<String> {
        val webForwardHistory = webView.copyBackForwardList()
        val historyList = ArrayList<String>()

        for (i in 0 until webForwardHistory.size - webForwardHistory.currentIndex - 1)
            historyList.add(webForwardHistory.getItemAtIndex(
                webForwardHistory.currentIndex + i + 1
            ).title)

        return historyList
    }

    fun loadWebPage() {


        //webview settings

        webView.settings.javaScriptEnabled=true
        webView.settings.javaScriptCanOpenWindowsAutomatically=true
        webView.settings.allowUniversalAccessFromFileURLs=true
        webView.settings.mediaPlaybackRequiresUserGesture=true
        webView.settings.setGeolocationEnabled(true)
        webView.settings.allowFileAccessFromFileURLs=true
        webView.settings.allowFileAccess=true
        webView.settings.loadsImagesAutomatically=true
        webView.settings.setAppCacheEnabled(true)
        webView.settings.useWideViewPort=true
        webView.settings.domStorageEnabled=true
        webView.settings.allowContentAccess=true

        pageLoadStatus()
        updateProgress()

        try{
            val uri = buildUri(uriText.text.toString())
            webView.loadUrl(uri.toString())
        } catch(e: UnsupportedOperationException) {
            e.printStackTrace()
        }


    }

    fun updateProgress() {
        webView.webChromeClient = object: WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                pageLoadProgressBar.progress = newProgress
            }
        }
    }

    fun pageLoadStatus() {
        webView.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                pageLoadProgressBar.visibility = View.VISIBLE
                pageLoadProgressBar.progress = 0
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                pageLoadProgressBar.visibility = View.GONE
            }
        }
    }

    override fun webpageSelected(webTitle: String) {
        val webHistory = webView.copyBackForwardList()

        for (i in 0 until webHistory.size) {
            if (webHistory.getItemAtIndex(i).title.equals(webTitle)) {
                webView.goBackOrForward(i - webHistory.currentIndex)
                break
            }
        }
    }
}
