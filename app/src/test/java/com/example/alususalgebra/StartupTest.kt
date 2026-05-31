package com.example.alususalgebra

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartupTest {
    @Test
    fun testStartup() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AlUsusViewModel(app)
        println("ViewModel initialized successfully")
    }
}
