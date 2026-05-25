package com.schoolnav.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.schoolnav.app.ui.navigation.Destination
import com.schoolnav.app.ui.theme.BrandBlue
import com.schoolnav.app.ui.theme.BrandGreen
import com.schoolnav.app.ui.theme.BrandOrange

/**
 * Profile for a school facility that doesn't have a dedicated Ramom Smart
 * School backend page (sports, science lab, computer lab, …). Each entry is
 * a small editorial card — hero photo, tagline, two-column quick-facts grid,
 * a bullet list of activities or equipment, and a CTA to the school's gallery.
 *
 * Tenant operators can edit these in [FacilityProfiles] without touching any
 * navigation code — the URL inventory stays multi-tenant.
 */
data class FacilityProfile(
    val title: String,
    val tagline: String,
    val heroImageUrl: String,
    val heroIcon: ImageVector,
    val accent: Color,
    val facts: List<FacilityFact>,
    val highlights: List<String>,
)

data class FacilityFact(val icon: ImageVector, val label: String, val value: String)

/**
 * Built-in facility content used when a [Destination] has no Ramom URL.
 * Editing this list is the only thing needed to keep facility screens
 * accurate as the school adds equipment or activities.
 */
object FacilityProfiles {
    val sports = FacilityProfile(
        title = "Sports",
        tagline = "Football, cricket, athletics and indoor games — every day, all ages.",
        heroImageUrl = "https://images.unsplash.com/photo-1521412644187-c49fa049e84d?w=1200",
        heroIcon = Icons.Filled.SportsSoccer,
        accent = BrandOrange,
        facts = listOf(
            FacilityFact(Icons.Filled.Schedule, "Practice hours", "Mon–Fri · 3:30–5:00 PM"),
            FacilityFact(Icons.Filled.Groups, "Teams", "Cricket, football, badminton, athletics"),
            FacilityFact(Icons.Filled.CalendarMonth, "Annual events", "Sports Day · Inter-class League"),
        ),
        highlights = listOf(
            "Outdoor football & cricket field with floodlights",
            "Covered badminton & table-tennis courts",
            "Athletics tracks (100 m, 200 m, long-jump pit)",
            "Indoor chess and carrom club, all year",
            "Professional coaches for cricket and football",
            "Inter-school tournaments hosted every term",
        ),
    )

    val scienceLab = FacilityProfile(
        title = "Science Lab",
        tagline = "Fully equipped physics, chemistry and biology labs with safety-first practice.",
        heroImageUrl = "https://images.unsplash.com/photo-1532187863486-abf9dbad1b69?w=1200",
        heroIcon = Icons.Filled.Science,
        accent = BrandGreen,
        facts = listOf(
            FacilityFact(Icons.Filled.Schedule, "Open hours", "Class hours + Sat practice 9–11 AM"),
            FacilityFact(Icons.Filled.Groups, "Capacity", "30 students per session"),
            FacilityFact(Icons.Filled.CalendarMonth, "Showcase", "Science Fair every February"),
        ),
        highlights = listOf(
            "Physics lab — optics bench, force & motion kits, oscilloscopes",
            "Chemistry lab — fume hood, periodic-table wall, full reagent stock",
            "Biology lab — microscopes, preserved specimens, dissection trays",
            "Safety: goggles, lab coats, gloves and eyewash for every station",
            "First-aid kit and fire blanket at every entry point",
            "Hands-on practicals from grade 6 upward",
        ),
    )

    val computerLab = FacilityProfile(
        title = "Computer Lab",
        tagline = "Modern PCs, fast Wi-Fi, and a programming curriculum from grade 4.",
        heroImageUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=1200",
        heroIcon = Icons.Filled.Computer,
        accent = BrandBlue,
        facts = listOf(
            FacilityFact(Icons.Filled.Schedule, "Open hours", "Class hours + Wed/Fri 4–5 PM"),
            FacilityFact(Icons.Filled.Groups, "Workstations", "40 PCs · 1 student per machine"),
            FacilityFact(Icons.Filled.CalendarMonth, "Programs", "Coding Club · Robotics Club"),
        ),
        highlights = listOf(
            "40 dual-core PCs with up-to-date OS and developer tools",
            "Gigabit campus Wi-Fi with filtered student access",
            "Curriculum: Scratch (4–6), Python (7–9), web dev (10+)",
            "After-school coding club and robotics club",
            "Annual Hackathon and IT Olympiad",
            "Smart-board for live coding demos and projection",
        ),
    )

    fun forDestination(destination: Destination): FacilityProfile? = when (destination) {
        Destination.Sports -> sports
        Destination.ScienceLab -> scienceLab
        Destination.ComputerLab -> computerLab
        else -> null
    }
}

/**
 * Renders a [FacilityProfile] as a scrollable native page. The "Open gallery"
 * CTA at the bottom routes the user to the school's public Gallery WebView,
 * which is where most tenants house their facility photos.
 */
@Composable
fun FacilityInfoScreen(
    profile: FacilityProfile,
    onOpenGallery: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { Hero(profile) }
        item { FactsRow(profile) }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Highlights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                profile.highlights.forEach { line ->
                    HighlightRow(line, profile.accent)
                }
            }
        }
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Button(
                    onClick = onOpenGallery,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = profile.accent),
                ) {
                    Text(
                        text = "See photos in school gallery",
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun Hero(profile: FacilityProfile) {
    Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
        AsyncImage(
            model = profile.heroImageUrl,
            contentDescription = profile.title,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.0f),
                            Color.Black.copy(alpha = 0.55f),
                        ),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(profile.accent.copy(alpha = 0.95f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = profile.heroIcon,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = profile.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                text = profile.tagline,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
            )
        }
    }
}

@Composable
private fun FactsRow(profile: FacilityProfile) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        profile.facts.forEach { fact ->
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = fact.icon,
                        contentDescription = null,
                        tint = profile.accent,
                    )
                    Text(
                        text = fact.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = fact.value,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun HighlightRow(line: String, accent: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp, end = 12.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(accent),
        )
        Text(
            text = line,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
