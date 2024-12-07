package com.example.garudasakti.midtrans

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.midtrans.sdk.uikit.external.UiKitApi

class MidtransConfig {
    private val MIDTRANS_MERCHAT_URL = "https://4ea3-223-255-231-132.ngrok-free.app" // sesuaikan
    private val MIDTRANS_CLIENT_KEY = "SB-Mid-client-oPkm6mdSZxomn5n8" // sesuaikan
    fun getMidtrans(applicationContext: Context){
        UiKitApi.Builder()
            .withMerchantClientKey(MIDTRANS_CLIENT_KEY)
            .withContext(applicationContext)
            .withMerchantUrl(MIDTRANS_MERCHAT_URL)
            .enableLog(true)
            .build()
        setLocaleNew("id")
    }
    fun setLocaleNew(languageCode: String?) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }
}