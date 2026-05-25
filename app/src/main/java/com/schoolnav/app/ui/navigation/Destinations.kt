package com.schoolnav.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Every navigable screen in the app. Keep route strings stable — they are referenced
 * from grid buttons on the home screen, deep links, the navigation drawer, and the
 * FCM payload handler.
 */
enum class Destination(val route: String, val title: String) {
    Home(route = "home", title = "Home"),

    // Academic
    Result(route = "academic/result", title = "Result"),
    StudentList(route = "academic/student_list", title = "Student List"),
    ClassDetails(route = "academic/class_details", title = "Class Details"),
    LatestActivity(route = "academic/latest_activity", title = "Latest Activity"),
    ClassRoutine(route = "academic/class_routine", title = "Class Routine"),
    ExamRoutine(route = "academic/exam_routine", title = "Exam Routine"),
    Attendance(route = "academic/attendance", title = "Attendance"),
    Assignments(route = "academic/assignments", title = "Assignments"),
    Syllabus(route = "academic/syllabus", title = "Syllabus"),

    // Teacher
    TeacherList(route = "teacher/list", title = "Teacher List"),
    MarkEntry(route = "teacher/mark_entry", title = "Mark Entry"),
    TeacherAttendance(route = "teacher/attendance", title = "Teacher Attendance"),
    LessonPlan(route = "teacher/lesson_plan", title = "Lesson Plan"),
    LeaveApply(route = "teacher/leave_apply", title = "Leave Apply"),
    Salary(route = "teacher/salary", title = "Salary"),

    // Communication
    Messages(route = "communication/messages", title = "Messages"),
    Announcements(route = "communication/announcements", title = "Announcements"),
    Helpline(route = "communication/helpline", title = "Helpline"),
    SmsAlerts(route = "communication/sms_alerts", title = "SMS Alerts"),
    Email(route = "communication/email", title = "Email"),
    LiveChat(route = "communication/live_chat", title = "Live Chat"),

    // Resources
    Library(route = "resources/library", title = "Library"),
    Sports(route = "resources/sports", title = "Sports"),
    Transport(route = "resources/transport", title = "Transport"),
    Hostel(route = "resources/hostel", title = "Hostel"),
    ScienceLab(route = "resources/science_lab", title = "Science Lab"),
    ComputerLab(route = "resources/computer_lab", title = "Computer Lab"),

    // Important
    NoticeBoard(route = "important/notice", title = "Notice Board"),
    Events(route = "important/events", title = "Events"),
    Contact(route = "important/contact", title = "Contact"),
    About(route = "important/about", title = "About"),
    Gallery(route = "important/gallery", title = "Gallery"),
    Calendar(route = "important/calendar", title = "Calendar"),
    Holidays(route = "important/holidays", title = "Holidays"),
    Map(route = "important/map", title = "Map"),
    ;

    companion object {
        fun fromRoute(route: String?): Destination? = entries.firstOrNull { it.route == route }
    }
}

/** A top-level destination that appears in the bottom navigation bar. */
data class BottomTab(val destination: Destination, val icon: ImageVector)

val BottomTabs: List<BottomTab> = listOf(
    BottomTab(Destination.Home, Icons.Filled.Home),
    BottomTab(Destination.NoticeBoard, Icons.Filled.Campaign),
    BottomTab(Destination.Messages, Icons.AutoMirrored.Filled.Message),
    BottomTab(Destination.About, Icons.Filled.Info),
)

/** Routes that should display the bottom navigation bar. */
val rootRoutes: Set<String> = BottomTabs.map { it.destination.route }.toSet()
