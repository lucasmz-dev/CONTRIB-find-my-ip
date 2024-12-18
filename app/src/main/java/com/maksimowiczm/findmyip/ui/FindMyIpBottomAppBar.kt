package com.maksimowiczm.findmyip.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.maksimowiczm.findmyip.R
import com.maksimowiczm.findmyip.ui.theme.FindMyIpAppTheme

@Composable
internal fun FindMyIpBottomAppBar(
    selectedBottomBarItem: () -> Route.Variant?,
    onHomeClick: () -> Unit,
    onAddressHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedBottomBarItem = selectedBottomBarItem() ?: return

    NavigationBar(modifier) {
        NavigationBarItem(
            selected = selectedBottomBarItem == Route.Variant.CurrentAddress,
            onClick = {
                if (selectedBottomBarItem != Route.Variant.CurrentAddress) {
                    onHomeClick()
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(R.string.home)
                )
            },
            label = { Text(stringResource(R.string.home)) }
        )
        NavigationBarItem(
            selected = selectedBottomBarItem == Route.Variant.AddressHistory,
            onClick = {
                if (selectedBottomBarItem != Route.Variant.AddressHistory) {
                    onAddressHistoryClick()
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_history_24),
                    contentDescription = stringResource(R.string.history)
                )
            },
            label = { Text(stringResource(R.string.history)) }
        )
        NavigationBarItem(
            selected = selectedBottomBarItem == Route.Variant.Settings,
            onClick = {
                if (selectedBottomBarItem != Route.Variant.Settings) {
                    onSettingsClick()
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            },
            label = { Text(stringResource(R.string.settings)) }
        )
    }
}

@PreviewLightDark
@Composable
private fun FindMyIpBottomAppBarPreview() {
    FindMyIpAppTheme {
        Surface {
            FindMyIpBottomAppBar(
                selectedBottomBarItem = { Route.Variant.AddressHistory },
                onHomeClick = {},
                onAddressHistoryClick = {},
                onSettingsClick = {}
            )
        }
    }
}
