package com.maksimowiczm.findmyip.addresshistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.findmyip.R
import com.maksimowiczm.findmyip.domain.AddressHistory
import com.maksimowiczm.findmyip.ui.theme.FindMyIpAppTheme

@Composable
fun AddressHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: AddressHistoryViewModel = hiltViewModel()
) {
    val hasPermission by viewModel.hasPermission.collectAsStateWithLifecycle()
    val addressHistoryState by viewModel.addressHistoryState.collectAsStateWithLifecycle()

    AddressHistoryScreen(
        modifier = modifier,
        state = addressHistoryState,
        hasPermission = hasPermission,
        onGrantPermission = viewModel::onGrantPermission
    )
}

@Composable
private fun AddressHistoryScreen(
    state: AddressHistoryState,
    hasPermission: Boolean,
    onGrantPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state is AddressHistoryState.Loading) {
        return Box(modifier) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }

    // Only if the user has not granted permission and history is empty
    if (!hasPermission) {
        return if (state is AddressHistoryState.Loaded && !state.addressHistory.isEmpty()) {
            NoPermissionScreen(
                modifier = modifier,
                items = state.addressHistory,
                onGrantPermission = onGrantPermission
            )
        } else {
            NoPermissionScreen(
                modifier = modifier,
                onGrantPermission = onGrantPermission
            )
        }
    }

    if (state is AddressHistoryState.Loaded) {
        return AddressHistoryList(
            modifier = modifier,
            items = state.addressHistory
        )
    }
}

@Composable
private fun NoPermissionScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    onGrantPermission: () -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    if (showDialog) {
        HistoryPermissionDialog(
            onDismiss = { showDialog = false },
            onGrantPermission = onGrantPermission
        )
    }

    Column(
        modifier = modifier.clickable { showDialog = true },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.history_no_permission_description),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify
        )

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(R.string.tap_to_enable),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun NoPermissionScreen(
    onGrantPermission: () -> Unit,
    items: List<AddressHistory>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    if (showDialog) {
        HistoryPermissionDialog(
            onDismiss = { showDialog = false },
            onGrantPermission = onGrantPermission
        )
    }

    Column(modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(onClick = { showDialog = true }),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.history_disabled_card_description),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
        }

        AddressHistoryList(items)
    }
}

@Composable
private fun AddressHistoryList(items: List<AddressHistory>, modifier: Modifier = Modifier) {
    if (items.isEmpty()) {
        AddressHistoryEmptyList(modifier)
    } else {
        LazyColumn(modifier) {
            items(
                items = items.mapIndexed { index, item -> item to index },
                key = { (_, index) -> index }
            ) { (it, index) ->
                AddressHistoryListItem(address = it)
                if (index < items.size - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun AddressHistoryListItem(address: AddressHistory, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(text = address.ip) },
        supportingContent = { Text(text = address.date) }
    )
}

@Composable
private fun AddressHistoryEmptyList(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = stringResource(R.string.empty)
        )
    }
}

@PreviewLightDark
@Composable
private fun NoPermissionScreenPreview() {
    FindMyIpAppTheme {
        Surface {
            NoPermissionScreen(
                onGrantPermission = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PermissionsDisabledScreenPreview() {
    FindMyIpAppTheme {
        Surface {
            NoPermissionScreen(
                items = listOf(
                    AddressHistory(
                        ip = "127.0.0.1",
                        date = "January 1, 2024"
                    )
                ),
                onGrantPermission = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AddressHistoryListItemPreview() {
    FindMyIpAppTheme {
        Surface {
            AddressHistoryListItem(
                modifier = Modifier.fillMaxWidth(),
                address = AddressHistory(
                    ip = "127.0.0.1",
                    date = "January 1, 2024"
                )
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AddressHistoryEmptyListPreview() {
    FindMyIpAppTheme {
        Surface {
            AddressHistoryEmptyList()
        }
    }
}
