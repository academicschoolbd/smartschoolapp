package com.schoolnav.app.ui.navigation

/**
 * Every navigable screen in the app. Keep route strings stable — they are referenced
 * from grid buttons on the home screen, deep links, and from the FCM payload handler.
 */
enum class Destination(val route: String, val title: String) {
    Home(route = "home", title = "School Nav"),

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
    About(route = "important/about", title = "About School"),
    ;

    companion object {
        fun fromRoute(route: String?): Destination? = entries.firstOrNull { it.route == route }
    }
}
