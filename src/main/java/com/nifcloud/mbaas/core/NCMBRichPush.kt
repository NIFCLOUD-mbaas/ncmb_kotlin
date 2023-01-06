/*
* Copyright 2017-2023 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.nifcloud.mbaas.core

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.*
import android.view.Window
import android.view.Gravity
import android.view.ViewGroup
import android.view.View
import android.view.WindowManager
import android.widget.*
import java.lang.StringBuilder
import java.lang.IllegalArgumentException

/**
 * NCMBRichPush provide dialog for rich push notification
 */
class NCMBRichPush(context: Context?, requestUrl: String) : Dialog(context!!, android.R.style.Theme_Translucent_NoTitleBar) {
    private var webBackView: LinearLayout? = null
    private var richPushHandlerContainer: FrameLayout? = null
    private var closeImage: ImageView? = null
    private var requestUrl = requestUrl
    private var progressDialog = setProgressDialog(context, "Loading...")

    @SuppressLint("RtlHardcoded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        richPushHandlerContainer = FrameLayout(context)

        createCloseImage()
        setUpWebView()
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.RIGHT or Gravity.TOP
        richPushHandlerContainer!!.addView(closeImage, layoutParams)
        addContentView(
            richPushHandlerContainer!!,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        webBackView = LinearLayout(context)
        val webViewContainer = LinearLayout(context)
        val webView = WebView(context)
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = RichPushWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.loadUrl(checkAndUpdateIfPdfUrl(requestUrl))
        webView.layoutParams = FILL

        webBackView!!.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        webBackView!!.setBackgroundColor(Color.DKGRAY)
        webBackView!!.setPadding(3, 3, 3, 3)
        webBackView!!.addView(webView)
        webBackView!!.visibility = View.INVISIBLE

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val disp = wm.defaultDisplay
        val size = Point()
        //API14以上
        disp.getSize(size)
        //API14以下
        //int dispWidth = disp.getWidth() / 60;
        val dispWidth = size.x / 60
        val closeImageWidth = closeImage!!.drawable.intrinsicWidth
        webViewContainer.setPadding(dispWidth, closeImageWidth / 2, dispWidth, dispWidth)
        webViewContainer.addView(webBackView)
        richPushHandlerContainer!!.addView(webViewContainer)
    }

    private fun createCloseImage() {
        closeImage = ImageView(context)
        closeImage!!.setOnClickListener { cancel() }
        val closeDrawable = context.resources.getDrawable(android.R.drawable.btn_dialog)
        closeImage!!.setImageDrawable(closeDrawable)
        closeImage!!.visibility = View.INVISIBLE
    }

    @SuppressLint("DefaultLocale")
    private fun checkAndUpdateIfPdfUrl(url: String): String {
        var url = url
        var extension = ""
        if (url.contains(".")) {
            extension = url.substring(url.lastIndexOf("."))
        }
        if (!url.contains(GOOGLE_DOCS_BASE_VIEWER_URL)
            && Arrays.asList(*arrOfficeFilenameExt).contains(extension.toLowerCase())
        ) {
            url = StringBuilder(GOOGLE_DOCS_BASE_VIEWER_URL).append(url).toString()
        }
        return url
    }

    private fun setProgressDialog(context:Context?, message:String): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }

    private inner class RichPushWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(checkAndUpdateIfPdfUrl(url))
            return true
        }

        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressDialog!!.show()
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            try {
                progressDialog!!.dismiss()
            } catch (localIllegalArgumentException: IllegalArgumentException) {
                Log.e("Error", localIllegalArgumentException.toString())
            }
            richPushHandlerContainer!!.setBackgroundColor(0)
            webBackView!!.visibility = View.VISIBLE
            closeImage!!.visibility = View.VISIBLE
        }
    }

    companion object {
        private val FILL: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        private const val GOOGLE_DOCS_BASE_VIEWER_URL =
            "https://docs.google.com/gview?embedded=true&url="
        val arrOfficeFilenameExt =
            arrayOf(".pdf", ".xls", ".xlsx", ".doc", ".docx", ".ppt", ".pptx")
    }
}