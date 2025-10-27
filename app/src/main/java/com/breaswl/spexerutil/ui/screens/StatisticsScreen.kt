package com.breaswl.spexerutil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breaswl.spexerutil.data.BreathingSession
import com.breaswl.spexerutil.data.SessionRepository
import com.breaswl.spexerutil.ui.components.BackgroundParticles
import com.breaswl.spexerutil.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    sessionRepository: SessionRepository,
    onNavigateBack: () -> Unit
) {
    val sessions by sessionRepository.getSessions().collectAsState(initial = emptyList())
    
    // Calculate statistics
    val totalSessions = sessions.size
    val totalMinutes = sessions.sumOf { it.durationMinutes }
    val lastWeekSessions = sessions.filter { session ->
        val sessionDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(session.date)
        val weekAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.time
        sessionDate?.after(weekAgo) ?: false
    }
    
    // Get last 30 days for calendar
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val last30Days = (0..29).map { daysAgo ->
        calendar.add(Calendar.DAY_OF_YEAR, if (daysAgo == 0) 0 else -1)
        dateFormat.format(calendar.time) to calendar.get(Calendar.DAY_OF_MONTH)
    }.reversed()
    
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
                    text = "Your Practice",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your progress will appear here\nafter your first session",
                        fontSize = 18.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Statistics cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(
                        title = "Total Sessions",
                        value = "$totalSessions",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatCard(
                        title = "Total Time",
                        value = "$totalMinutes min",
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                StatCard(
                    title = "This Week",
                    value = "${lastWeekSessions.size} sessions, ${lastWeekSessions.sumOf { it.durationMinutes }} min",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Last 30 Days",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Calendar grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(last30Days.size) { index ->
                        val (date, day) = last30Days[index]
                        val daysSessions = sessions.filter { it.date == date }
                        val totalMinutesForDay = daysSessions.sumOf { it.durationMinutes }
                        
                        DayBubble(
                            day = day,
                            minutes = totalMinutesForDay
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(
                Brush.linearGradient(
                    colors = listOf(BackgroundMid.copy(alpha = 0.5f), BackgroundDeep.copy(alpha = 0.5f))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AccentCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                color = TextGray
            )
        }
    }
}

@Composable
fun DayBubble(
    day: Int,
    minutes: Int
) {
    val size = when {
        minutes == 0 -> 40.dp
        minutes < 10 -> 45.dp
        minutes < 20 -> 50.dp
        else -> 55.dp
    }
    
    val alpha = when {
        minutes == 0 -> 0.2f
        minutes < 10 -> 0.4f
        minutes < 20 -> 0.6f
        else -> 0.9f
    }
    
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                if (minutes > 0) {
                    Brush.radialGradient(
                        colors = listOf(
                            SpherePink1.copy(alpha = alpha),
                            SpherePink2.copy(alpha = alpha * 0.8f)
                        )
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(
                            BackgroundMid.copy(alpha = 0.3f),
                            BackgroundDeep.copy(alpha = 0.3f)
                        )
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$day",
            fontSize = 14.sp,
            fontWeight = if (minutes > 0) FontWeight.Bold else FontWeight.Normal,
            color = if (minutes > 0) TextWhite else TextGray
        )
    }
}

