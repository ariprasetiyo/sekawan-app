package com.arprast.sekawan.ui.media.account.facebook

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.arprast.sekawan.util.PreferanceVariable
import com.arprast.sekawan.util.ShowTextUtil
import com.arprastandroid.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FacebookMainFragment(username: String, password: String) : Fragment() {

    private val username = username
    private val password = password

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_media_youtube, container, false)

        val fab: FloatingActionButton = root.findViewById(R.id.float_show_password)
        fab.setOnClickListener { view ->
            ShowTextUtil.showTextUtil(
                "Credential account !",
                "Username: $username\nPassword: $password",
                context
            )

        }

        val webViewFacebook = root.findViewById(R.id.webview) as WebView
        webViewFacebook.loadUrl("https://m.facebook.com/login/?next&ref=dbl&fl&refid=8")
        val webViewSetting = webViewFacebook.settings
        webViewSetting.loadsImagesAutomatically = true
        webViewSetting.javaScriptEnabled = true
        webViewSetting.domStorageEnabled = true
        webViewSetting.cacheMode = WebSettings.LOAD_DEFAULT
        webViewFacebook.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webViewFacebook.setWebChromeClient(object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d(
                    "debug-arprast", consoleMessage.message() + " -- From line "
                            + consoleMessage.lineNumber() + " of "
                            + consoleMessage.sourceId()
                )
                return super.onConsoleMessage(consoleMessage)
            }
        })
        webViewFacebook.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Log.d(PreferanceVariable.DEBUG_NAME, url)
                view.loadUrl(
                    "javascript:var yy =document.getElementById('m_login_email').value = '$username';" +
                            "var xx = document.getElementById('m_login_password').value='$password';"
                )
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                if (url.startsWith("https://m.facebook.com/home.php") ||
                    url.startsWith("https://m.facebook.com/?ref=dbl&_rdr")
                ) {
                    fab.visibility = View.GONE
                }
            }
        })

        return root
    }
}