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

    // Extra public/private screens reachable via the navigation drawer
    // (handy on Ramom Smart School tenants that expose them).
    Principal(route = "school/principal", title = "Principal Message"),
    Chairperson(route = "school/chairperson", title = "Chairperson"),
    Admission(route = "school/admission", title = "Admission"),
    Certificates(route = "school/certificates", title = "Certificates"),
    Video(route = "school/video", title = "Video Gallery"),
    Homework(route = "user/homework", title = "Homework"),
    StudyMaterial(route = "user/study_material", title = "Study Material"),
    OnlineExam(route = "user/online_exam", title = "Online Exam"),
    Profile(route = "user/profile", title = "My Profile"),
    Dashboard(route = "user/dashboard", title = "Dashboard"),

    // Administration (admin / staff modules)
    FeesCollect(route = "admin/fees_collect", title = "Fees Collect"),
    FeesInvoice(route = "admin/fees_invoice", title = "Fees Invoice"),
    FeesDue(route = "admin/fees_due", title = "Fees Due"),
    FeesDiscount(route = "admin/fees_discount", title = "Fees Discount"),
    Income(route = "admin/income", title = "Income"),
    Expense(route = "admin/expense", title = "Expense"),
    Accounting(route = "admin/accounting", title = "Office Accounting"),
    ChartOfAccount(route = "admin/chart_of_account", title = "Chart of Accounts"),
    BankAccount(route = "admin/bank_account", title = "Bank Accounts"),
    Payroll(route = "admin/payroll", title = "Payroll"),
    AdvanceSalary(route = "admin/advance_salary", title = "Advance Salary"),
    BonusPayment(route = "admin/bonus", title = "Bonus Payment"),
    OnlineAdmission(route = "admin/online_admission", title = "Online Admission"),
    IdCard(route = "admin/id_card", title = "ID Card"),
    CertificateTemplate(route = "admin/certificate_template", title = "Certificate Template"),
    HumanResources(route = "admin/hr", title = "Human Resources"),
    Reports(route = "admin/report", title = "Reports"),
    Promotion(route = "admin/promotion", title = "Promotion"),
    QuestionBank(route = "admin/question_bank", title = "Question Bank"),
    Alumni(route = "admin/alumni", title = "Alumni"),
    Complaint(route = "admin/complaint", title = "Complaints"),
    VisitorLog(route = "admin/visitor", title = "Visitor Log"),
    PostalDispatch(route = "admin/postal_dispatch", title = "Postal Dispatch"),
    PostalReceive(route = "admin/postal_receive", title = "Postal Receive"),
    AdmissionEnquiry(route = "admin/enquiry", title = "Admission Enquiry"),

    // Auth flow
    Login(route = "auth/login", title = "Sign in"),
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
