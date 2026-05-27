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
import com.ucb.proyectofinal.designsystem.components.PrimaryButton
import com.ucb.proyectofinal.designsystem.components.SecondaryButton
import com.ucb.proyectofinal.designsystem.theme.AppTheme
import com.ucb.proyectofinal.lists.domain.model.CastMember
import com.ucb.proyectofinal.lists.domain.model.ItemDetail
import com.ucb.proyectofinal.lists.domain.model.Review
import com.ucb.proyectofinal.lists.presentation.intent.ItemDetailIntent
import com.ucb.proyectofinal.lists.presentation.viewmodel.ItemDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ItemDetailScreen(
    itemId: String,
    onNavigateBack: () -> Unit,
    viewModel: ItemDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(itemId) {
        viewModel.onIntent(ItemDetailIntent.LoadDetail(itemId))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AppTheme.colors.primary
            )
        } else {
            state.item?.let { item ->
                ItemDetailContent(
                    item = item,
                    onBack = onNavigateBack,
                    onIntent = viewModel::onIntent
                )
            }
        }
    }
}

@Composable
private fun ItemDetailContent(
    item: ItemDetail,
    onBack: () -> Unit,
    onIntent: (ItemDetailIntent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = AppTheme.spacing.extraLarge)
    ) {
        item {
            HeroHeader(item, onBack)
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.spacing.medium)
            ) {
                DetailHeader(item)
                Spacer(modifier = Modifier.height(AppTheme.spacing.medium))
                RatingCard(item)
                Spacer(modifier = Modifier.height(AppTheme.spacing.medium))
                SynopsisSection(item.description)
                Spacer(modifier = Modifier.height(AppTheme.spacing.medium))
                item.parentsGuide?.let {
                    ParentsGuideCard(it)
                    Spacer(modifier = Modifier.height(AppTheme.spacing.medium))
                }
                ActionButtonsRow(
                    onWatchTrailer = { onIntent(ItemDetailIntent.WatchTrailer) },
                    onAddToWatchlist = { onIntent(ItemDetailIntent.AddToWatchlist) }
                )
                Spacer(modifier = Modifier.height(AppTheme.spacing.large))
            }
        }

        item {
            SectionTitleRow(title = "Cast & Crew", onSeeAll = {})
            CastLazyRow(item.cast)
            Spacer(modifier = Modifier.height(AppTheme.spacing.large))
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
            .height(350.dp)
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            AppTheme.colors.background
                        )
                    )
                )
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(AppTheme.spacing.small)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
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
            color = AppTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.extraSmall))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.extraSmall)
        ) {
            val metadata = when (item) {
                is ItemDetail.Movie -> listOf(item.year, item.duration, item.director)
                is ItemDetail.Series -> listOf(item.year, "${item.seasons} Seasons", item.creator)
                is ItemDetail.Book -> listOf(item.author, "${item.pages} Pages", item.publisher)
            }

            metadata.forEachIndexed { index, text ->
                val isDirectorOrCreator = (item is ItemDetail.Movie && index == 2) || 
                                          (item is ItemDetail.Series && index == 2)
                Text(
                    text = text,
                    style = AppTheme.typography.bodySmall,
                    color = if (isDirectorOrCreator) AppTheme.colors.primary else AppTheme.colors.textSecondary
                )
                if (index < metadata.size - 1) {
                    Text(
                        text = "•",
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingCard(item: ItemDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.medium)
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
                Spacer(modifier = Modifier.width(AppTheme.spacing.extraSmall))
                Text(
                    text = item.rating.toString(),
                    style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = AppTheme.colors.textPrimary
                )
                Text(
                    text = " /10",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)) {
                    Badge(containerColor = Color(0xFFF5C518), contentColor = Color.Black) { Text("IMDb") }
                    Badge(containerColor = Color(0xFFFA320A), contentColor = Color.White) { Text("🍅 89%") }
                }
            }

            Text(
                text = "(${item.totalReviews / 1000000.0}M Reviews)",
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.medium))

            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)) {
                item.tags.forEach { tag ->
                    AssistChip(
                        onClick = {},
                        label = { Text(tag, style = AppTheme.typography.labelMedium) },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.EmojiEvents,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFFFAD00)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = AppTheme.colors.textPrimary,
                            containerColor = AppTheme.colors.background.copy(alpha = 0.5f)
                        ),
                        border = null
                    )
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
            color = AppTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(AppTheme.spacing.small))
        Text(
            text = description,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textSecondary,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun ParentsGuideCard(guide: com.ucb.proyectofinal.lists.domain.model.ParentsGuide) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(AppTheme.spacing.medium)) {
            Text(
                text = "Parents Guide",
                style = AppTheme.typography.labelLarge.copy(color = AppTheme.colors.primary)
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.small))
            Surface(
                color = AppTheme.colors.background,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.border(1.dp, AppTheme.colors.textSecondary.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            ) {
                Text(
                    text = guide.classification,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.height(AppTheme.spacing.small))
            guide.details.forEach { detail ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("•", color = AppTheme.colors.textSecondary)
                    Spacer(modifier = Modifier.width(AppTheme.spacing.small))
                    Text(
                        text = detail,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsRow(onWatchTrailer: () -> Unit, onAddToWatchlist: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)) {
        PrimaryButton(
            text = "Watch Trailer",
            onClick = onWatchTrailer,
            modifier = Modifier.fillMaxWidth()
        )
        SecondaryButton(
            text = "Add to Watchlist",
            onClick = onAddToWatchlist,
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.IosShare, contentDescription = "Share", tint = AppTheme.colors.textPrimary)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.StarBorder, contentDescription = "Rate", tint = AppTheme.colors.textPrimary)
            }
        }
    }
}

@Composable
private fun SectionTitleRow(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.textPrimary
        )
        TextButton(onClick = onSeeAll) {
            Text(
                text = "See All",
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.primary
            )
        }
    }
}

@Composable
private fun CastLazyRow(cast: List<CastMember>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = AppTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium)
    ) {
        items(cast) { member ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AppTheme.colors.surface)
                ) {
                    AsyncImage(
                        model = member.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(AppTheme.spacing.extraSmall))
                Text(
                    text = member.name,
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = member.role,
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textSecondary,
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
        contentPadding = PaddingValues(horizontal = AppTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium)
    ) {
        items(reviews) { review ->
            Card(
                modifier = Modifier.width(300.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(AppTheme.spacing.medium)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                if (index < review.rating / 2) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = null,
                                tint = Color(0xFFFFAD00),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(AppTheme.spacing.extraSmall))
                        Text(
                            text = "${review.rating}/10",
                            style = AppTheme.typography.labelMedium,
                            color = AppTheme.colors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(AppTheme.spacing.small))
                    Text(
                        text = review.content,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(AppTheme.spacing.medium))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(AppTheme.colors.primary)) {
                            Text(
                                review.author.take(1),
                                modifier = Modifier.align(Alignment.Center),
                                style = AppTheme.typography.labelMedium,
                                color = AppTheme.colors.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(AppTheme.spacing.small))
                        Text(
                            text = "${review.author} • ${review.date}",
                            style = AppTheme.typography.labelMedium,
                            color = AppTheme.colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}
