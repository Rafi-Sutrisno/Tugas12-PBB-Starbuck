package com.example.starbak

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPCodeScreen(navController: NavController) {
    val context = LocalContext.current
    val otpLength = 6
    var otpValue by remember { mutableStateOf(TextFieldValue(text = "", selection = TextRange(0))) }

    val verificationId: String? = navController.previousBackStackEntry?.savedStateHandle?.get("verificationID")
    val phoneNumber: String? = navController.previousBackStackEntry?.savedStateHandle?.get("phoneNumber")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.Gray,
            modifier = Modifier
                .size(28.dp)
                .clickable { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card (
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults
                .cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "OTP Icon",
                    tint = Color(0xFF007A5E),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Enter OTP",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We sent a code to ${phoneNumber ?: "your phone"}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                BasicTextField(
                    value = otpValue,
                    onValueChange = {
                        if (it.text.length <= otpLength && it.text.all { char -> char.isDigit() }) {
                            otpValue = it
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    decorationBox = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(otpLength) { index ->
                                val char = otpValue.text.getOrNull(index)?.toString() ?: ""
                                Box(
                                    modifier = Modifier
                                        .width(48.dp)
                                        .height(56.dp)
                                        .border(
                                            width = 1.dp,
                                            color = Color.LightGray,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Didn't receive code?",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RESEND",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007A5E),
                        modifier = Modifier.clickable {
                            if (phoneNumber != null) {
                                Toast.makeText(context, "Resending OTP to $phoneNumber...", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Phone number unavailable.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button (
                    onClick = {
                        if (otpValue.text.length == otpLength && verificationId != null) {
                            val credential = PhoneAuthProvider.getCredential(verificationId, otpValue.text)
                            Firebase.auth.signInWithCredential(credential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "OTP Verified!", Toast.LENGTH_SHORT).show()
                                        navController.currentBackStackEntry?.savedStateHandle?.set("phoneNumber", phoneNumber)
                                        navController.navigate("RegisterScreen")
                                    } else {
                                        Toast.makeText(context, "OTP failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Please enter a $otpLength-digit OTP", Toast.LENGTH_SHORT).show()
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
                    Text("VERIFY", fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                }
            }
        }
    }
}
