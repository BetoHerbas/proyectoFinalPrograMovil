package com.ucb.proyectofinal.auth.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.proyectofinal.designsystem.theme.AppTheme

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101715))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
             Canvas(modifier = Modifier.fillMaxSize()) {
                 drawCircle(color = Color(0xFF1A2421), radius = size.minDimension / 2.5f)
                 drawCircle(
                     color = Color.White.copy(alpha = 0.05f), 
                     radius = size.minDimension / 2f, 
                     style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                 )
                 drawCircle(
                     color = Color.White.copy(alpha = 0.02f), 
                     radius = size.minDimension / 1.5f, 
                     style = Stroke(width = 2f)
                 )
             }
             Text("▶️", fontSize = 32.sp)
             
             Text("📖", modifier = Modifier.align(Alignment.TopCenter).offset(y = 20.dp), fontSize = 20.sp)
             Text("🎙️", modifier = Modifier.align(Alignment.TopEnd).offset(x = (-20).dp, y = 40.dp), fontSize = 20.sp)
             Text("🎬", modifier = Modifier.align(Alignment.BottomStart).offset(x = 20.dp, y = (-40).dp), fontSize = 20.sp)
             Text("🎵", modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-30).dp, y = (-20).dp), fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Bienvenido de nuevo",
            style = AppTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Conecta con todo tu contenido favorito.",
            style = AppTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Correo Electrónico", color = Color.White, style = AppTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("hola@ejemplo.com", color = Color.Gray) },
                leadingIcon = { Text("✉️") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFF1A2421),
                    focusedContainerColor = Color(0xFF1A2421),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF00E5B6),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Contraseña", color = Color.White, style = AppTheme.typography.labelLarge)
                TextButton(onClick = { /* TODO */ }, contentPadding = PaddingValues(0.dp)) {
                    Text("¿Olvidaste tu contraseña?", color = Color(0xFF00E5B6), style = AppTheme.typography.labelMedium)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("........", color = Color.Gray) },
                leadingIcon = { Text("🔒") },
                trailingIcon = { 
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if(passwordVisible) "👁️" else "🙈")
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFF1A2421),
                    focusedContainerColor = Color(0xFF1A2421),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF00E5B6),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { isLoading = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5B6), contentColor = Color(0xFF101715))
        ) {
            if (isLoading) {
                 CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF101715))
            } else {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Text("Iniciar Sesión", fontWeight = FontWeight.Bold, style = AppTheme.typography.bodyLarge)
                     Spacer(modifier = Modifier.width(8.dp))
                     Text("➔")
                 }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f))
            Text(" O continúa con ", color = Color.Gray, style = AppTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp))
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2421), contentColor = Color.White)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("G", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google")
                }
            }
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2421), contentColor = Color.White)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🍎", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apple")
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¿No tienes una cuenta?",
                style = AppTheme.typography.bodyMedium,
                color = Color.Gray
            )
            TextButton(onClick = { /* Navigate */ }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                Text(
                    text = "Regístrate",
                    style = AppTheme.typography.labelLarge,
                    color = Color(0xFF00E5B6),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

