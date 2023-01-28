package com.cc.resellconnect.utils

import android.os.AsyncTask
import com.github.barteksc.pdfviewer.PDFView
import com.kaopiz.kprogresshud.KProgressHUD
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class RetrivePDFfromUrl() : AsyncTask<String?, Void?, InputStream?>() {
    var inputStream: InputStream? = null
    var pdfView: PDFView? = null
    var url:String? = null
    var hud:KProgressHUD? = null

    constructor(
        inputStream: InputStream?,pdfView: PDFView,url:String, hud: KProgressHUD
    ):this(){
        this.inputStream = inputStream
        this.pdfView = pdfView
        this.url = url
        this.hud = hud
    }

    override fun onPreExecute() {
        super.onPreExecute()
        hud?.show()
    }

    override fun doInBackground(vararg strings: String?): InputStream? {
        // we are using inputstream
        // for getting out PDF.

        try {
            val url = URL(this.url)
            // below is the step where we are
            // creating our connection.
            val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
            if (urlConnection.getResponseCode() === 200) {
                // response is success.
                // we are getting input stream from url
                // and storing it in our variable.
                inputStream = BufferedInputStream(urlConnection.getInputStream())
            }
        } catch (e: IOException) {
            // this is the method
            // to handle errors.
            e.printStackTrace()
            return null
        }
        return inputStream
    }

    override fun onPostExecute(inputStream: InputStream?) {
        // after the execution of our async
        // task we are loading our pdf in our pdf view.

        hud?.dismiss()
        pdfView!!.fromStream(inputStream).load()
    }
}