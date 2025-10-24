package com.example.assignment.presentation.cities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.models.SimpleCity
import com.example.assignment.data.models.toSimpleCity
import com.example.assignment.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CitiesViewModel : ViewModel() {

    private val _cities = MutableStateFlow<List<SimpleCity>>(emptyList())
    val cities: StateFlow<List<SimpleCity>> = _cities

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun searchCities(query: String) {
        if (query.isBlank()) {
            _cities.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.api.getCities(namePrefix = query)

                val simpleCities = response.data.map { it.toSimpleCity() }

                _cities.value = simpleCities
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
