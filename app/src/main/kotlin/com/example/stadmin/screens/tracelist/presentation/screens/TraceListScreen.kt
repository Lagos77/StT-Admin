package com.example.stadmin.screens.tracelist.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stadmin.R
import com.example.stadmin.screens.trace.presentation.TraceViewModel
import com.example.stadmin.screens.trace.presentation.TraceViewModelFactory
import com.example.stadmin.screens.trace.presentation.components.list.TraceCard
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.common.TopBar
import com.example.stadmin.ui.common.TopBarType
import com.example.stadmin.ui.theme.STAdminTheme

@Composable
fun TraceListScreen(
    onBack: () -> Unit,
    onNavigateToDetail: () -> Unit,
) {
    val viewModel: TraceViewModel = viewModel(factory = TraceViewModelFactory(LocalContext.current))
    val state by viewModel.viewState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackBarMessage) {
        state.snackBarMessage?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.onSnackBarMessageConsumed()
        }
    }

    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess) {
            //TODO avoid hardcoding
            snackBarHostState.showSnackbar("Trace deleted successfully")
            viewModel.onDeleteSuccessConsumed()
        }
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            viewModel.onSaveSuccessConsumed()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopBar(
                type = TopBarType.LIST,
                title = stringResource(R.string.trace_list_title),
                onBack = {
                    viewModel.onSnackBarMessageConsumed()
                    onBack()
                },
                onAdd = {
                    viewModel.onCreateNewTrace()
                    onNavigateToDetail()
                },
                onRefresh = {
                    viewModel.getTraceList()
                }
            )
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
        TraceListScreen(
            onBack = {},
            onNavigateToDetail = {}
        )
    }
}
