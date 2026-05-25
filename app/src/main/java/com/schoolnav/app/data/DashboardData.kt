package com.schoolnav.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.schoolnav.app.ui.navigation.Destination
import com.schoolnav.app.ui.theme.BrandBlue
import com.schoolnav.app.ui.theme.BrandGreen
import com.schoolnav.app.ui.theme.BrandOrange
import com.schoolnav.app.ui.theme.BrandPurple

/** A single tile in the home-screen quick-stats row. */
data class QuickStat(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val tint: Color,
    val destination: Destination,
)

/** A teaser card in the "Recent Notices" horizontal strip on the home screen. */
data class NoticePreview(
    val title: String,
    val category: String,
    val date: String,
    val tint: Color,
)

object DashboardData {

    val quickStats: List<QuickStat> = listOf(
        QuickStat(
            label = "Classes\nToday",
            value = "6",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            tint = BrandBlue,
            destination = Destination.ClassRoutine,
        ),
        QuickStat(
            label = "Pending\nTasks",
            value = "3",
            icon = Icons.Filled.PendingActions,
            tint = BrandOrange,
            destination = Destination.Assignments,
        ),
        QuickStat(
            label = "Attendance",
            value = "92%",
            icon = Icons.Filled.CheckCircle,
            tint = BrandGreen,
            destination = Destination.MyAttendance,
        ),
        QuickStat(
            label = "New\nNotices",
            value = "4",
            icon = Icons.Filled.Notifications,
            tint = BrandPurple,
            destination = Destination.NoticeBoard,
        ),
    )

    val recentNotices: List<NoticePreview> = listOf(
        NoticePreview(
            title = "Mid-term exam routine published",
            category = "Exam",
            date = "Today",
            tint = BrandBlue,
        ),
        NoticePreview(
            title = "Parent–teacher meeting on Saturday",
            category = "Event",
            date = "Yesterday",
            tint = BrandPurple,
        ),
        NoticePreview(
            title = "Annual sports week schedule",
            category = "Sports",
            date = "2 days ago",
            tint = BrandOrange,
        ),
        NoticePreview(
            title = "School closed for national holiday",
            category = "Holiday",
            date = "3 days ago",
            tint = BrandGreen,
        ),
    )
}
