package com.minshawi.quran1967.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.minshawi.quran1967.util.DownloadProgress
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

    // Download States Tracking
    val downloadStates by DownloadHelper.downloadStates.collectAsState()

    LaunchedEffect(Unit) {
        DownloadHelper.checkInitialDownloadedState(context, QuranRepository.surahs)
    }

    // Location & Prayer Time State
    var selectedLocation by remember { mutableStateOf(CityLocation.EGYPT_CAIRO) }
    var prayerScheduleKey by remember { mutableStateOf(0) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showFullPlayerSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCompletedSurah by remember { mutableStateOf<Surah?>(null) }

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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp)
            ) {
                // 1. Top Header Banner: Daily Azan Times & Next Prayer Countdown
                item(key = "header_prayer_card", contentType = "header") {
                    PrayerCard(
                        schedule = prayerSchedule,
                        onSelectLocationClicked = { showSettingsDialog = true },
                        onRefreshSchedule = { prayerScheduleKey++ }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 2. Search Box for 114 Surahs
                item(key = "search_box", contentType = "search") {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = {
                            Text(
                                text = "ابحث برقم أو اسم السورة (مثل: الكهف أو 18)...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = GoldAccent
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = EmeraldDark,
                            unfocusedContainerColor = EmeraldDark,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 3. Section Title with Count
                item(key = "section_title", contentType = "title") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سور القرآن الكريم (${filteredSurahs.size})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
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
                        downloadState = downloadStates[surah.number],
                        onPlayClicked = {
                            if (isCurrent) {
                                AudioPlaybackManager.togglePlayPause()
                            } else {
                                AudioPlaybackManager.playSurah(surah, 0L)
                            }
                        },
                        onDownloadClicked = {
                            DownloadHelper.downloadSurah(context, surah)
                        },
                        onCancelDownloadClicked = {
                            DownloadHelper.cancelDownload(surah.number, surah.arabicName, context)
                        },
                        onCompletedOptionsClicked = {
                            selectedCompletedSurah = surah
                        },
                        onItemClicked = {
                            if (isCurrent && isPlaying) {
                                showFullPlayerSheet = true
                            } else if (isCurrent && !isPlaying) {
                                AudioPlaybackManager.togglePlayPause()
                                showFullPlayerSheet = true
                            } else {
                                AudioPlaybackManager.playSurah(surah, 0L)
                                showFullPlayerSheet = true
                            }
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

    // Modal: Completed Surah Options (Play Offline / File Info / Delete)
    if (selectedCompletedSurah != null) {
        val completedSurah = selectedCompletedSurah!!
        val file = DownloadHelper.getLocalSurahFile(context, completedSurah)
        val sizeMb = if (file.exists()) {
            String.format(java.util.Locale.US, "%.1f MB", file.length() / (1024f * 1024f))
        } else {
            "~18.5 MB"
        }

        DownloadedSurahDialog(
            surah = completedSurah,
            fileSizeMb = sizeMb,
            onPlayOffline = {
                AudioPlaybackManager.playSurah(completedSurah, 0L)
                showFullPlayerSheet = true
            },
            onDeleteSurah = {
                DownloadHelper.deleteDownloadedSurah(context, completedSurah)
            },
            onDismiss = {
                selectedCompletedSurah = null
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SurahListItem(
    surah: Surah,
    isCurrentlyPlaying: Boolean,
    isSelected: Boolean,
    downloadState: DownloadProgress?,
    onPlayClicked: () -> Unit,
    onDownloadClicked: () -> Unit,
    onCancelDownloadClicked: () -> Unit,
    onCompletedOptionsClicked: () -> Unit,
    onItemClicked: () -> Unit
) {
    val context = LocalContext.current
    val isDownloading = downloadState?.isDownloading == true
    val isPaused = downloadState?.isPaused == true
    val isCompleted = downloadState?.isCompleted == true || DownloadHelper.isSurahDownloaded(context, surah)

    val itemModifier = if (isSelected) {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(ItemShape)
            .background(EmeraldCard)
            .border(1.dp, SelectedBorderColor, ItemShape)
            .clickable(onClick = onItemClicked)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(ItemShape)
            .background(EmeraldSurface)
            .clickable(onClick = onItemClicked)
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

    Column(
        modifier = itemModifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
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
                    if (isCompleted) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "محفوظة بدون إنترنت ✓",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GoldAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    } else if (isPaused && downloadState != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "متوقف مؤقتاً (${downloadState.percentage}%) • اضغط للاستئناف",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GoldAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    } else if (isDownloading && downloadState != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = downloadState.downloadedFormatted,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GoldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Action Buttons: Download / Progress & Play / Pause
            // (Maintaining strict 14.dp spacing between the two buttons)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Download / Progress Column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isDownloading && downloadState != null) {
                        // Downloading Active: Click to Pause, Long-Click to Cancel
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark)
                                .border(1.dp, GoldAccent.copy(alpha = 0.5f), CircleShape)
                                .combinedClickable(
                                    onClick = onDownloadClicked,
                                    onLongClick = onCancelDownloadClicked
                                )
                        ) {
                            CircularProgressIndicator(
                                progress = { downloadState.progress },
                                modifier = Modifier.size(38.dp),
                                color = GoldAccent,
                                strokeWidth = 2.5.dp,
                                trackColor = EmeraldCard
                            )
                            Text(
                                text = "${downloadState.percentage}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            )
                        }

                        // Real-time downloading rate measurement (e.g. 2.4 MB/s)
                        if (downloadState.speedFormatted.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = downloadState.speedFormatted,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = GoldAccent
                                ),
                                maxLines = 1
                            )
                        }
                    } else if (isPaused && downloadState != null) {
                        // Paused State: Click to Resume, Long-Click to Cancel
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark)
                                .border(1.2.dp, GoldAccent, CircleShape)
                                .combinedClickable(
                                    onClick = onDownloadClicked,
                                    onLongClick = onCancelDownloadClicked
                                )
                        ) {
                            CircularProgressIndicator(
                                progress = { downloadState.progress },
                                modifier = Modifier.size(38.dp),
                                color = GoldAccent.copy(alpha = 0.6f),
                                strokeWidth = 2.5.dp,
                                trackColor = EmeraldCard
                            )
                            Icon(
                                painter = painterResource(R.drawable.ic_play_arrow),
                                contentDescription = "استئناف التنزيل",
                                tint = GoldAccent,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "متوقف مؤقتاً",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldLight
                            ),
                            maxLines = 1
                        )
                    } else if (isCompleted) {
                        // Downloaded Offline State: Click to open full actions dialog (Play / Info / Delete)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.15f))
                                .border(1.2.dp, GoldAccent, CircleShape)
                                .clickable { onCompletedOptionsClicked() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_circle),
                                contentDescription = "خيارات السورة المحفوظة",
                                tint = GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    } else {
                        // Normal Download Ready Button
                        IconButton(
                            onClick = onDownloadClicked,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark)
                                .border(1.dp, GoldAccent.copy(alpha = 0.35f), CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_download),
                                contentDescription = "تحميل سورة ${surah.arabicName}",
                                tint = GoldAccent,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }

                // Action Play / Pause Icon
                IconButton(
                    onClick = onPlayClicked,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) GoldAccent else EmeraldDark)
                ) {
                    Icon(
                        painter = painterResource(
                            if (isCurrentlyPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
                        ),
                        contentDescription = if (isCurrentlyPlaying) "إيقاف مؤقت" else "تشغيل",
                        tint = if (isSelected) EmeraldDark else GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Glowing progress bar along bottom of card while downloading or paused
        if (isDownloading && downloadState != null) {
            LinearProgressIndicator(
                progress = { downloadState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = GoldAccent,
                trackColor = EmeraldDark
            )
        } else if (isPaused && downloadState != null) {
            LinearProgressIndicator(
                progress = { downloadState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = GoldAccent.copy(alpha = 0.45f),
                trackColor = EmeraldDark
            )
        }
    }
}

@Composable
fun DownloadedSurahDialog(
    surah: Surah,
    fileSizeMb: String,
    onPlayOffline: () -> Unit,
    onDeleteSurah: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldCard),
            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(GoldAccent.copy(alpha = 0.15f))
                        .border(1.5.dp, GoldAccent, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check_circle),
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "سورة ${surah.arabicName}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                )

                Text(
                    text = "محفوظة على جهازك وتعمل بدون إنترنت",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info Box (Size & Format)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldDark),
                    border = BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("حجم الملف:", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text(fileSizeMb, style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("جودة التسجيل:", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text("ختمة 1967 النقية الأصلية", style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action 1: Play Surah Offline
                Button(
                    onClick = {
                        onPlayOffline()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_play_arrow),
                        contentDescription = null,
                        tint = EmeraldDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تشغيل السورة أوفلاين",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 2: Delete Surah from device
                OutlinedButton(
                    onClick = {
                        onDeleteSurah()
                        onDismiss()
                    },
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = "حذف السورة لتوفير المساحة",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action 3: Cancel / Close
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "إغلاق",
                        style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                    )
                }
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
