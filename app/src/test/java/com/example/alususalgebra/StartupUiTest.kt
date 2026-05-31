package com.example.alususalgebra

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import android.app.Application
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartupUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testRendering() {
        try {
            val app = ApplicationProvider.getApplicationContext<Application>()
            val viewModel = AlUsusViewModel(app)
            
            composeTestRule.setContent {
                AlUsusScreen(viewModel = viewModel)
            }
            
            println("Screen rendered perfectly!")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
