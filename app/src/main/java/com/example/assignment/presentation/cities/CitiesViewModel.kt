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

    private var currentQuery = ""
    private var offset = 0
    private val limit = 10
    private var canLoadMore = true

    private var lastSearchTime = 0L

    init {
        loadAllCities()
    }

    private fun loadAllCities(loadMore: Boolean = false) {
        searchCities("", loadMore)
    }

    fun searchCities(query: String, loadMore: Boolean = false) {
        if (_isLoading.value) return

        if (!loadMore && query.isBlank()) {
            resetPagination()
        }

        if (!loadMore && query != currentQuery) {
            offset = 0
            canLoadMore = true
            _cities.value = emptyList()
        }

        if (!canLoadMore) return

        val now = System.currentTimeMillis()
        if (!loadMore && now - lastSearchTime < 1000) return // انتظار ثانية بين كل بحث
        lastSearchTime = now

        currentQuery = query
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getCities(
                    namePrefix = query.ifBlank { null },
                    limit = limit,
                    offset = offset
                )

                val newCities = response.data.map { it.toSimpleCity() }

                if (newCities.isEmpty() || newCities.size < limit) {
                    canLoadMore = false
                } else {
                    offset += limit
                }

                _cities.value = _cities.value + newCities
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMore() {
        if (!_isLoading.value && canLoadMore) {
            searchCities(currentQuery, loadMore = true)
        }
    }


    fun resetPagination() {
        offset = 0
        canLoadMore = true
        _cities.value = emptyList()
    }
}
