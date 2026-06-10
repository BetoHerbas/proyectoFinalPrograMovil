package com.ucb.proyectofinal.lists.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucb.proyectofinal.lists.presentation.effect.EditListEffect
import com.ucb.proyectofinal.lists.presentation.intent.EditListIntent
import com.ucb.proyectofinal.lists.presentation.viewmodel.EditListViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource
import proyectofinalprogramovil.composeapp.generated.resources.Res
import proyectofinalprogramovil.composeapp.generated.resources.create_list_cancel
import proyectofinalprogramovil.composeapp.generated.resources.edit_list_button
import proyectofinalprogramovil.composeapp.generated.resources.create_list_name_label
import proyectofinalprogramovil.composeapp.generated.resources.create_list_name_placeholder
import proyectofinalprogramovil.composeapp.generated.resources.create_list_desc_label
import proyectofinalprogramovil.composeapp.generated.resources.create_list_desc_placeholder
import proyectofinalprogramovil.composeapp.generated.resources.create_list_private
import proyectofinalprogramovil.composeapp.generated.resources.create_list_private_desc

// ─── Color palette (consistent with the app) ───
private val BgGradientStart = Color(0xFF0A1D26)
private val BgGradientMid = Color(0xFF072F2C)
private val BgGradientEnd = Color(0xFF061A25)
private val Accent = Color(0xFF1CF1D2)
private val AccentDark = Color(0xFF043636)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFFBCD1D8)
private val TextMuted = Color(0xFF72919A)
private val TextLabel = Color(0xFF23E9D0)
private val FieldBg = Color(0x44233542)
private val FieldBorderFocused = Color(0x5538DBC9)
private val FieldBorderUnfocused = Color(0x3335AFA3)
private val DividerColor = Color(0x2236B4AA)
private val SwitchTrackOff = Color(0xFF536D75)
private val SwitchThumbOff = Color(0xFFE1ECEF)
private val SubtitleColor = Color(0xFF8AA7AF)
private val CounterColor = Color(0xFF6E8A93)

@Composable
fun EditListScreen(
    listId: String,
    listName: String,
    description: String,
    coverImageUrl: String?,
    isPublic: Boolean,
    listType: String,
    onNavigateBack: () -> Unit,
    viewModel: EditListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(listId) {
        viewModel.onIntent(
            EditListIntent.LoadList(
                listId = listId,
                name = listName,
                description = description,
                coverImageUrl = coverImageUrl,
                isPublic = isPublic,
                listType = listType
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is EditListEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is EditListEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
                is EditListEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(BgGradientStart, BgGradientMid, BgGradientEnd)
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // ─── Top bar: Cancelar + Editar ───
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onNavigateBack) {
                        Text(
                            stringResource(Res.string.create_list_cancel),
                            color = TextSecondary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Button(
                        onClick = { viewModel.onIntent(EditListIntent.SaveChanges) },
                        enabled = !state.isSaving,
                        shape = RoundedCornerShape(999.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Accent,
                            contentColor = AccentDark
                        )
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = AccentDark,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            stringResource(Res.string.edit_list_button),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ─── Name field ───
                Text(
                    stringResource(Res.string.create_list_name_label),
                    color = TextLabel,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.onIntent(EditListIntent.UpdateName(it)) },
                    placeholder = { Text(stringResource(Res.string.create_list_name_placeholder), color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedBorderColor = FieldBorderFocused,
                        unfocusedBorderColor = FieldBorderUnfocused,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = Accent
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ─── Description field ───
                Text(
                    stringResource(Res.string.create_list_desc_label),
                    color = TextLabel,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.onIntent(EditListIntent.UpdateDescription(it)) },
                    placeholder = {
                        Text(
                            stringResource(Res.string.create_list_desc_placeholder),
                            color = TextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FieldBg,
                        unfocusedContainerColor = FieldBg,
                        focusedBorderColor = FieldBorderFocused,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = Accent
                    )
                )
                Text(
                    text = "${state.description.length}/150",
                    color = CounterColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(modifier = Modifier.weight(1f))

                // ─── Privacy toggle ───
                HorizontalDivider(color = DividerColor)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(Res.string.create_list_private),
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            stringResource(Res.string.create_list_private_desc),
                            color = SubtitleColor,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = state.isPrivate,
                        onCheckedChange = {
                            viewModel.onIntent(EditListIntent.TogglePrivate(it))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextPrimary,
                            checkedTrackColor = Accent,
                            uncheckedThumbColor = SwitchThumbOff,
                            uncheckedTrackColor = SwitchTrackOff
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
