package com.example.ntp25.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.ntp25.LoginActivity
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🐾 Kedili Uygulama") },
                actions = {
                    TextButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    }) {
                        Text("Çıkış Yap")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                items = items,
                navController = navController,
                onItemClick = {
                    navController.navigate(it.route)
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainNavigationScreen(navController = navController)
        }
    }
}
