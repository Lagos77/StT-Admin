package com.example.stadmin.screens.trace.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadmin.screens.trace.domain.TraceEra
import com.example.stadmin.screens.trace.domain.model.Passage
import com.example.stadmin.screens.trace.domain.model.Source
import com.example.stadmin.screens.trace.domain.model.Trace
import com.example.stadmin.screens.trace.domain.model.Video
import com.example.stadmin.screens.trace.domain.usecase.CreateTraceUseCase
import com.example.stadmin.screens.trace.domain.usecase.DeleteImageUseCase
import com.example.stadmin.screens.trace.domain.usecase.DeleteTraceUseCase
import com.example.stadmin.screens.trace.domain.usecase.EditTraceUseCase
import com.example.stadmin.screens.trace.domain.usecase.GetTracesUseCase
import com.example.stadmin.screens.trace.domain.usecase.UploadImageUseCase
import com.example.stadmin.translation.domain.model.TraceTranslation
import com.example.stadmin.translation.domain.usecase.CreateTranslationUseCase
import com.example.stadmin.translation.domain.usecase.DeleteTranslationUseCase
import com.example.stadmin.translation.domain.usecase.EditTranslationUseCase
import com.example.stadmin.translation.domain.usecase.GetTranslationsUseCase
import com.example.stadmin.translation.presentation.TranslationLanguage
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
    private val deleteTraceUseCase: DeleteTraceUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val deleteImageUseCase: DeleteImageUseCase,
    private val getTranslationsUseCase: GetTranslationsUseCase,
    private val createTranslationUseCase: CreateTranslationUseCase,
    private val editTranslationUseCase: EditTranslationUseCase,
    private val deleteTranslationUseCase: DeleteTranslationUseCase,
) : ViewModel() {

    private val _viewState = MutableStateFlow(TraceViewState())
    val viewState: StateFlow<TraceViewState> = _viewState.asStateFlow()

    init {
        getTraceList()
    }

    fun getTraceList() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, snackBarMessage = null) }
            getTracesUseCase.invoke().collectLatest { result ->
                result.fold(
                    onSuccess = { traces ->
                        _viewState.update {
                            it.copy(
                                isLoading = false,
                                traces = traces.sortedByDescending { trace -> trace.updatedAt })
                        }
                    },
                    onFailure = { error ->
                        _viewState.update {
                            it.copy(
                                isLoading = false,
                                snackBarMessage = error.message
                            )
                        }
                    }
                )
            }
        }
    }

    fun deleteTrace(slug: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isDeleting = true, snackBarMessage = null) }
            deleteTraceUseCase(slug).collectLatest { result ->
                result.fold(
                    onSuccess = {
                        _viewState.update {
                            it.copy(
                                isDeleting = false,
                                deleteSuccess = true,
                            )
                        }
                        getTraceList()
                    },
                    onFailure = { error ->
                        _viewState.update {
                            it.copy(isDeleting = false, snackBarMessage = error.message)
                        }
                    }
                )
            }
        }
    }

    fun saveTrace(trace: Trace) {
        viewModelScope.launch {
            _viewState.update { it.copy(isSaving = true, snackBarMessage = null) }
            val isNewTrace = _viewState.value.selectedTrace == null
            val flow = if (isNewTrace) {
                createTraceUseCase(trace)
            } else {
                editTraceUseCase(trace)
            }

            flow.collectLatest { result ->
                result.fold(
                    onSuccess = {
                        _viewState.update {
                            it.copy(
                                isFirstTimeCreated = isNewTrace,
                                isSaving = false,
                                saveSuccess = true,
                                pendingImageUrls = emptyList(),
                                snackBarMessage = if (isNewTrace) "New trace saved!" else "Edit saved!"
                            )
                        }
                    },
                    onFailure = { error ->
                        _viewState.update {
                            it.copy(
                                isSaving = false,
                                translationSaveSuccess = false,
                                snackBarMessage = error.message
                            )
                        }
                    }
                )
            }
        }
    }

    fun onTitleChanged(value: String) {
        _viewState.update {
            it.copy(
                title = value,
                slug = generateSlug(title = value)
            )
        }
    }

    private fun generateSlug(title: String): String {
        return title
            .lowercase()
            .replace("å", "a")
            .replace("ä", "a")
            .replace("ö", "o")
            .replace("é", "e")
            .replace("è", "e")
            .replace("ê", "e")
            .replace("ë", "e")
            .replace("à", "a")
            .replace("â", "a")
            .replace("ü", "u")
            .replace("ú", "u")
            .replace("ù", "u")
            .replace("û", "u")
            .replace("ï", "i")
            .replace("î", "i")
            .replace("í", "i")
            .replace("ì", "i")
            .replace("ó", "o")
            .replace("ô", "o")
            .replace("ò", "o")
            .replace("ñ", "n")
            .replace("ç", "c")
            .replace("'", "")
            .replace("[^a-z0-9\\s-]".toRegex(), "")
            .trim()
            .replace("\\s+".toRegex(), "-")
            .replace("-+".toRegex(), "-")
    }

    fun onImageSelected(context: Context, imageType: ImageType, uri: Uri) {
        viewModelScope.launch {
            _viewState.update { it.copy(isUploadingImage = true) }

            val oldUrl = when (imageType) {
                ImageType.CARD -> _viewState.value.imageUrl
                ImageType.HERO -> _viewState.value.heroImageUrl
            }

            if (oldUrl.isNotBlank()) {
                deleteImageUseCase(oldUrl).collectLatest { result ->
                    result.fold(
                        onSuccess = {},
                        onFailure = { error -> _viewState.update { it.copy(snackBarMessage = error.message) } }
                    )
                }
            }
            uploadImageUseCase(context, uri, imageType).collectLatest { result ->
                result.fold(
                    onSuccess = { url ->
                        _viewState.update {
                            when (imageType) {
                                ImageType.CARD -> it.copy(
                                    imageUrl = url,
                                    isUploadingImage = false,
                                    pendingImageUrls = it.pendingImageUrls + url
                                )

                                ImageType.HERO -> it.copy(
                                    heroImageUrl = url,
                                    isUploadingImage = false,
                                    pendingImageUrls = it.pendingImageUrls + url
                                )
                            }
                        }
                    },
                    onFailure = { error ->
                        _viewState.update {
                            it.copy(
                                isUploadingImage = false,
                                snackBarMessage = error.message
                            )
                        }
                    }
                )
            }
        }
    }

    fun onImageDeleted(imageType: ImageType) {
        viewModelScope.launch {
            val url = when (imageType) {
                ImageType.CARD -> _viewState.value.imageUrl
                ImageType.HERO -> _viewState.value.heroImageUrl
            }
            if (url.isBlank()) {
                _viewState.update { it.copy(snackBarMessage = "Image url is blank") }
                return@launch
            }
            deleteImageUseCase(url).collectLatest { result ->
                result.fold(
                    onSuccess = {
                        _viewState.update {
                            when (imageType) {
                                ImageType.CARD -> it.copy(imageUrl = "")
                                ImageType.HERO -> it.copy(heroImageUrl = "")
                            }
                        }
                    },
                    onFailure = { error -> _viewState.update { it.copy(snackBarMessage = error.message) } }
                )
            }
        }
    }

    fun onDiscardChanges(onBack: () -> Unit) {
        viewModelScope.launch {
            val pendingUrls = _viewState.value.pendingImageUrls
            pendingUrls.forEach { url ->
                deleteTraceUseCase(url).collectLatest { result ->
                    result.fold(
                        onSuccess = {
                        },
                        onFailure = { error -> _viewState.update { it.copy(snackBarMessage = error.message) } }
                    )
                }
            }
            _viewState.update { it.copy(pendingImageUrls = emptyList()) }
            onBack()
        }
    }

    fun onSlugChanged(value: String) = _viewState.update { it.copy(slug = value) }
    fun onDescriptionChanged(value: String) = _viewState.update { it.copy(description = value) }
    fun onYearChanged(value: String) = _viewState.update { it.copy(year = value) }
    fun onEraChanged(value: TraceEra) = _viewState.update { it.copy(era = value) }
    fun onLatitudeChanged(value: String) = _viewState.update { it.copy(latitude = value) }
    fun onLongitudeChanged(value: String) = _viewState.update { it.copy(longitude = value) }
    fun onPublishedChanged(value: Boolean) = _viewState.update { it.copy(published = value) }

    fun onTraceSelected(trace: Trace) {
        viewModelScope.launch {
            _viewState.update {
                it.copy(
                    selectedTrace = trace,
                    title = trace.title,
                    slug = trace.slug,
                    description = trace.description ?: "",
                    year = trace.year?.toString() ?: "",
                    era = trace.era,
                    imageUrl = trace.imageUrl ?: "",
                    heroImageUrl = trace.heroImageUrl ?: "",
                    latitude = trace.latitude?.toString() ?: "",
                    longitude = trace.longitude?.toString() ?: "",
                    published = trace.published,
                    content = trace.content ?: emptyList(),
                    passages = trace.passages ?: emptyList(),
                    videos = trace.videos ?: emptyList(),
                    sources = trace.sources ?: emptyList(),
                )
            }
            getTranslation(slug = trace.slug)
        }
    }

    private suspend fun getTranslation(slug: String) {
        getTranslationsUseCase.invoke(slug).collectLatest { result ->
            result.fold(
                onSuccess = { translations ->
                    _viewState.update { it.copy(translations = translations) }
                },
                onFailure = { error ->
                    _viewState.update { it.copy(snackBarMessage = error.message) }
                }
            )
        }
    }

    fun saveTranslation(traceSlug: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, snackBarMessage = null) }
            val state = _viewState.value
            val isNew = state.selectedTranslation == null

            val translation = TraceTranslation(
                id = state.selectedTranslation?.id,
                traceSlug = traceSlug,
                language = state.language,
                title = state.titleTranslated.ifBlank { null },
                description = state.descriptionTranslated.ifBlank { null },
                content = state.contentTranslated.filter { it.isNotBlank() }.ifEmpty { null },
                passages = state.passagesTranslated.ifEmpty { null },
                videos = state.videosTranslated.filter { it.isNotBlank() }.ifEmpty { null },
            )

            val flow = if (isNew) {
                createTranslationUseCase(translation)
            } else {
                editTranslationUseCase(translation)
            }

            flow.collectLatest { result ->
                result.fold(
                    onSuccess = {
                        _viewState.update {
                            it.copy(
                                isLoading = false,
                                translationSaveSuccess = true,
                                snackBarMessage = if (isNew) "Translation saved!" else "Translation updated!"
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

    fun deleteTranslation(id: String, traceSlug: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, snackBarMessage = null) }
            deleteTranslationUseCase(id).collectLatest { result ->
                result.fold(
                    onSuccess = {
                        _viewState.update { it.copy(isLoading = false, deleteSuccess = true) }
                        getTranslation(slug = traceSlug)
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

    fun onTranslationSelected(translation: TraceTranslation) {
        _viewState.update {
            it.copy(
                selectedTranslation = translation,
                language = translation.language,
                titleTranslated = translation.title ?: "",
                descriptionTranslated = translation.description ?: "",
                contentTranslated = translation.content ?: emptyList(),
                passagesTranslated = translation.passages ?: emptyList(),
                videosTranslated = translation.videos ?: emptyList(),
            )
        }
    }

    fun onNewTranslation(language: TranslationLanguage = TranslationLanguage.ES) {
        _viewState.update {
            it.copy(
                selectedTranslation = null,
                language = language,
                titleTranslated = "",
                descriptionTranslated = "",
                contentTranslated = emptyList(),
                passagesTranslated = emptyList(),
                videosTranslated = emptyList(),
            )
        }
    }

    fun onLanguageChanged(value: TranslationLanguage) =
        _viewState.update { it.copy(language = value) }

    fun onTitleTranslatedChanged(value: String) =
        _viewState.update { it.copy(titleTranslated = value) }

    fun onDescriptionTranslatedChanged(value: String) =
        _viewState.update { it.copy(descriptionTranslated = value) }

    fun onContentTranslatedChanged(content: List<String>) =
        _viewState.update { it.copy(contentTranslated = content) }

    fun onPassagesTranslatedChanged(passages: List<Passage>) =
        _viewState.update { it.copy(passagesTranslated = passages) }

    fun onVideosTranslatedChanged(videos: List<String>) =
        _viewState.update { it.copy(videosTranslated = videos) }

    fun onCreateNewTrace() {
        _viewState.update {
            it.copy(
                selectedTrace = null,
                title = "",
                slug = "",
                description = "",
                year = "",
                era = TraceEra.UNKNOWN,
                imageUrl = "",
                heroImageUrl = "",
                latitude = "",
                longitude = "",
                published = false,
                content = emptyList(),
                passages = emptyList(),
                videos = emptyList(),
                sources = emptyList(),
            )
        }
    }

    fun onContentChanged(content: List<String>) = _viewState.update { it.copy(content = content) }
    fun onPassagesChanged(passages: List<Passage>) =
        _viewState.update { it.copy(passages = passages) }

    fun onVideosChanged(videos: List<Video>) = _viewState.update { it.copy(videos = videos) }
    fun onSourcesChanged(sources: List<Source>) = _viewState.update { it.copy(sources = sources) }

    fun onDeleteSuccessConsumed() {
        _viewState.update { it.copy(deleteSuccess = false) }
    }

    fun onSaveSuccessConsumed() {
        _viewState.update { it.copy(saveSuccess = false) }
    }

    fun onTranslationSaveSuccessConsumed() {
        _viewState.update { it.copy(translationSaveSuccess = false) }
    }

    fun onShowTranslationSheet() {
        _viewState.update { it.copy(showTranslationSheet = true) }
    }

    fun onHideTranslationSheet() {
        _viewState.update { it.copy(showTranslationSheet = false) }
    }

    fun onFirstTimeCreatedConsumed() {
        _viewState.update { it.copy(isFirstTimeCreated = false) }
    }

    fun onSnackBarMessageConsumed() {
        _viewState.update { it.copy(snackBarMessage = null) }
    }
}

data class TraceViewState(
    val traces: List<Trace> = emptyList(),
    val isLoading: Boolean = false,
    val snackBarMessage: String? = null,
    val isDeleting: Boolean = false,
    val selectedTrace: Trace? = null,
    val selectedTranslation: TraceTranslation? = null,
    val isSaving: Boolean = false,
    val isFirstTimeCreated: Boolean = false,
    val saveSuccess: Boolean = false,
    val translationSaveSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val isUploadingImage: Boolean = false,
    val pendingImageUrls: List<String> = emptyList(),
    val showTranslationSheet: Boolean = false,

    // Form fields
    val title: String = "",
    val slug: String = "",
    val description: String = "",
    val year: String = "",
    val era: TraceEra = TraceEra.UNKNOWN,
    val imageUrl: String = "",
    val heroImageUrl: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val published: Boolean = false,
    val content: List<String> = emptyList(),
    val passages: List<Passage> = emptyList(),
    val videos: List<Video> = emptyList(),
    val sources: List<Source> = emptyList(),
    val translations: List<TraceTranslation> = emptyList(),

    //Translations
    val language: TranslationLanguage = TranslationLanguage.ES,
    val titleTranslated: String = "",
    val descriptionTranslated: String = "",
    val contentTranslated: List<String> = emptyList(),
    val passagesTranslated: List<Passage> = emptyList(),
    val videosTranslated: List<String> = emptyList(),
)

fun TraceViewState.toTrace(): Trace = Trace(
    id = selectedTrace?.id,
    slug = slug,
    title = title,
    description = description.ifBlank { null },
    year = year.toIntOrNull(),
    era = era,
    imageUrl = imageUrl.ifBlank { null },
    heroImageUrl = heroImageUrl.ifBlank { null },
    latitude = latitude.toDoubleOrNull(),
    longitude = longitude.toDoubleOrNull(),
    content = content.filter { it.isNotBlank() },
    passages = passages,
    videos = videos,
    sources = sources,
    published = published,
    createdAt = selectedTrace?.createdAt,
    updatedAt = selectedTrace?.updatedAt
)

enum class ImageType {
    CARD,
    HERO,
}