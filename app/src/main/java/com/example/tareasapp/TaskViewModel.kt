package com.example.tareasapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOrder {
    NEWEST_FIRST,
    OLDEST_FIRST,
    TITLE_A_Z,
    TITLE_Z_A
}

class TaskViewModel(
    private val dao: TaskDao
) : ViewModel() {

    private val _searchInput = MutableStateFlow("")
    val searchInput: StateFlow<String> = _searchInput.asStateFlow()

    private val _activeQuery = MutableStateFlow("")

    /** Orden actualmente seleccionado por el usuario. */
    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST_FIRST)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<TaskEntity>> =
        combine(_activeQuery, _sortOrder) { query, order -> query to order }
            .flatMapLatest { (query, order) ->
                when (order) {
                    SortOrder.NEWEST_FIRST -> dao.searchTasks(query)
                    SortOrder.OLDEST_FIRST -> dao.searchTasksOldestFirst(query)
                    SortOrder.TITLE_A_Z    -> dao.searchTasksTitleAZ(query)
                    SortOrder.TITLE_Z_A    -> dao.searchTasksTitleZA(query)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            dao.insert(TaskEntity(title = title.trim()))
        }
    }

    fun toggleCompleted(task: TaskEntity) {
        viewModelScope.launch {
            dao.update(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            dao.delete(task)
        }
    }

    fun onSearchInputChanged(text: String) {
        _searchInput.value = text
    }

    fun executeSearch() {
        _activeQuery.value = _searchInput.value.trim()
    }

    /** Cambia el criterio de ordenación y reactualiza la lista de inmediato. */
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                val dao = AppDataBase
                    .getInstance(application)
                    .taskDao()
                TaskViewModel(dao)
            }
        }
    }
}