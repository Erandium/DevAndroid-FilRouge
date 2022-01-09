package com.jdock.fil_rouge.network

import android.app.Application
import androidx.preference.PreferenceManager
import com.jdock.fil_rouge.authentication.SHARED_PREF_TOKEN_KEY

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Api.setUpContext(this)

    }

    fun getToken() = PreferenceManager.getDefaultSharedPreferences(Api.appContext).getString(SHARED_PREF_TOKEN_KEY, "")

}