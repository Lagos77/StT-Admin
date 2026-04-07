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
                        _viewState.update { it.copy(isLoading = false, traces = traces) }
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
                                saveSuccess = false,
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
    }

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

    fun onFirstTimeCreatedConsumed() {
        _viewState.update { it.copy(isFirstTimeCreated = false) }
    }

    fun onSnackBarMessageConsumed() {
        _viewState.update { it.copy(snackBarMessage = null) }
    }

    data class TraceViewState(
        val traces: List<Trace> = emptyList(),
        val isLoading: Boolean = false,
        val snackBarMessage: String? = null,
        val isDeleting: Boolean = false,
        val selectedTrace: Trace? = null,
        val isSaving: Boolean = false,
        val isFirstTimeCreated: Boolean = false,
        val saveSuccess: Boolean = false,
        val deleteSuccess: Boolean = false,
        val isUploadingImage: Boolean = false,
        val pendingImageUrls: List<String> = emptyList(),

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
    )
}

enum class ImageType {
    CARD,
    HERO,
}