package com.atsz7.ram.hub.common.ui.components.rows

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.atsz7.ram.hub.common.R
import com.atsz7.ram.hub.common.ui.models.RamBasicBadge
import com.atsz7.ram.hub.common.ui.theme.RamHubTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RamBasicRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun setContent(
        isFavorite: Boolean = false,
        badge: RamBasicBadge = RamBasicBadge.ALIVE,
        onFavoriteToggle: () -> Unit = {},
        onClick: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            RamHubTheme {
                RamBasicRow(
                    title = "Rick Sanchez",
                    subtitle = "Human",
                    imageUrl = "",
                    badge = badge,
                    shape = RoundedCornerShape(8.dp),
                    isFavorite = isFavorite,
                    onFavoriteToggle = onFavoriteToggle,
                    onClick = onClick
                )
            }
        }
    }

    @Test
    fun showsTheTitleAndSubtitle() {

        // Given
        setContent()

        // Then
        composeTestRule.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Human").assertIsDisplayed()
    }

    @Test
    fun showsTheBadgeLabel() {

        // Given
        setContent(badge = RamBasicBadge.DEAD)

        // Then
        composeTestRule.onNodeWithText(context.getString(R.string.badge_dead_label)).assertIsDisplayed()
    }

    @Test
    fun showsTheRemoveFavoriteAction_whenIsFavoriteIsTrue() {

        // Given
        setContent(isFavorite = true)

        // Then
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.remove_favorite_cd))
            .assertIsDisplayed()
    }

    @Test
    fun showsTheAddFavoriteAction_whenIsFavoriteIsFalse() {

        // Given
        setContent(isFavorite = false)

        // Then
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.add_favorite_cd))
            .assertIsDisplayed()
    }

    @Test
    fun clickingTheFavoriteAction_invokesOnFavoriteToggle() {

        // Given
        var toggled = false
        setContent(isFavorite = false, onFavoriteToggle = { toggled = true })

        // When
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.add_favorite_cd))
            .performClick()

        // Then
        assertTrue(toggled)
    }

    @Test
    fun clickingTheRow_invokesOnClick() {

        // Given
        var clicked = false
        setContent(onClick = { clicked = true })

        // When
        composeTestRule.onNodeWithText("Rick Sanchez").performClick()

        // Then
        assertTrue(clicked)
    }
}
