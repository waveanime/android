package com.waveanime.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Accueil", Icons.Rounded.Home, Icons.Outlined.Home),
    WAVELISTS("Wavelists", Icons.Rounded.Bookmarks, Icons.Outlined.Bookmarks),
    CATALOG("Catalogue", Icons.Rounded.Subscriptions, Icons.Outlined.Subscriptions),
    PLANNING("Planning", Icons.Rounded.Event, Icons.Outlined.Event),
    PROFILE("Toi", Icons.Rounded.AccountCircle, Icons.Outlined.AccountCircle)
}