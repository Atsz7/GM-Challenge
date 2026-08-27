package com.atsz7.ram.hub.common.ui.components.bars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atsz7.ram.hub.common.ui.theme.RamHubTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RamTopBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsTheTitle() {

        // Given
        composeTestRule.setContent {
            RamHubTheme {
                RamTopBar(title = "Characters", subtitle = null)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Characters").assertIsDisplayed()
    }

    @Test
    fun hidesTheSubtitle_whenItIsNull() {

        // Given
        composeTestRule.setContent {
            RamHubTheme {
                RamTopBar(title = "Characters", subtitle = null)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Rick and Morty").assertDoesNotExist()
    }

    @Test
    fun showsTheSubtitle_whenProvided() {

        // Given
        composeTestRule.setContent {
            RamHubTheme {
                RamTopBar(title = "Characters", subtitle = "Rick and Morty")
            }
        }

        // Then
        composeTestRule.onNodeWithText("Rick and Morty").assertIsDisplayed()
    }

    @Test
    fun rendersTheNavigationIcon_whenProvided() {

        // Given
        composeTestRule.setContent {
            RamHubTheme {
                RamTopBar(
                    title = "Characters",
                    subtitle = null,
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                        }
                    }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun clickingTheNavigationIcon_invokesItsOnClick() {

        // Given
        var clicked = false
        composeTestRule.setContent {
            RamHubTheme {
                RamTopBar(
                    title = "Characters",
                    subtitle = null,
                    navigationIcon = {
                        IconButton(onClick = { clicked = true }) {
                            Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                        }
                    }
                )
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Then
        assertTrue(clicked)
    }

    @Test
    fun rendersTheActionIcon_whenProvided() {

        // Given
        composeTestRule.setContent {
            RamHubTheme {
                RamTopBar(
                    title = "Characters",
                    subtitle = null,
                    actionIcon = {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Search").assertIsDisplayed()
    }
}
