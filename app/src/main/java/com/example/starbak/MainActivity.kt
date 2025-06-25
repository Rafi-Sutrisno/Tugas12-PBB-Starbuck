package com.example.starbak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colors.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "AggreementScreen") {
        composable("AggreementScreen") {
            AggreementScreen(navController)
        }

        composable(route = "OTPPhone"){
            OTPPhoneScreen(navController)
        }

        composable(route = "OTPCode"){
            OTPCodeScreen(navController)
        }
        composable(route = "RegisterScreen"){
            RegisterScreen(navController)
        }
    }
}