package com.ucb.proyectofinal.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import com.ucb.proyectofinal.designsystem.components.BasicInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import org.junit.After
import org.koin.core.context.stopKoin

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = android.app.Application::class)
class BasicInputTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun basicInput_displaysLabelCorrectly() {
        composeTestRule.setContent {
            BasicInput(
                value = "",
                onValueChange = {},
                label = "Username"
            )
        }

        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
    }

    @Test
    fun basicInput_displaysValueCorrectly() {
        composeTestRule.setContent {
            BasicInput(
                value = "Test User",
                onValueChange = {},
                label = "Username"
            )
        }

        composeTestRule.onNodeWithText("Test User").assertIsDisplayed()
    }

    @Test
    fun basicInput_triggersOnValueChange() {
        var textResult = ""
        composeTestRule.setContent {
            BasicInput(
                value = textResult,
                onValueChange = { textResult = it },
                label = "Input"
            )
        }

        // Initially empty, we find the node by label or by its text field semantics
        // When typing text, we use performTextInput
        // We find the node by label text
        composeTestRule.onNodeWithText("Input").performTextInput("Hello")
        
        assertEquals("Hello", textResult)
    }

    @Test
    fun basicInput_displaysSupportingText_whenProvided() {
        composeTestRule.setContent {
            BasicInput(
                value = "",
                onValueChange = {},
                label = "Password",
                supportingText = "Must be 8 characters long"
            )
        }

        composeTestRule.onNodeWithText("Must be 8 characters long").assertIsDisplayed()
    }
}
