package com.schoolnav.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.schoolnav.app.ui.navigation.Destination
import com.schoolnav.app.ui.theme.BrandBlue
import com.schoolnav.app.ui.theme.BrandGreen
import com.schoolnav.app.ui.theme.BrandOrange
import com.schoolnav.app.ui.theme.BrandPink
import com.schoolnav.app.ui.theme.BrandPurple
import com.schoolnav.app.ui.theme.BrandTeal

data class GridItem(
    val label: String,
    val icon: ImageVector,
    val tint: Color,
    val destination: Destination,
)

data class HomeSection(
    val title: String,
    val items: List<GridItem>,
)

data class BannerItem(
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val accent: Color,
)

object HomeData {

    val bannerItems: List<BannerItem> = listOf(
        BannerItem(
            title = "Welcome to a new term",
            subtitle = "Stay on top of classes, results and notices",
            imageUrl = "https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=1200",
            accent = BrandBlue,
        ),
        BannerItem(
            title = "Mid-term results are out",
            subtitle = "Tap Result to view your grade card",
            imageUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=1200",
            accent = BrandGreen,
        ),
        BannerItem(
            title = "Annual sports week",
            subtitle = "See the events tab for the schedule",
            imageUrl = "https://images.unsplash.com/photo-1517649763962-0c623066013b?w=1200",
            accent = BrandOrange,
        ),
        BannerItem(
            title = "Parent–teacher meeting",
            subtitle = "Sat, 10:00 AM in the main auditorium",
            imageUrl = "https://images.unsplash.com/photo-1577896851231-70ef18881754?w=1200",
            accent = BrandPurple,
        ),
    )

    val academic = HomeSection(
        title = "Academic",
        items = listOf(
            GridItem("Result", Icons.Filled.Grade, BrandBlue, Destination.Result),
            GridItem("Student List", Icons.Filled.Groups, BrandGreen, Destination.StudentList),
            GridItem("Class Details", Icons.AutoMirrored.Filled.MenuBook, BrandPurple, Destination.ClassDetails),
            GridItem("Latest Activity", Icons.Filled.Timeline, BrandPink, Destination.LatestActivity),
            GridItem("Class Routine", Icons.Filled.CalendarMonth, BrandTeal, Destination.ClassRoutine),
            GridItem("Exam Routine", Icons.Filled.Timer, BrandOrange, Destination.ExamRoutine),
        ),
    )

    val teacher = HomeSection(
        title = "Teacher",
        items = listOf(
            GridItem("Teacher List", Icons.Filled.School, BrandBlue, Destination.TeacherList),
            GridItem("Mark Entry", Icons.Filled.EditNote, BrandGreen, Destination.MarkEntry),
        ),
    )

    val important = HomeSection(
        title = "Important",
        items = listOf(
            GridItem("Notice", Icons.Filled.Campaign, BrandOrange, Destination.NoticeBoard),
            GridItem("Events", Icons.Filled.Event, BrandPink, Destination.Events),
            GridItem("Contact", Icons.Filled.Phone, BrandTeal, Destination.Contact),
            GridItem("About", Icons.Filled.Info, BrandPurple, Destination.About),
        ),
    )

    val allSections: List<HomeSection> = listOf(academic, teacher, important)

}
