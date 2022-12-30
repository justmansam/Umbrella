package com.example.umbrella.ui.main.model

data class MainUiState(
    val isSearchActive: Boolean = false,
    val isSearchFailed: Int = 0, // 0:No, 1:Yes(Typo!), 2:Yes(No Connection!), 3:Yes(Unexpected!)
    val hasSharedPref: Boolean = false,
    val isInProcess: Boolean = false,
    val hasConnection: Boolean = true
)