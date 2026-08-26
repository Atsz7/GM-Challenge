package com.atsz7.ram.hub.ui.main.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atsz7.ram.hub.domain.usecases.ObserveDarkModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    observeDarkModeUseCase: ObserveDarkModeUseCase
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = observeDarkModeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = false
        )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
