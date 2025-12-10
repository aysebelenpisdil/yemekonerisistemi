package com.example.app.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.UserRepository
import com.example.app.model.UserPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ProfileNavigationEvent>()
    val navigationEvent: SharedFlow<ProfileNavigationEvent> = _navigationEvent.asSharedFlow()

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: Fetch user profile from repository
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // TODO: Clear user session/tokens from repository
            _navigationEvent.emit(ProfileNavigationEvent.NavigateToLogin)
        }
    }

    fun navigateToSettings() {
        viewModelScope.launch {
            _navigationEvent.emit(ProfileNavigationEvent.NavigateToSettings)
        }
    }

    fun navigateToEditProfile() {
        viewModelScope.launch {
            _navigationEvent.emit(ProfileNavigationEvent.NavigateToEditProfile)
        }
    }

    fun updateUserPreferences(preferences: UserPreferences) {
        viewModelScope.launch {
            // TODO: Save preferences via repository
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            // TODO: Call repository to delete account
            _navigationEvent.emit(ProfileNavigationEvent.NavigateToLogin)
        }
    }

    fun refreshProfile() {
        loadUserProfile()
    }

    fun clearCache() {
        viewModelScope.launch {
            // TODO: Clear local cache via repository
        }
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val errorMessage: String? = null
)

sealed class ProfileNavigationEvent {
    object NavigateToLogin : ProfileNavigationEvent()
    object NavigateToSettings : ProfileNavigationEvent()
    object NavigateToEditProfile : ProfileNavigationEvent()
}