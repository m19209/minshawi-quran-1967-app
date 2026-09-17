package com.minshawi.quran1967.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minshawi.quran1967.R
import com.minshawi.quran1967.audio.AudioPlaybackManager
import com.minshawi.quran1967.data.QuranRepository
import com.minshawi.quran1967.data.Surah
import com.minshawi.quran1967.prayer.AzanScheduler
import com.minshawi.quran1967.prayer.CityLocation
import com.minshawi.quran1967.prayer.PrayerCalculator
import com.minshawi.quran1967.ui.components.AnimatedEqualizer
import com.minshawi.quran1967.ui.components.AzanOverlayDialog
import com.minshawi.quran1967.ui.components.PlayerBottomSheet
import com.minshawi.quran1967.ui.components.PrayerCard
import com.minshawi.quran1967.ui.components.SettingsDialog
import com.minshawi.quran1967.util.DownloadHelper
import com.minshawi.quran1967.ui.theme.BackgroundDark
import com.minshawi.quran1967.ui.theme.CardBorder
import com.minshawi.quran1967.ui.theme.EmeraldCard
import com.minshawi.quran1967.ui.theme.EmeraldDark
import com.minshawi.quran1967.ui.theme.EmeraldSurface
import com.minshawi.quran1967.ui.theme.GoldAccent
import com.minshawi.quran1967.ui.theme.GoldLight
import com.minshawi.quran1967.ui.theme.TextLight
import com.minshawi.quran1967.ui.theme.TextSecondary

