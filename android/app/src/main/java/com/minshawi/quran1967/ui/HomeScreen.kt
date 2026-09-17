package com.minshawi.quran1967.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.res.painterResource
import com.minshawi.quran1967.R
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minshawi.quran1967.audio.AudioPlaybackManager
import com.minshawi.quran1967.data.QuranRepository
import com.minshawi.quran1967.data.Surah
import com.minshawi.quran1967.prayer.AzanScheduler
import com.minshawi.quran1967.prayer.CityLocation
import com.minshawi.quran1967.prayer.PrayerCalculator
import com.minshawi.quran1967.ui.components.AzanOverlayDialog
import com.minshawi.quran1967.ui.components.PlayerBottomSheet
import com.minshawi.quran1967.ui.components.PrayerCard
import com.minshawi.quran1967.ui.components.SettingsDialog
import com.minshawi.quran1967.ui.theme.AmberGlow
import com.minshawi.quran1967.ui.theme.BackgroundDark
import com.minshawi.quran1967.ui.theme.CardBorder
import com.minshawi.quran1967.ui.theme.EmeraldCard
import com.minshawi.quran1967.ui.theme.EmeraldDark
import com.minshawi.quran1967.ui.theme.EmeraldLight
import com.minshawi.quran1967.ui.theme.EmeraldSurface
import com.minshawi.quran1967.ui.theme.GoldAccent
import com.minshawi.quran1967.ui.theme.GoldLight
import com.minshawi.quran1967.ui.theme.TextLight
import com.minshawi.quran1967.ui.theme.TextMuted
import com.minshawi.quran1967.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current

    // Audio & Azan States from AudioPlaybackManager
    val currentSurah by AudioPlaybackManager.currentSurah.collectAsState()
    val isPlaying by AudioPlaybackManager.isPlaying.collectAsState()
    val isLoading by AudioPlaybackManager.isLoading.collectAsState()
    val currentPosition by AudioPlaybackManager.currentPosition.collectAsState()
    val duration by AudioPlaybackManager.duration.collectAsState()
    val playbackSpeed by AudioPlaybackManager.playbackSpeed.collectAsState()
    val repeatMode by AudioPlaybackManager.repeatMode.collectAsState()
    val isAzanActive by AudioPlaybackManager.isAzanActive.collectAsState()
    val activeAzanPrayerName by AudioPlaybackManager.activeAzanPrayerName.collectAsState()

    // Location & Prayer Time State
    var selectedLocation by remember { mutableStateOf(CityLocation.EGYPT_CAIRO) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showFullPlayerSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Dynamic prayer schedule
    val prayerSchedule = remember(selectedLocation) {
        PrayerCalculator.calculateTodayPrayers(selectedLocation)
    }

    val filteredSurahs = remember(searchQuery) {
        QuranRepository.searchSurahs(searchQuery)
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = {
            // Sticky Mini Player at bottom
            if (currentSurah != null) {
                MiniPlayerBar(
                    surah = currentSurah!!,
                    isPlaying = isPlaying,
                    isLoading = isLoading,
                    currentPosition = currentPosition,
                    duration = duration,
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
                .background(
                    Brush.verticalGradient(
                        colors = listOf(EmeraldDark, BackgroundDark)
                    )
                )
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
                        painter = painterResource(R.drawable.ic_tune),
                        contentDescription = "الإعدادات والدولة",
                        tint = GoldAccent
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 1. Prayer Times Banner Card
                item {
                    PrayerCard(
                        schedule = prayerSchedule,
                        onSelectLocationClicked = { showSettingsDialog = true },
                        onSimulateAzanClicked = {
                            // Test simulate Azan right now
                            AzanScheduler.triggerTestAzan(context, prayerSchedule.currentPrayerName)
                        },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // 2. Search Bar for Surahs
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        placeholder = {
                            Text("ابحث برقم أو اسم السورة (مثال: الكهف، 18)...", color = TextMuted)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = GoldAccent)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = EmeraldCard,
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
                item {
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

                // 4. Surahs List Items
                items(filteredSurahs, key = { it.number }) { surah ->
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
                        }
                    )
                }

                // Bottom spacer so list doesn't get covered by mini-player
                item {
                    Spacer(modifier = Modifier.height(80.dp))
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
                currentPosition = currentPosition,
                duration = duration,
                playbackSpeed = playbackSpeed,
                repeatMode = repeatMode,
                onPlayPauseClicked = { AudioPlaybackManager.togglePlayPause() },
                onSeekTo = { AudioPlaybackManager.seekTo(it) },
                onNextClicked = { AudioPlaybackManager.playNext() },
                onPrevClicked = { AudioPlaybackManager.playPrevious() },
                onSpeedChanged = { AudioPlaybackManager.setSpeed(it) },
                onRepeatClicked = { AudioPlaybackManager.cycleRepeatMode() }
            )
        }
    }

    // Modal: Settings & Location Picker
    if (showSettingsDialog) {
        SettingsDialog(
            currentLocation = selectedLocation,
            onLocationSelected = { newLoc ->
                selectedLocation = newLoc
                AzanScheduler.scheduleAllPrayers(context, newLoc)
            },
            onDismiss = { showSettingsDialog = false }
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
    onItemClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) EmeraldCard else EmeraldSurface)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) GoldAccent.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onItemClicked() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Surah Number inside Islamic Star Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) GoldAccent.copy(alpha = 0.2f) else EmeraldDark)
                    .border(1.dp, if (isSelected) GoldAccent else CardBorder, RoundedCornerShape(8.dp))
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

        // Action Play / Equalizer Icon
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

@Composable
fun MiniPlayerBar(
    surah: Surah,
    isPlaying: Boolean,
    isLoading: Boolean,
    currentPosition: Long,
    duration: Long,
    onPlayPauseClicked: () -> Unit,
    onBarClicked: () -> Unit
) {
    val progress = if (duration > 0) currentPosition.toFloat() / duration else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(EmeraldCard)
            .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .clickable { onBarClicked() }
    ) {
        // Thin Golden Progress Line at top of Mini Player
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = GoldAccent,
            trackColor = EmeraldDark
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
                // Wave/Icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GoldAccent.copy(alpha = 0.15f))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_graphic_eq),
                        contentDescription = null,
                        tint = AmberGlow,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "سورة ${surah.arabicName}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                    )
                    Text(
                        text = "ختمة 1967 النقية • الشيخ المنشاوي",
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
