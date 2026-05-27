package com.ucb.proyectofinal.onboarding.presentation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ucb.proyectofinal.onboarding.presentation.state.OnboardingEffect
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import com.ucb.proyectofinal.onboarding.presentation.viewmodel.OnboardingViewModel

// ── Color palette ───────────────────────────────────────────────────────────────
private val BgDark1 = Color(0xFF0F0F1A)
private val BgDark2 = Color(0xFF1A1A2E)
private val AccentPurple = Color(0xFF6C63FF)
private val AccentTeal = Color(0xFF00E5B6)
private val TextPrimary = Color.White
private val TextSecondary = Color.White.copy(alpha = 0.7f)
private val DotInactive = Color.White.copy(alpha = 0.3f)

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // Collect navigation effects
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                OnboardingEffect.NavigateToHome -> onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = listOf(BgDark1, BgDark2))
            )
    ) {
        when {
            state.isLoading -> LoadingContent()
            state.error != null && state.slides.isEmpty() -> ErrorContent(state.error!!)
            state.slides.isNotEmpty() -> {
                val pagerState = rememberPagerState(pageCount = { state.slides.size })
                val isFirstPage = pagerState.currentPage == 0
                val isLastPage = pagerState.currentPage == state.slides.lastIndex

                Column(modifier = Modifier.fillMaxSize()) {

                    // ── Top bar: Skip button ────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { viewModel.skipOnboarding() }) {
                            Text(
                                text = "Omitir",
                                color = TextSecondary,
                                fontSize = 15.sp
                            )
                        }
                    }

                    // ── Pager ────────────────────────────────────────────────
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) { page ->
                        val slide = state.slides[page]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 32.dp)
                        ) {
                            // Image
                            AsyncImage(
                                model = slide.imageUrl,
                                contentDescription = slide.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                                    .clip(RoundedCornerShape(20.dp))
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Title
                            Text(
                                text = slide.title,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                lineHeight = 32.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Description
                            Text(
                                text = slide.description,
                                fontSize = 15.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    // ── Dots indicator ───────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(state.slides.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            val width by animateDpAsState(
                                targetValue = if (isSelected) 24.dp else 8.dp,
                                animationSpec = tween(300),
                                label = "dotWidth"
                            )
                            val color by animateColorAsState(
                                targetValue = if (isSelected) AccentPurple else DotInactive,
                                animationSpec = tween(300),
                                label = "dotColor"
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .height(8.dp)
                                    .width(width)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }

                    // ── Bottom buttons ───────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 36.dp, top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous button (hidden on first page)
                        if (!isFirstPage) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Anterior",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Anterior", fontSize = 15.sp)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        // Next / Start button
                        if (isLastPage) {
                            Button(
                                onClick = { viewModel.completeOnboarding() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentTeal,
                                    contentColor = BgDark1
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Text(
                                    "Iniciar",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentPurple,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Text("Siguiente", fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Siguiente",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Loading state ───────────────────────────────────────────────────────────────
@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = AccentPurple, strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cargando…", color = TextSecondary, fontSize = 14.sp)
        }
    }
}

// ── Error state ─────────────────────────────────────────────────────────────────
@Composable
private fun ErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Error: $message",
            color = Color(0xFFFF6B6B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}
