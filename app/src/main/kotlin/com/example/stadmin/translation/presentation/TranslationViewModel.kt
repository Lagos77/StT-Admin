package com.example.stadmin.translation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadmin.core.deepl.DeepDataSource
import com.example.stadmin.screens.trace.domain.model.Passage
import com.example.stadmin.screens.trace.domain.model.Trace
import com.example.stadmin.translation.domain.model.TraceTranslation
import com.example.stadmin.translation.domain.usecase.CreateTranslationUseCase
import com.example.stadmin.translation.domain.usecase.DeleteTranslationUseCase
import com.example.stadmin.translation.domain.usecase.EditTranslationUseCase
import com.example.stadmin.translation.domain.usecase.GetTranslationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranslationViewModel @Inject constructor(
    private val getTranslationsUseCase: GetTranslationsUseCase,
    private val createTranslationUseCase: CreateTranslationUseCase,
    private val editTranslationUseCase: EditTranslationUseCase,
    private val deleteTranslationUseCase: DeleteTranslationUseCase,
    private val deepDataSource: DeepDataSource,
) : ViewModel() {

    private val _viewState = MutableStateFlow(TranslationViewState())
    val viewState: StateFlow<TranslationViewState> = _viewState.asStateFlow()

    fun initiate(trace: Trace) {
        _viewState.update {
            it.copy(
                baseTrace = trace,
                language = TranslationLanguage.ES,
                selectedTranslation = null,
                titleDraft = "",
                descriptionDraft = "",
                contentDraft = emptyList(),
                passagesDraft = emptyList(),
                translations = emptyList(),
            )
        }
        getTranslation(trace.slug)
    }

    private fun getTranslation(traceSlug: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true) }
            getTranslationsUseCase(traceSlug).collectLatest { result ->
                result.fold(
                    onSuccess = { translations ->
                        val existingEs = translations.find { it.language == TranslationLanguage.ES }
                        _viewState.update {
                            it.copy(
                                isLoading = false,
                                translations = translations,
                                selectedTranslation = existingEs,
                                titleDraft = existingEs?.title ?: "",
                                descriptionDraft = existingEs?.description ?: "",
                                contentDraft = existingEs?.content ?: it.contentDraft,
                                passagesDraft = existingEs?.passages ?: it.passagesDraft,
                            )
                        }
                    },
                    onFailure = { error ->
                        _viewState.update {
                            it.copy(isLoading = false, snackBarMessage = error.message)
                        }
                    }
                )
            }
        }
    }

    fun onLanguageChange(language: TranslationLanguage) {
        _viewState.update { it.copy(language = language) }
        val existing = _viewState.value.translations.find { it.language == language }
        if (existing != null) {
            _viewState.update {
                it.copy(
                    selectedTranslation = existing,
                    titleDraft = existing.title ?: "",
                    descriptionDraft = existing.description ?: "",
                    contentDraft = existing.content ?: emptyList(),
                    passagesDraft = existing.passages ?: emptyList(),
                )
            }
        } else {
            _viewState.update {
                it.copy(
                    selectedTranslation = null,
                    titleDraft = "",
                    descriptionDraft = "",
                    contentDraft = emptyList(),
                    passagesDraft = emptyList(),
                )
            }
        }
    }

    fun translateRemainingContent(fromIndex: Int, targetLanguage: String, sourceContent: List<String>) {
        viewModelScope.launch {
            _viewState.update { it.copy(isTranslating = true) }
            sourceContent.drop(fromIndex).forEachIndexed { index, text ->
                val index = fromIndex + index
                val alreadyDone = _viewState.value.contentDraft.getOrElse(index) { "" }.isNotBlank()
                if (!alreadyDone) {
                    deepDataSource.translate(
                        text = text,
                        targetLanguage = targetLanguage,
                        sourceLanguage = "EN"
                    )
                }
            }
            _viewState.update { it.copy(isTranslating = false) }
        }
    }

    fun saveTranslation(traceSlug: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isSaving = true, snackBarMessage = null) }
            val isNew = _viewState.value.selectedTranslation == null

            val translation = TraceTranslation(
                id = _viewState.value.selectedTranslation?.id,
                traceSlug = traceSlug,
                language = _viewState.value.language,
                title = _viewState.value.titleDraft.ifBlank { null },
                description = _viewState.value.descriptionDraft.ifBlank { null },
                content = _viewState.value.contentDraft.filter { it.isNotBlank() }.ifEmpty { null },
                passages = _viewState.value.passagesDraft.ifEmpty { null },
                videos = null,
            )

            val flow = if (isNew) createTranslationUseCase(translation)
            else editTranslationUseCase(translation)

            flow.collectLatest { result ->
                result.fold(
                    onSuccess = {
                        _viewState.update {
                            it.copy(
                                isSaving = false,
                                saveSuccess = true,
                                snackBarMessage = if (isNew) "Translation saved!" else "Translation updated!"
                            )
                        }
                    },
                    onFailure = { error ->
                        _viewState.update {
                            it.copy(
                                isSaving = false,
                                snackBarMessage = error.message
                            )
                        }
                    }
                )
            }
        }
    }

    fun deleteTranslation(id: String, traceSlug: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, snackBarMessage = null) }
            deleteTranslationUseCase(id).collectLatest { result ->
                result.fold(
                    onSuccess = {
                        _viewState.update { it.copy(isLoading = false, deleteSuccess = true) }
                        getTranslation(traceSlug = traceSlug)
                    },
                    onFailure = { error ->
                        _viewState.update {
                            it.copy(isLoading = false, snackBarMessage = error.message)
                        }
                    }
                )
            }
        }
    }


    fun onTitleChanged(value: String) =
        _viewState.update { it.copy(titleDraft = value) }

    fun onDescriptionChanged(value: String) =
        _viewState.update { it.copy(descriptionDraft = value) }

    fun onContentChanged(index: Int, value: String) {
        val updated = _viewState.value.contentDraft.toMutableList().also {
            while (it.size <= index) it.add("")
            it[index] = value
        }
        _viewState.update { it.copy(contentDraft = updated) }
    }

    fun onPassageChanged(index: Int, passage: Passage) {
        val updated = _viewState.value.passagesDraft.toMutableList().also {
            while (it.size <= index) it.add(Passage("", null, null, null, "", ""))
            it[index] = passage
        }
        _viewState.update { it.copy(passagesDraft = updated) }
    }

    fun onSaveSuccessConsumed() = _viewState.update { it.copy(saveSuccess = false) }
    fun onDeleteSuccessConsumed() = _viewState.update { it.copy(deleteSuccess = false) }
    fun onSnackBarMessageConsumed() = _viewState.update { it.copy(snackBarMessage = null) }

    fun translate(
        text: String,
        targetLanguage: String,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            _viewState.update { it.copy(isTranslating = true) }
            deepDataSource.translate(
                text = text, targetLanguage = targetLanguage, sourceLanguage = "EN"
            )?.let { onResult(it) }
            _viewState.update { it.copy(isTranslating = false) }
        }
    }

    data class TranslationViewState(
        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val isTranslating: Boolean = false,
        val saveSuccess: Boolean = false,
        val deleteSuccess: Boolean = false,
        val snackBarMessage: String? = null,
        val baseTrace: Trace? = null,
        val translations: List<TraceTranslation> = emptyList(),
        val selectedTranslation: TraceTranslation? = null,
        val language: TranslationLanguage = TranslationLanguage.ES,
        val titleDraft: String = "",
        val descriptionDraft: String = "",
        val contentDraft: List<String> = emptyList(),
        val passagesDraft: List<Passage> = emptyList()
    )
}