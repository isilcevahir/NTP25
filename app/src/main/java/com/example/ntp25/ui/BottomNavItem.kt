package com.example.ntp25.ui


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Home : BottomNavItem("Ana Sayfa", Icons.Filled.Home, "home")
    object Profile : BottomNavItem("Profil", Icons.Filled.Person, "profile")
}
