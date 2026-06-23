package com.example.stadmin.screens.tracelist.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.stadmin.R
import com.example.stadmin.screens.trace.domain.model.Trace
import com.example.stadmin.screens.trace.presentation.TraceViewModel
import com.example.stadmin.screens.trace.presentation.TraceViewState
import com.example.stadmin.screens.trace.presentation.components.list.TraceCard
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.buttons.ContainerColor
import com.example.stadmin.ui.buttons.CustomizedButton
import com.example.stadmin.ui.theme.STAdminTheme

@Composable
fun TraceListScreen(
    onBack: () -> Unit,
    onNavigateToDetail: () -> Unit,
) {
    val viewModel: TraceViewModel = hiltViewModel()
    val state by viewModel.viewState.collectAsState()

    TraceListContent(
        onBack = {
            viewModel.onSnackBarMessageConsumed()
            onBack()
        },
        onAddTrace = {
            viewModel.onCreateNewTrace()
            onNavigateToDetail()
        },
        state = state,
        onRefresh = viewModel::getTraceList,
        onTraceSelected = {
            viewModel.onTraceSelected(it)
            onNavigateToDetail()
        },
        onDeleteTrace = { viewModel.deleteTrace(it) },
        onSnackBarMessageConsumed = { viewModel.onSnackBarMessageConsumed() }
    )
}

@Composable
private fun TraceListContent(
    state: TraceViewState,
    onAddTrace: () -> Unit,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    onTraceSelected: (Trace) -> Unit,
    onDeleteTrace: (String) -> Unit,
    onSnackBarMessageConsumed: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackBarMessage) {
        state.snackBarMessage?.let {
            snackBarHostState.showSnackbar(it)
            onSnackBarMessageConsumed()
        }
    }

    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess) {
            snackBarHostState.showSnackbar("Trace deleted successfully")
            onSnackBarMessageConsumed()
        }
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            onSnackBarMessageConsumed()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.small, vertical = Spacing.verySmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CustomizedButton(
                    label = null,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    containerColor = ContainerColor.WHITE,
                    onClick = onBack,
                )
                Text(
                    text = "Total: ${state.traces.size} · Draft: ${state.traces.count { !it.isComplete }}" ,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    CustomizedButton(
                        label = null,
                        icon = Icons.Filled.Refresh,
                        containerColor = ContainerColor.WHITE,
                        onClick = onRefresh,
                    )
                    CustomizedButton(
                        label = null,
                        icon = Icons.Filled.Add,
                        containerColor = ContainerColor.WHITE,
                        onClick = onAddTrace,
                    )
                }
            }
            /*
            TopBar(
                type = TopBarType.LIST,
                title = stringResource(R.string.trace_list_title),
                onBack = onBack,
                onAdd = onAddTrace,
                onRefresh = onRefresh,
            )

             */
        }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingScreen(paddingValues)
            state.traces.isEmpty() -> EmptyScreen(paddingValues)
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
                            onClick = { onTraceSelected(trace) },
                            onDelete = { onDeleteTrace(trace.slug) }
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun LoadingScreen(paddingValues: PaddingValues) {
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

@Composable
private fun EmptyScreen(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.trace_list_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Preview
@Composable
private fun Example() {
    STAdminTheme {
        TraceListContent(
            state = TraceViewState(),
            onAddTrace = {},
            onRefresh = {},
            onBack = {},
            onTraceSelected = {},
            onDeleteTrace = {},
            onSnackBarMessageConsumed = {}
        )
    }
}
