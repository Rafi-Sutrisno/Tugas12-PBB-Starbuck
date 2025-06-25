package com.example.starbak

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var favoriteBeverage by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val phoneNumber: String? = navController.previousBackStackEntry?.savedStateHandle?.get("phoneNumber")
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auth = Firebase.auth
    val db = Firebase.firestore
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold (
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
                .background(Color(0xFFF8F9FA))
                .imePadding()
                .navigationBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Register Icon",
                tint = Color(0xFF007A5E),
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create Your Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerDialog = true }
            ) {
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Date of Birth") },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = favoriteBeverage,
                onValueChange = { favoriteBeverage = it },
                label = { Text("Favorite Beverage") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        when {
                            email.isBlank() || password.isBlank() || confirmPassword.isBlank() ||
                                    firstName.isBlank() || lastName.isBlank() || dateOfBirth.isBlank() || favoriteBeverage.isBlank() ->
                                snackbarHostState.showSnackbar("Please fill in all fields")

                            password != confirmPassword ->
                                snackbarHostState.showSnackbar("Passwords do not match")

                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                                snackbarHostState.showSnackbar("Invalid email format")

                            else -> {
                                isLoading = true
                                try {
                                    val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                                    val user = authResult.user
                                    if (user != null) {
                                        val userDetails = hashMapOf(
                                            "firstName" to firstName,
                                            "lastName" to lastName,
                                            "dateOfBirth" to dateOfBirth,
                                            "favoriteBeverage" to favoriteBeverage,
                                            "email" to email,
                                            "phoneNumber" to phoneNumber
                                        )
                                        db.collection("users").document(user.uid).set(userDetails)
                                            .addOnSuccessListener {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Registration successful!")
                                                }
                                                isLoading = false
                                            }
                                            .addOnFailureListener { e ->
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Firestore error: ${e.message}")
                                                }
                                                isLoading = false
                                                user.delete()
                                            }
                                    }
                                } catch (e: Exception) {
                                    isLoading = false
                                    val msg = when (e) {
                                        is FirebaseAuthUserCollisionException -> "Email already exists."
                                        is FirebaseAuthWeakPasswordException -> "Weak password."
                                        else -> e.message ?: "Registration failed."
                                    }
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007A5E),
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("REGISTER", letterSpacing = 1.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        dateOfBirth = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                    }
                    showDatePickerDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

