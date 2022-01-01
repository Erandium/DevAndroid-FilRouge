package com.jdock.fil_rouge

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.jdock.fil_rouge.network.Api
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val userInfoTextView = findViewById<TextView>(R.id.main_text_view)

        lifecycleScope.launch {
            val userInfo = Api.userWebService.getInfo().body()!!
            userInfoTextView.text = "${userInfo.firstName} ${userInfo.lastName}"
        }

    }
}