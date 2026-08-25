package com.waveanime.ui.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waveanime.data.api.ApiClient
import com.waveanime.data.model.SearchAnimeItem
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel : ViewModel() {
    val query = MutableStateFlow("")

    private val _results = MutableStateFlow<List<SearchAnimeItem>>(emptyList())
    val results = _results.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            query
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { text ->
                    if (text.isBlank()) {
                        _results.value = emptyList()
                        _isLoading.value = false
                    } else {
                        _isLoading.value = true
                        try {
                            val items = ApiClient.searchSeries(text.trim())
                            _results.value = items
                        } catch (e: Exception) {
                            Log.e("SearchViewModel", "Erreur recherche: ${e.message}", e)
                            _results.value = emptyList()
                        } finally {
                            _isLoading.value = false
                        }
                    }
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }
}