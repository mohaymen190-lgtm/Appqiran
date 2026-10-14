package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Top iOS status bar matching the user screenshot:
 * On the left: Battery (58%), Signal bars, Wi-Fi, Headphones icon
 * In the middle: Subtle notch / pill
 * On the right: WhatsApp icon, Time 8:03
 */
@Composable
fun IPhoneStatusBar(
    modifier: Modifier = Modifier,
    currentTime: String = "٨:٠٣",
    batteryPercent: String = "٥٨%"
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side indicators (battery, signal bars, wifi, headphones)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "$batteryPercent",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            // Signal Bars icon
            Icon(
                imageVector = Icons.Default.SignalCellularAlt,
                contentDescription = "Signal",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            // Wi-Fi icon
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "Wi-Fi",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            // Headphones icon (from screenshot)
            Icon(
                imageVector = Icons.Default.Headphones,
                contentDescription = "Headphones",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(13.dp)
            )
        }

        // Notch / Dynamic Island pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 54.dp, height = 8.dp)
                    .background(Color(0xFF0F1524), RoundedCornerShape(6.dp))
            )
        }

        // Right side indicators (Time 8:03)
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = currentTime,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Top Spotify Header exactly matching screenshot:
 * Left: Round dark button with white equalizer bars
 * Middle: "الرئيسية" in clean white bold Arabic
 * Right: "Premium" rounded dark pill button
 */
@Composable
fun QuranAppHeader(
    title: String = "الرئيسية",
    onEqualizerClick: () -> Unit = {},
    onPremiumClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Equalizer sound button in circular container (matching screenshot left side)
        Surface(
            shape = CircleShape,
            color = Color(0xFF1E2842).copy(alpha = 0.9f),
            modifier = Modifier
                .size(46.dp)
                .clickable { onEqualizerClick() }
                .testTag("header_sound_button")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.size(20.dp)
                ) {
                    Box(modifier = Modifier.size(width = 3.5.dp, height = 16.dp).background(Color.White, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(3.dp))
                    Box(modifier = Modifier.size(width = 3.5.dp, height = 10.dp).background(Color.White, RoundedCornerShape(2.dp)))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Large Arabic Title "الرئيسية"
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            ),
            color = Color.White
        )

        Spacer(modifier = Modifier.weight(1f))

        // "Premium" Dark Pill Button (matching screenshot right side)
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1B253D).copy(alpha = 0.95f),
            modifier = Modifier
                .clickable { onPremiumClick() }
                .testTag("header_premium_badge")
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Premium",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
