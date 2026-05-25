package com.schoolnav.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Grading
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.SportsSoccer
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
            GridItem("Attendance", Icons.Filled.CheckCircle, BrandGreen, Destination.Attendance),
            GridItem("Assignments", Icons.AutoMirrored.Filled.Assignment, BrandBlue, Destination.Assignments),
            GridItem("Syllabus", Icons.Filled.Description, BrandPurple, Destination.Syllabus),
        ),
    )

    val teacher = HomeSection(
        title = "Teacher",
        items = listOf(
            GridItem("Teacher List", Icons.Filled.School, BrandBlue, Destination.TeacherList),
            GridItem("Mark Entry", Icons.Filled.EditNote, BrandGreen, Destination.MarkEntry),
            GridItem("Attendance", Icons.Filled.HowToReg, BrandTeal, Destination.TeacherAttendance),
            GridItem("Lesson Plan", Icons.Filled.Description, BrandPurple, Destination.LessonPlan),
            GridItem("Leave Apply", Icons.Filled.EventBusy, BrandOrange, Destination.LeaveApply),
            GridItem("Salary", Icons.Filled.Payments, BrandPink, Destination.Salary),
        ),
    )

    val studentCorner = HomeSection(
        title = "Student Corner",
        items = listOf(
            GridItem("My Profile", Icons.Filled.AccountCircle, BrandBlue, Destination.MyProfile),
            GridItem("My Attendance", Icons.Filled.EventAvailable, BrandGreen, Destination.MyAttendance),
            GridItem("My Marks", Icons.AutoMirrored.Filled.Grading, BrandPurple, Destination.MyMarks),
            GridItem("Fees", Icons.Filled.Payments, BrandOrange, Destination.Fees),
            GridItem("ID Card", Icons.Filled.Badge, BrandPink, Destination.IdCard),
            GridItem("Library Card", Icons.Filled.CardMembership, BrandTeal, Destination.LibraryCard),
        ),
    )

    val communication = HomeSection(
        title = "Communication",
        items = listOf(
            GridItem("Messages", Icons.AutoMirrored.Filled.Message, BrandBlue, Destination.Messages),
            GridItem("Announcements", Icons.AutoMirrored.Filled.Announcement, BrandOrange, Destination.Announcements),
            GridItem("Helpline", Icons.Filled.Phone, BrandGreen, Destination.Helpline),
            GridItem("SMS Alerts", Icons.Filled.Sms, BrandPink, Destination.SmsAlerts),
            GridItem("Email", Icons.Filled.Email, BrandTeal, Destination.Email),
            GridItem("Live Chat", Icons.AutoMirrored.Filled.Chat, BrandPurple, Destination.LiveChat),
        ),
    )

    val resources = HomeSection(
        title = "Resources",
        items = listOf(
            GridItem("Library", Icons.AutoMirrored.Filled.LibraryBooks, BrandBlue, Destination.Library),
            GridItem("Sports", Icons.Filled.SportsSoccer, BrandOrange, Destination.Sports),
            GridItem("Transport", Icons.Filled.DirectionsBus, BrandGreen, Destination.Transport),
            GridItem("Hostel", Icons.Filled.Apartment, BrandPurple, Destination.Hostel),
            GridItem("Science Lab", Icons.Filled.Science, BrandTeal, Destination.ScienceLab),
            GridItem("Computer Lab", Icons.Filled.Computer, BrandPink, Destination.ComputerLab),
        ),
    )

    val important = HomeSection(
        title = "Important",
        items = listOf(
            GridItem("Notice", Icons.Filled.Campaign, BrandOrange, Destination.NoticeBoard),
            GridItem("Events", Icons.Filled.Event, BrandPink, Destination.Events),
            GridItem("Contact", Icons.Filled.Phone, BrandTeal, Destination.Contact),
            GridItem("About", Icons.Filled.Info, BrandPurple, Destination.About),
            GridItem("Gallery", Icons.Filled.PhotoLibrary, BrandBlue, Destination.Gallery),
            GridItem("Calendar", Icons.Filled.CalendarToday, BrandGreen, Destination.Calendar),
            GridItem("Holidays", Icons.Filled.BeachAccess, BrandOrange, Destination.Holidays),
            GridItem("Map", Icons.Filled.Map, BrandTeal, Destination.Map),
        ),
    )

    val allSections: List<HomeSection> = listOf(
        academic,
        teacher,
        studentCorner,
        communication,
        resources,
        important,
    )

}
