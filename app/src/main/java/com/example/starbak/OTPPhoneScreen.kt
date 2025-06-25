package com.example.starbak

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPPhoneScreen(navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }
    var showAlert by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Card (
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone Icon",
                    tint = Color(0xFF007A5E),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Verify your phone",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter your active phone number to receive an OTP.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (showAlert) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠ Please enter a valid phone number",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button (
                    onClick = {
                        if (phoneNumber.isNotBlank() && phoneNumber.length >= 7) {
                            val formattedPhoneNumber = if (!phoneNumber.startsWith("+")) "+62" + phoneNumber.removePrefix("0") else phoneNumber
                            Log.d("PhoneAuth", "Attempting to verify: $formattedPhoneNumber")

                            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                                .setPhoneNumber(formattedPhoneNumber)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(context as Activity)
                                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                        Toast.makeText(context, "Verification complete!", Toast.LENGTH_SHORT).show()
                                        Firebase.auth.signInWithCredential(credential).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                Toast.makeText(context, "Signed in!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Sign-in failed: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }

                                    override fun onVerificationFailed(e: FirebaseException) {
                                        Log.e("PhoneAuth", "Verification failed: ${e.message}", e)
                                        Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
                                        showAlert = true
                                    }

                                    override fun onCodeSent(verificationID: String, token: PhoneAuthProvider.ForceResendingToken) {
                                        Toast.makeText(context, "OTP sent!", Toast.LENGTH_SHORT).show()
                                        navController.currentBackStackEntry?.savedStateHandle?.set("verificationID", verificationID)
                                        navController.currentBackStackEntry?.savedStateHandle?.set("phoneNumber", formattedPhoneNumber)
                                        navController.navigate("OTPCode")
                                    }
                                }).build()

                            PhoneAuthProvider.verifyPhoneNumber(options)
                        } else {
                            showAlert = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007A5E),
                        contentColor = Color.White
                    )
                ) {
                    Text("SEND OTP", fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                }
            }
        }
    }
}

