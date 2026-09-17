package com.minshawi.quran1967.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.res.painterResource
import com.minshawi.quran1967.R
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.minshawi.quran1967.prayer.CityLocation
import com.minshawi.quran1967.ui.theme.AmberGlow
import com.minshawi.quran1967.ui.theme.EmeraldCard
import com.minshawi.quran1967.ui.theme.EmeraldDark
import com.minshawi.quran1967.ui.theme.EmeraldSurface
import com.minshawi.quran1967.ui.theme.GoldAccent
import com.minshawi.quran1967.ui.theme.GoldLight
import com.minshawi.quran1967.ui.theme.TextLight
import com.minshawi.quran1967.ui.theme.TextSecondary

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private data class AzanVoiceItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val isDefault: Boolean = false
)

@Composable
fun SettingsDialog(
    currentLocation: CityLocation,
    onLocationSelected: (CityLocation) -> Unit,
    onDismiss: () -> Unit,
    onSimulateAzanClicked: () -> Unit = {}
) {
    var selectedVoiceId by remember { mutableStateOf("minshawi") }

    val azanVoices = remember {
        listOf(
            AzanVoiceItem(
                id = "minshawi",
                title = "أذان الشيخ محمد صديق المنشاوي رحمه الله",
                subtitle = "الافتراضي - تسجيل إذاعي نقي ونادر",
                isDefault = true
            ),
            AzanVoiceItem(
                id = "makkah",
                title = "أذان الحرم المكي الشريف",
                subtitle = "تسجيل الحرم المكي بأداء خاشع",
                isDefault = false
            ),
            AzanVoiceItem(
                id = "madinah",
                title = "أذان الحرم النبوي الشريف (المدينة المنورة)",
                subtitle = "تسجيل المسجد النبوي الشريف بأداء مهيب",
                isDefault = false
            )
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = EmeraldCard,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(EmeraldSurface, EmeraldDark)
                        )
                    )
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إعدادات الأذان والمواقيت",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "الدولة والمدينة لحساب المواقيت فلكياً:",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = GoldLight,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Cities List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 140.dp)
                ) {
                    items(CityLocation.DEFAULT_CITIES) { city ->
                        val isSelected = (city.cityName == currentLocation.cityName && city.countryName == currentLocation.countryName)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) GoldAccent.copy(alpha = 0.18f) else EmeraldSurface
                                )
                                .border(
                                    width = if (isSelected) 1.dp else 0.dp,
                                    color = if (isSelected) GoldAccent else androidx.compose.ui.graphics.Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    onLocationSelected(city)
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_location_city),
                                    contentDescription = null,
                                    tint = if (isSelected) GoldAccent else TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = city.displayName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isSelected) GoldLight else TextLight,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                    Text(
                                        text = "طريقة الحساب: ${city.calculationMethod.name}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "محدد",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section: Choose Azan Voice
                Text(
                    text = "اختر الأذان الذي تريد:",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = GoldLight,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    azanVoices.forEach { voice ->
                        val isVoiceSelected = voice.id == selectedVoiceId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isVoiceSelected) GoldAccent.copy(alpha = 0.15f) else EmeraldSurface
                                )
                                .border(
                                    width = if (isVoiceSelected) 1.dp else 0.dp,
                                    color = if (isVoiceSelected) GoldAccent else androidx.compose.ui.graphics.Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedVoiceId = voice.id }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = voice.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isVoiceSelected) GoldLight else TextLight,
                                            fontWeight = if (isVoiceSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    )
                                    if (voice.isDefault) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = GoldAccent.copy(alpha = 0.2f),
                                            border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldAccent)
                                        ) {
                                            Text(
                                                text = "الافتراضي",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = GoldLight,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = voice.subtitle,
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                )
                            }
                            Icon(
                                imageVector = if (isVoiceSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isVoiceSelected) GoldAccent else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Test / Simulate Azan Option (نقل محاكاة الأذان لداخل الإعدادات)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldDark.copy(alpha = 0.65f))
                        .border(1.dp, GoldAccent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable {
                            onDismiss()
                            onSimulateAzanClicked()
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_notifications_active),
                            contentDescription = null,
                            tint = AmberGlow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "تجربة محاكاة الأذان المختار الآن",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            )
                            Text(
                                text = "اختبار ميزة إيقاف واستئناف التلاوة تلقائياً",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }
                    }
                    Icon(
                        painter = painterResource(R.drawable.ic_play_arrow),
                        contentDescription = "تشغيل المحاكاة",
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = EmeraldDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "حفظ وإغلاق",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    )
                }
            }
        }
    }
}
