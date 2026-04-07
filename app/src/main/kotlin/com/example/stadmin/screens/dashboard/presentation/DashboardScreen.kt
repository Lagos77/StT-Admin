package com.example.stadmin.screens.dashboard.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.stadmin.R
import com.example.stadmin.ui.Border
import com.example.stadmin.ui.Sizing
import com.example.stadmin.ui.Spacing
import com.example.stadmin.ui.theme.STAdminTheme

@Composable
fun DashboardScreen(
    onNavigateToTraces: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onSignOut: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Spacing.medium),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.dashboard_screen_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.padding(vertical = Spacing.large))

            DashboardCard(
                title = stringResource(R.string.dashboard_traces_title),
                subtitle = stringResource(R.string.dashboard_traces_body),
                onClick = onNavigateToTraces
            )
            DashboardCard(
                title = stringResource(R.string.dashboard_home_title),
                subtitle = stringResource(R.string.dashboard_home_body),
                onClick = onNavigateToHome
            )
            DashboardCard(
                title = stringResource(R.string.dashboard_about_title),
                subtitle = stringResource(R.string.dashboard_about_body),
                onClick = onNavigateToAbout
            )

            Spacer(modifier = Modifier.padding(vertical = Spacing.large))

            TextButton(onClick = onSignOut) {
                Text(stringResource(R.string.dashboard_sign_out), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.small),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = Border.small,
            color = MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(Sizing.cardRadius)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Preview
@Composable
private fun Example() {
    STAdminTheme {
        DashboardScreen(
            onNavigateToTraces = {},
            onNavigateToHome = {},
            onNavigateToAbout = {},
            onSignOut = {},
        )
    }
}