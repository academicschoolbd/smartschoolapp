package com.schoolnav.app.ui.navigation

import androidx.compose.material.icons.Icons
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

    // Teacher
    TeacherList(route = "teacher/list", title = "Teacher List"),
    MarkEntry(route = "teacher/mark_entry", title = "Mark Entry"),

    // Important
    NoticeBoard(route = "important/notice", title = "Notice Board"),
    Events(route = "important/events", title = "Events"),
    Contact(route = "important/contact", title = "Contact"),
    About(route = "important/about", title = "About"),
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
    BottomTab(Destination.About, Icons.Filled.Info),
)

/** Routes that should display the bottom navigation bar. */
val rootRoutes: Set<String> = BottomTabs.map { it.destination.route }.toSet()
