package com.example.stadmin.screens.trace.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stadmin.screens.trace.presentation.TraceViewModel
import com.example.stadmin.screens.trace.presentation.TraceViewModelFactory
import com.example.stadmin.ui.Shapes
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.theme.STAdminTheme

@Composable
fun TraceListScreen(
    onBack: () -> Unit,
    onNavigateToDetail: () -> Unit,
) {
    val viewModel: TraceViewModel = viewModel(factory = TraceViewModelFactory(LocalContext.current))
    val state by viewModel.viewState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.onErrorConsumed()
        }
    }

    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess) {
            snackBarHostState.showSnackbar("Trace deleted successfully")
            viewModel.onDeleteSuccessConsumed()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TraceListTopBar(
                onBack = {
                    viewModel.onErrorConsumed()
                    onBack()
                },
                onAdd = {
                    viewModel.onCreateNewTrace()
                    onNavigateToDetail()
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            state.traces.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No traces found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = Spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                    contentPadding = PaddingValues(vertical = Spacing.medium)
                ) {
                    items(state.traces, key = { it.slug }) { trace ->
                        TraceCard(
                            trace = trace,
                            onClick = {
                                viewModel.onTraceSelected(trace)
                                onNavigateToDetail()
                            },
                            onDelete = { viewModel.deleteTrace(trace.slug) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TraceListTopBar(
    onBack: () -> Unit,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(Sizing.thumbnailSmall)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = Shapes.small
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(Sizing.iconMedium)
            )
        }

        Text(
            text = "Traces",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        IconButton(
            onClick = onAdd,
            modifier = Modifier
                .size(Sizing.thumbnailSmall)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = Shapes.small
                )
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add trace",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(Sizing.iconMedium)
            )
        }
    }
}

@Preview
@Composable
private fun Example() {
    STAdminTheme {
        TraceListScreen(
            onBack = {},
            onNavigateToDetail = {}
        )
    }
}
