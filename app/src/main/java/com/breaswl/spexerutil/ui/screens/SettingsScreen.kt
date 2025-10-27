package com.breaswl.spexerutil.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breaswl.spexerutil.ui.components.BackgroundParticles
import com.breaswl.spexerutil.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    selectedTheme: String,
    backgroundMusicEnabled: Boolean,
    onThemeChanged: (String) -> Unit,
    onBackgroundMusicChanged: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundDark, BackgroundMid, BackgroundDeep)
                )
            )
    ) {
        BackgroundParticles()
        
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 32.dp) // Add top padding for status bar
                .padding(bottom = 32.dp) // Add bottom padding for navigation
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Profile & Settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Profile avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(SpherePink1, SpherePink2)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(60.dp),
                    tint = TextWhite
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Breath Master",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Theme selection
            Text(
                text = "Theme",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ThemeOption("Pink", SpherePink1, SpherePink2, selectedTheme == "Pink") {
                    onThemeChanged("Pink")
                }
                ThemeOption("Blue", SphereBlue1, SphereBlue2, selectedTheme == "Blue") {
                    onThemeChanged("Blue")
                }
                ThemeOption("Green", SphereGreen1, SphereGreen2, selectedTheme == "Green") {
                    onThemeChanged("Green")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Background music
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(BackgroundMid.copy(alpha = 0.5f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Background Music",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                    Text(
                        text = "Calming sounds during practice",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }
                
                Switch(
                    checked = backgroundMusicEnabled,
                    onCheckedChange = onBackgroundMusicChanged,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AccentCyan,
                        checkedTrackColor = AccentCyan.copy(alpha = 0.5f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // View Statistics button
            Button(
                onClick = onNavigateToStatistics,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "View Statistics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://breatthspheere.com/privacy-policy.html"))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "Policy",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhite
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "BreathSphere v1.0",
                fontSize = 14.sp,
                color = TextGray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ThemeOption(
    name: String,
    color1: androidx.compose.ui.graphics.Color,
    color2: androidx.compose.ui.graphics.Color,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onSelect)
    ) {
        Box(
            modifier = Modifier
                .size(if (isSelected) 70.dp else 60.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(color1, color2)
                    )
                )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) TextWhite else TextGray
        )
    }
}

