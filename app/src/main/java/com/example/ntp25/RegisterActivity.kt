package com.example.ntp25

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ntp25.ui.theme.NTP25Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        setContent {
            NTP25Theme {
                RegisterScreen { adSoyad, email, sifre, dogumGunu ->
                    kullaniciKaydet(adSoyad, email, sifre, dogumGunu)
                }
            }
        }
    }

    private fun kullaniciKaydet(adSoyad: String, email: String, sifre: String, dogumGunu: String) {
        if (adSoyad.isBlank() || email.isBlank() || sifre.isBlank() || dogumGunu.isBlank()) {
            Toast.makeText(applicationContext, "Tüm alanları doldurunuz.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email.trim(), sifre.trim())
            .addOnSuccessListener { sonuc ->
                val kullaniciId = sonuc.user?.uid ?: return@addOnSuccessListener
                val kullaniciBilgileri = mapOf(
                    "adSoyad" to adSoyad.trim(),
                    "email" to email.trim(),
                    "dogumGunu" to dogumGunu.trim()
                )

                val ref = db.reference

                ref.child("kullanicilar").child(kullaniciId)
                    .setValue(kullaniciBilgileri)
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "Veri kaydedilirken hata oluştu: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Kayıt başarısız: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
}

@Composable
fun RegisterScreen(
    onRegisterClick: (adSoyad: String, email: String, sifre: String, dogumGunu: String) -> Unit
) {
    var adSoyad by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var sifre by remember { mutableStateOf("") }
    var dogumGunu by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = adSoyad,
            onValueChange = { adSoyad = it },
            label = { Text("Ad Soyad") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = sifre,
            onValueChange = { sifre = it },
            label = { Text("Şifre") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = dogumGunu,
            onValueChange = { dogumGunu = it },
            label = { Text("Doğum Günü (GG/AA/YYYY)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onRegisterClick(adSoyad, email, sifre, dogumGunu) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Kayıt Ol")
        }
    }
}
