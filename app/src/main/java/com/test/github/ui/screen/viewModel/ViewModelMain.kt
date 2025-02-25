package com.test.github.ui.screen.viewModel

import android.content.Context
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.github.database.History
import com.test.github.helper.loadJsonFromAssets
import com.test.github.network.GitHubService
import com.test.github.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject


@HiltViewModel
class ViewModelMain  @Inject constructor(
    @ApplicationContext private val context: Context,
    private val history: History,
    private val service: GitHubService
) : ViewModel() {

    val searchTextFieldState = TextFieldState()

    sealed interface SearchState {
        data object Empty: SearchState
        data class UseQuery(val query: String): SearchState
    }

    sealed interface ScreenState{
        data class Empty(
            val userQuery: String,
            val result: List<Repository>
        ): ScreenState
        data object Searching: ScreenState
        data class Error(val message: String): ScreenState
        data class Content(
            val userQuery: String,
            val result: List<Repository>
        ): ScreenState
        data class Selected(
            val userQuery: String,
            val result: Map<String, List<String>>
        ): ScreenState
    }

    private val isFocused = mutableStateOf(false)




    private val empty = emptyList<Repository>()
    private val _searchList = MutableStateFlow(empty)
    val searchList: StateFlow<List<Repository>> = _searchList.asStateFlow()
    private fun updateSearchList(list: List<Repository>) = _searchList.update {
        list
    }

    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Empty("",empty))
    val uiState = _uiState.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val searchTextState: StateFlow<SearchState> = snapshotFlow { searchTextFieldState.text to isFocused.value }
        .debounce(500)
        .mapLatest {(text, focused) -> if (focused) SearchState.UseQuery(text.toString()) else
            (/*if (text.isBlank()) */SearchState.Empty /*else SearchState.UseQuery(text.toString())*/)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 2000),
            initialValue = SearchState.Empty
        )

    fun observeUserSearch() = viewModelScope.launch {
        searchTextState.collectLatest {
            when (it) {
                is SearchState.Empty -> _uiState.update { ScreenState.Empty("",_searchList.value) }
                is SearchState.UseQuery -> queryAnalysis(it.query)
            }
        }
    }

    private fun queryAnalysis(query: String) = viewModelScope.launch {
        val spaceList = query.split(" ")
        val historyList = history.getAll()
        var list = mutableListOf<String>()
        if(spaceList.last().contains(":")){
            val key = spaceList.last().split(":")[0].lowercase()
            val value = spaceList.last().split(":")[1].lowercase()
            val jsonObject  = loadJsonFromAssets(context, "qualifier.json")?.let { JSONObject(it) }

            val jsonArray = jsonObject?.optJSONArray(key)

            if(jsonArray != null){
                for (i in 0 until jsonArray.length()) {
                    list.add(jsonArray.getString(i))
                }
                if(value.isNotEmpty()){
                    list = list.filter { it.lowercase().startsWith(value) && it.lowercase() != value }.toMutableList()
                }
            }

        }
        _uiState.update { ScreenState.Selected(query, mapOf("history" to historyList, "qualifier" to list)) }
    }

    fun onFocusChanged(focused: Boolean) {
        isFocused.value = focused
    }

    fun startSearch(search: String, page: Int){

            _uiState.update { ScreenState.Searching }
            history.add(search)
            viewModelScope.launch {
                try {
                    val list = service.gitHubRepository.getSearch(search, page).items
                    updateSearchList(list)
                    _uiState.update { ScreenState.Content(search, list) }
                }catch (e: Exception){
                    _uiState.update { ScreenState.Error(e.message.toString()) }
                }
            }


    }


}