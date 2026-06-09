package com.ucb.proyectofinal.lists.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ucb.proyectofinal.designsystem.theme.AppTheme
import com.ucb.proyectofinal.lists.domain.model.CastMember
import com.ucb.proyectofinal.lists.domain.model.ItemDetail
import com.ucb.proyectofinal.lists.domain.model.Review
import com.ucb.proyectofinal.lists.presentation.intent.ItemDetailIntent
import com.ucb.proyectofinal.lists.presentation.viewmodel.ItemDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

// ─── Estilos locales específicos para ItemDetailScreen ───
private val BgDark = Color(0xFF0B1D29)
private val BgTeal = Color(0xFF0A3736)
private val BgDeep = Color(0xFF0D1B2D)
private val Accent = Color(0xFF00E5B6)
private val AccentBright = Color(0xFF22F2D4)
private val CardBg = Color(0xFF1A2421)
private val TextPrimary = Color(0xFFE8FAFF)
private val TextSecondary = Color(0xFF8FB3BC)

@Composable
fun ItemDetailScreen(
    listId: String,
    itemId: String,
    onNavigateBack: () -> Unit,
    viewModel: ItemDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(itemId, listId) {
        viewModel.onIntent(ItemDetailIntent.LoadDetail(itemId, listId))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(BgDark, BgTeal, BgDeep)
                )
            )
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Accent
            )
        } else {
            state.item?.let { item ->
                ItemDetailContent(
                    item = item,
                    onBack = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun ItemDetailContent(
    item: ItemDetail,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item { HeroHeader(item, onBack) }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                DetailHeader(item)
                Spacer(modifier = Modifier.height(20.dp))
                RatingCard(item)
                Spacer(modifier = Modifier.height(24.dp))
                SynopsisSection(item.description)
                Spacer(modifier = Modifier.height(24.dp))
                item.parentsGuide?.let {
                    ParentsGuideCard(it)
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.IosShare, contentDescription = null, tint = Accent)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.StarBorder, contentDescription = null, tint = Accent)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item {
            SectionTitleRow(title = "Cast & Crew", onSeeAll = {})
            CastLazyRow(item.cast)
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            SectionTitleRow(title = "Top Reviews", onSeeAll = {})
            ReviewsLazyRow(item.reviews)
        }
    }
}

@Composable
private fun HeroHeader(item: ItemDetail, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .background(Color.Black)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(item.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = Icons.Default.BrokenImage.let { null } // Placeholder logic
        )

        // Capa de degradado para asegurar visibilidad de textos
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BgDark.copy(alpha = 0.4f),
                            BgDark
                        )
                    )
                )
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun DetailHeader(item: ItemDetail) {
    Column {
        Text(
            text = item.title.value,
            style = AppTheme.typography.headlineLarge,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val metadata = when (item) {
                is ItemDetail.Movie -> "${item.year}  •  ${item.duration}  •  "
                is ItemDetail.Series -> "${item.year}  •  ${item.seasons} Seasons  •  "
                is ItemDetail.Book -> "${item.author}  •  "
            }
            Text(
                text = metadata,
                style = AppTheme.typography.bodySmall,
                color = TextSecondary
            )
            val director = when (item) {
                is ItemDetail.Movie -> item.director
                is ItemDetail.Series -> item.creator
                is ItemDetail.Book -> item.publisher
            }
            Text(
                text = director,
                style = AppTheme.typography.bodySmall,
                color = Accent
            )
        }
    }
}

@Composable
private fun RatingCard(item: ItemDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFAD00),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = " ${item.rating}",
                    style = AppTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " /10",
                    style = AppTheme.typography.bodySmall,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Surface(color = Color(0xFFF5C518), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = "IMDb",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color.Black,
                        style = AppTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item.tags.forEach { tag ->
                    Surface(
                        color = BgDark.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(0.5.dp, Accent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.EmojiEvents,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFFFFAD00)
                            )
                            Text(
                                text = " $tag",
                                style = AppTheme.typography.labelMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SynopsisSection(description: String) {
    Column {
        Text(
            text = "Synopsis",
            style = AppTheme.typography.headlineSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = AppTheme.typography.bodyMedium,
            color = TextSecondary,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun ParentsGuideCard(guide: com.ucb.proyectofinal.lists.domain.model.ParentsGuide) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Parents Guide",
                style = AppTheme.typography.labelLarge,
                color = AccentBright
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = BgTeal,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = guide.classification,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    color = TextPrimary,
                    style = AppTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            guide.details.forEach { detail ->
                Text(
                    text = "• $detail",
                    style = AppTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun SectionTitleRow(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AppTheme.typography.headlineSmall,
            color = TextPrimary
        )
        TextButton(onClick = onSeeAll) {
            Text(
                text = "See All",
                color = Accent,
                style = AppTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun CastLazyRow(cast: List<CastMember>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cast) { member ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(CardBg)
                ) {
                    AsyncImage(
                        model = member.imageUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=200",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = member.name,
                    style = AppTheme.typography.labelMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = member.role,
                    style = AppTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ReviewsLazyRow(reviews: List<Review>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(reviews) { review ->
            Card(
                modifier = Modifier.width(280.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row {
                        repeat(5) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFAD00),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = review.content,
                        style = AppTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${review.author} • ${review.date}",
                        style = AppTheme.typography.labelMedium,
                        color = Accent
                    )
                }
            }
        }
    }
}
