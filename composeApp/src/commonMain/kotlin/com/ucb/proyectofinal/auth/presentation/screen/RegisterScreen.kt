package com.ucb.proyectofinal.auth.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucb.proyectofinal.auth.presentation.effect.AuthEffect
import com.ucb.proyectofinal.auth.presentation.intent.AuthIntent
import com.ucb.proyectofinal.auth.presentation.viewmodel.AuthViewModel
import com.ucb.proyectofinal.designsystem.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AuthEffect.NavigateToHome -> onRegisterSuccess()
                else -> Unit
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF171920), Color(0xFF10312C), Color(0xFF141A27)),
                    start = Offset.Zero,
                    end = Offset(1000f, 1800f)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xAA0D1818))
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign Up",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                color = Color(0xFF96A0A5),
                style = AppTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp))
            RegisterHeroOrbit()
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Unete al club",
                style = AppTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Organiza tus libros, peliculas y series\nen un solo lugar.",
                style = AppTheme.typography.bodyLarge,
                color = Color(0xFFA5B0B3),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))
            RegisterField(
                title = "NOMBRE COMPLETO",
                value = state.nameInput,
                onValueChange = { viewModel.onIntent(AuthIntent.UpdateName(it)) },
                placeholder = "Ej. Ana Garcia",
                icon = Icons.Outlined.Person,
                isError = state.nameError != null,
                errorText = state.nameError
            )

            Spacer(modifier = Modifier.height(14.dp))
            RegisterField(
                title = "CORREO ELECTRONICO",
                value = state.emailInput,
                onValueChange = { viewModel.onIntent(AuthIntent.UpdateEmail(it)) },
                placeholder = "hola@ejemplo.com",
                icon = Icons.Outlined.Email,
                isError = state.emailError != null,
                errorText = state.emailError
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "CONTRASENA",
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                style = AppTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.passwordInput,
                onValueChange = { viewModel.onIntent(AuthIntent.UpdatePassword(it)) },
                placeholder = { Text("Minimo 6 caracteres", color = Color(0xFF748087)) },
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFF6E7B82))
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = Color(0xFF758187)
                        )
                    }
                },
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0x66253639),
                    focusedContainerColor = Color(0x8021363E),
                    unfocusedBorderColor = Color(0x3348D7C1),
                    focusedBorderColor = Color(0xFF24EEC1),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF24EEC1)
                )
            )

            if (state.generalError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    state.generalError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = AppTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.onIntent(AuthIntent.Register) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1CE8C1),
                    contentColor = Color(0xFF102623)
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF0B2320))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Crear cuenta", fontWeight = FontWeight.SemiBold, style = AppTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("->")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0x3377848A))
                Text(
                    " O registrate con ",
                    color = Color(0xFF7D898F),
                    style = AppTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0x3377848A))
            }

            Spacer(modifier = Modifier.height(18.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RegisterSocialButton(modifier = Modifier.weight(1f), label = "Google", icon = "G")
                RegisterSocialButton(modifier = Modifier.weight(1f), label = "Apple", icon = "A")
            }

            Spacer(modifier = Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text(
                    "Ya tienes una cuenta?",
                    style = AppTheme.typography.bodyMedium,
                    color = Color(0xFF7E888E)
                )
                TextButton(onClick = onNavigateToLogin, contentPadding = PaddingValues(horizontal = 4.dp)) {
                    Text(
                        "Inicia sesion",
                        style = AppTheme.typography.labelLarge,
                        color = Color(0xFF1EF0C5),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisterHeroOrbit() {
    Box(modifier = Modifier.size(176.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x9047FFD3), Color(0x00000000)),
                    center = center,
                    radius = size.minDimension / 2
                ),
                radius = size.minDimension / 2
            )
            drawCircle(color = Color(0xFF163036), radius = size.minDimension / 3)
            drawCircle(
                color = Color(0x3346D6C1),
                radius = size.minDimension / 2.3f,
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f))
            )
            drawCircle(
                color = Color(0x2646D6C1),
                radius = size.minDimension / 2.65f,
                style = Stroke(width = 2f)
            )
        }
        Icon(Icons.Outlined.Person, contentDescription = null, tint = Color(0xFF2DE8CC), modifier = Modifier.size(34.dp))
        RegisterOrbitTag(text = "BK", modifier = Modifier.align(Alignment.TopStart).offset(x = 24.dp, y = 18.dp))
        RegisterOrbitTag(text = "PD", modifier = Modifier.align(Alignment.TopEnd).offset(x = (-26).dp, y = 28.dp))
        RegisterOrbitTag(text = "MV", modifier = Modifier.align(Alignment.BottomStart).offset(x = 24.dp, y = (-22).dp))
        RegisterOrbitTag(text = "MU", modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-22).dp, y = (-16).dp))
    }
}

@Composable
private fun RegisterOrbitTag(text: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = Color(0xFF17282D)) {
        Text(
            text = text,
            color = Color(0xFF73F0D5),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun RegisterField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isError: Boolean,
    errorText: String?
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, color = Color.White, style = AppTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF748087)) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFF6E7B82)) },
            isError = isError,
            supportingText = errorText?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0x66253639),
                focusedContainerColor = Color(0x8021363E),
                unfocusedBorderColor = Color(0x3348D7C1),
                focusedBorderColor = Color(0xFF24EEC1),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF24EEC1)
            )
        )
    }
}

@Composable
private fun RegisterSocialButton(modifier: Modifier = Modifier, label: String, icon: String) {
    Button(
        onClick = { },
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3341D6C1)),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0x66203238), contentColor = Color.White)
    ) {
        Text(icon, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}
