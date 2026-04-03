package com.example.stadmin.screens.trace.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadmin.screens.trace.domain.model.Trace
import com.example.stadmin.screens.trace.domain.usecase.CreateTraceUseCase
import com.example.stadmin.screens.trace.domain.usecase.DeleteTraceUseCase
import com.example.stadmin.screens.trace.domain.usecase.EditTraceUseCase
import com.example.stadmin.screens.trace.domain.usecase.GetTracesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TraceViewModel(
    private val getTracesUseCase: GetTracesUseCase,
    private val createTraceUseCase: CreateTraceUseCase,
    private val editTraceUseCase: EditTraceUseCase,
    private val deleteTraceUseCase: DeleteTraceUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(TraceViewState())
    val viewState: StateFlow<TraceViewState> = _viewState.asStateFlow()

    init {
        getTraceList()
    }

    private fun getTraceList() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null) }
            getTracesUseCase.invoke().collectLatest { result ->
                result.fold(
                    onSuccess = { traces ->
                        _viewState.update { it.copy(isLoading = false, traces = traces) }
                    },
                    onFailure = { error ->
                        _viewState.update { it.copy(isLoading = false, error = error.message) }
                    }
                )
            }
        }
    }

    fun deleteTrace(slug: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isDeleting = true, error = null) }
            deleteTraceUseCase(slug).collectLatest { result ->
                result.fold(
                    onSuccess = {
                        _viewState.update {
                            it.copy(
                                isDeleting = false,
                                deleteSuccess = true,
                            )
                        }
                    },
                    onFailure = { error ->
                        _viewState.update {
                            it.copy(isDeleting = false, error = error.message)
                        }
                    }
                )
            }
        }
    }

    fun saveTrace(trace: Trace) {
        viewModelScope.launch {
            _viewState.update { it.copy(isSaving = true, error = null) }
            val isNewTrace = _viewState.value.selectedTrace == null
            val flow = if (isNewTrace) {
                createTraceUseCase(trace)
            } else {
                editTraceUseCase(trace)
            }

            flow.collectLatest { result ->
                result.fold(
                    onSuccess = {
                        _viewState.update { it.copy(isSaving = false, saveSuccess = true) }
                        getTraceList()
                    },
                    onFailure = { error ->
                        _viewState.update { it.copy(isSaving = false, error = error.message) }
                    }
                )
            }
        }
    }

    fun onTraceSelected(trace: Trace) {
        _viewState.update { it.copy(selectedTrace = trace) }
    }

    fun onCreateNewTrace() {
        _viewState.update { it.copy(selectedTrace = null) }
    }

    fun onDeleteSuccessConsumed() {
        _viewState.update { it.copy(deleteSuccess = false) }
    }

    fun onSaveSuccessConsumed() {
        _viewState.update { it.copy(saveSuccess = false) }
    }

    fun onErrorConsumed() {
        _viewState.update { it.copy(error = null) }
    }

    data class TraceViewState(
        val traces: List<Trace> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isDeleting: Boolean = false,
        val selectedTrace: Trace? = null,
        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false,
        val deleteSuccess: Boolean = false,
    )
}