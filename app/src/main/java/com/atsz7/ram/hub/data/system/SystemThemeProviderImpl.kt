package com.atsz7.ram.hub.data.system

import android.content.Context
import android.content.res.Configuration
import com.atsz7.ram.hub.domain.system.SystemThemeProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SystemThemeProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SystemThemeProvider {

    override fun isSystemInDarkMode(): Boolean {
        val nightModeFlags =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}