private val ItemShape = RoundedCornerShape(14.dp)
private val BadgeShape = RoundedCornerShape(8.dp)
private val SelectedBorderColor = Color(0x80D4AF37)
private val SelectedBadgeBg = Color(0x33D4AF37)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current

    // Audio & Azan States (Playback position is isolated into MiniPlayerBar)
    val currentSurah by AudioPlaybackManager.currentSurah.collectAsState()
    val isPlaying by AudioPlaybackManager.isPlaying.collectAsState()
    val isLoading by AudioPlaybackManager.isLoading.collectAsState()
    val isAzanActive by AudioPlaybackManager.isAzanActive.collectAsState()
    val activeAzanPrayerName by AudioPlaybackManager.activeAzanPrayerName.collectAsState()

    // Location & Prayer Time State
    var selectedLocation by remember { mutableStateOf(CityLocation.EGYPT_CAIRO) }
    var prayerScheduleKey by remember { mutableStateOf(0) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showFullPlayerSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Dynamic prayer schedule recalculated on location change or when a prayer passes
    val prayerSchedule = remember(selectedLocation, prayerScheduleKey) {
        PrayerCalculator.calculateTodayPrayers(selectedLocation)
    }

    val filteredSurahs = remember(searchQuery) {
        QuranRepository.searchSurahs(searchQuery)
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val screenBgBrush = remember {
        Brush.verticalGradient(
            colors = listOf(EmeraldDark, BackgroundDark)
        )
    }

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = {
            // Sticky Mini Player at bottom (observes position locally without recomposing list)
            if (currentSurah != null) {
                MiniPlayerBar(
                    surah = currentSurah!!,
                    isPlaying = isPlaying,
                    isLoading = isLoading,
                    onPlayPauseClicked = { AudioPlaybackManager.togglePlayPause() },
                    onBarClicked = { showFullPlayerSheet = true }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(screenBgBrush)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "مصحف المنشاوي 1967",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "الختمة المرتلة النقية النادرة مع الأذان الذكي",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }

                IconButton(
                    onClick = { showSettingsDialog = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldSurface)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = "الإعدادات والدولة",
                        tint = GoldAccent
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 1. Prayer Times Banner Card with Real-time 1s Ticker
                item(key = "prayer_card", contentType = "header") {
                    PrayerCard(
                        schedule = prayerSchedule,
                        onSelectLocationClicked = { showSettingsDialog = true },
                        onRefreshSchedule = { prayerScheduleKey++ },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // 2. Search Bar
                item(key = "search_bar", contentType = "header") {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        placeholder = {
                            Text("ابحث باسم السورة أو رقمها (مثال: الكهف، 18)...", color = TextSecondary)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = GoldAccent)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = EmeraldSurface,
                            unfocusedContainerColor = EmeraldSurface,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                }

                // 3. Section Title & 1967 Tag
                item(key = "section_title", contentType = "header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سور القرآن الكريم (${filteredSurahs.size})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = GoldLight,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "تسجيلات إذاعة دمشق والقاهرة 1967",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }

                // 4. Surahs List Items (Optimized with key and contentType for smooth 60/120fps scrolling)
                items(
                    items = filteredSurahs,
                    key = { it.number },
                    contentType = { "surah_item" }
                ) { surah ->
                    val isCurrent = currentSurah?.number == surah.number

                    SurahListItem(
                        surah = surah,
                        isCurrentlyPlaying = isCurrent && isPlaying,
                        isSelected = isCurrent,
                        onPlayClicked = {
                            if (isCurrent) {
                                AudioPlaybackManager.togglePlayPause()
                            } else {
                                AudioPlaybackManager.playSurah(surah)
                            }
                        },
                        onItemClicked = {
                            AudioPlaybackManager.playSurah(surah)
                            showFullPlayerSheet = true
                        },
                        onDownloadClicked = {
                            DownloadHelper.downloadSurah(context, surah)
                        }
                    )
                }

                // Bottom spacer so list doesn't get covered by mini-player
                item(key = "bottom_spacer", contentType = "spacer") {
                    Spacer(modifier = Modifier.height(88.dp))
                }
            }
        }
    }

    // Modal: Full Player Bottom Sheet
    if (showFullPlayerSheet && currentSurah != null) {
        ModalBottomSheet(
            onDismissRequest = { showFullPlayerSheet = false },
            sheetState = sheetState,
            containerColor = EmeraldDark
        ) {
            PlayerBottomSheet(
                surah = currentSurah!!,
                isPlaying = isPlaying,
                isLoading = isLoading,
                onPlayPauseClicked = { AudioPlaybackManager.togglePlayPause() },
                onSeekTo = { AudioPlaybackManager.seekTo(it) },
                onNextClicked = { AudioPlaybackManager.playNext() },
                onPrevClicked = { AudioPlaybackManager.playPrevious() },
                onRepeatClicked = { AudioPlaybackManager.cycleRepeatMode() },
                onDownloadClicked = {
                    DownloadHelper.downloadSurah(context, currentSurah!!)
                }
            )
        }
    }

    // Modal: Settings & Location Picker
    if (showSettingsDialog) {
        SettingsDialog(
            currentLocation = selectedLocation,
            onLocationSelected = { newLoc ->
                selectedLocation = newLoc
                prayerScheduleKey++
                AzanScheduler.scheduleAllPrayers(context, newLoc)
            },
            onDismiss = { showSettingsDialog = false },
            onSimulateAzanClicked = {
                AzanScheduler.triggerTestAzan(context, prayerSchedule.currentPrayerName)
            }
        )
    }

    // Modal: Active Azan Overlay (Interruption banner)
    if (isAzanActive) {
        AzanOverlayDialog(
            prayerName = activeAzanPrayerName ?: "الصلاة",
            onStopAzanAndResume = {
                AudioPlaybackManager.stopAzanAndResumeQuran()
            }
        )
    }
}

@Composable
fun SurahListItem(
    surah: Surah,
    isCurrentlyPlaying: Boolean,
    isSelected: Boolean,
    onPlayClicked: () -> Unit,
    onItemClicked: () -> Unit,
    onDownloadClicked: () -> Unit
) {
    val itemModifier = if (isSelected) {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(ItemShape)
            .background(EmeraldCard)
            .border(1.dp, SelectedBorderColor, ItemShape)
            .clickable(onClick = onItemClicked)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(ItemShape)
            .background(EmeraldSurface)
            .clickable(onClick = onItemClicked)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    }

    val badgeModifier = if (isSelected) {
        Modifier
            .size(36.dp)
            .clip(BadgeShape)
            .background(SelectedBadgeBg)
            .border(1.dp, GoldAccent, BadgeShape)
    } else {
        Modifier
            .size(36.dp)
            .clip(BadgeShape)
            .background(EmeraldDark)
            .border(1.dp, CardBorder, BadgeShape)
    }

    Row(
        modifier = itemModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            // Surah Number inside Islamic Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = badgeModifier
            ) {
                Text(
                    text = surah.number.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) GoldAccent else TextLight
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Surah Name and Metadata
            Column {
                Text(
                    text = "سورة ${surah.arabicName}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) GoldLight else TextLight
                    )
                )
                Text(
                    text = "${surah.englishName} • ${if (surah.isMakki) "مكية" else "مدنية"} (${surah.ayahCount} آية)",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Action Download Icon
            IconButton(
                onClick = onDownloadClicked,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(EmeraldDark.copy(alpha = 0.7f))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = "تنزيل سورة ${surah.arabicName}",
                    tint = GoldLight.copy(alpha = 0.85f),
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Action Play / Pause Icon
            IconButton(
                onClick = onPlayClicked,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) GoldAccent else EmeraldDark)
            ) {
                Icon(
                    painter = painterResource(
                        if (isCurrentlyPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
                    ),
                    contentDescription = null,
                    tint = if (isSelected) EmeraldDark else GoldAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun MiniPlayerBar(
    surah: Surah,
    isPlaying: Boolean,
    isLoading: Boolean,
    onPlayPauseClicked: () -> Unit,
    onBarClicked: () -> Unit
) {
    val currentPosition by AudioPlaybackManager.currentPosition.collectAsState()
    val duration by AudioPlaybackManager.duration.collectAsState()
    val progress = if (duration > 0) currentPosition.toFloat() / duration else 0f

    val barBrush = remember {
        Brush.verticalGradient(
            listOf(EmeraldCard, EmeraldDark)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(barBrush)
            .border(1.dp, CardBorder, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .clickable(onClick = onBarClicked)
    ) {
        // Thin Progress Indicator
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = GoldAccent,
            trackColor = EmeraldSurface
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Dynamic Sound Wave Equalizer (علامة الترددات الصوتية الحية المتحركة)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GoldAccent.copy(alpha = 0.15f))
                ) {
                    AnimatedEqualizer(
                        isPlaying = isPlaying,
                        barColor = GoldAccent,
                        barWidth = 2.8.dp,
                        modifier = Modifier.size(24.dp, 18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "سورة ${surah.arabicName}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                    )
                    Text(
                        text = "المنشاوي (ختمة 1967)",
                        style = MaterialTheme.typography.labelSmall.copy(color = GoldLight)
                    )
                }
            }

            // Play / Pause button
            IconButton(
                onClick = onPlayPauseClicked,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(GoldAccent)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = EmeraldDark,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(
                            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
                        ),
                        contentDescription = null,
                        tint = EmeraldDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
