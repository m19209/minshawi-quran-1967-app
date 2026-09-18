package com.minshawi.quran1967

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import com.minshawi.quran1967.audio.AudioPlaybackManager
import com.minshawi.quran1967.audio.QuranAudioService
import com.minshawi.quran1967.prayer.AzanScheduler
import com.minshawi.quran1967.prayer.CityLocation
import com.minshawi.quran1967.ui.HomeScreen
import com.minshawi.quran1967.ui.theme.MinshawiQuranTheme

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Refresh downloaded state in case newly granted permissions allow reading external storage
        DownloadHelper.checkInitialDownloadedState(applicationContext, QuranRepository.surahs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Audio System
        AudioPlaybackManager.initialize(applicationContext)

        // Start background media service
        val serviceIntent = Intent(this, QuranAudioService::class.java)
        startService(serviceIntent)

        // Schedule initial prayer alarms for default location (Cairo, Egypt)
        AzanScheduler.scheduleAllPrayers(this, CityLocation.EGYPT_CAIRO)

        // Request runtime permissions
        checkAndRequestPermissions()

        setContent {
            // Enforce Right-to-Left (RTL) for Arabic Islamic Interface
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MinshawiQuranTheme {
                    HomeScreen()
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
