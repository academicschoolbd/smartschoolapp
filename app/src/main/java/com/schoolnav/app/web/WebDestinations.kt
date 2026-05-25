package com.schoolnav.app.web

import com.schoolnav.app.tenant.ActiveTenant
import com.schoolnav.app.ui.navigation.Destination

/**
 * How a [Destination] should be rendered when the user opens it.
 *
 * Every grid button on the home screen maps to exactly one of these. The
 * routing layer ([com.schoolnav.app.ui.navigation.SchoolNavGraph]) reads
 * this table and either:
 *  - opens [WebPage.Public] inside a [com.schoolnav.app.ui.screens.WebFeatureScreen]
 *    without any auth gate, or
 *  - opens [WebPage.Authenticated] inside a [WebFeatureScreen] gated on the
 *    user being logged in — unauthenticated users see a "Please log in"
 *    prompt that deep-links into the native login screen, and
 *  - falls back to a "coming soon" placeholder for any destination that
 *    does not exist on the Ramom Smart School backend
 *    (e.g. Sports / Science Lab / Computer Lab don't have a dedicated page).
 *
 * The URLs are derived from [ActiveTenant], so this table works without
 * modification for any Ramom Smart School tenant — just change the slug
 * and base URL in [com.schoolnav.app.tenant.TenantConfig].
 */
sealed class WebPage {
    /** Page on the **public** website at `/<slug>/<path>` — no login required. */
    data class Public(val path: String) : WebPage() {
        val url: String get() = ActiveTenant.publicUrl(path)
    }

    /** Page **inside** the admin/user portal at `/<path>` — login required. */
    data class Authenticated(val path: String) : WebPage() {
        val url: String get() = ActiveTenant.authedUrl(path)
    }

    /** No real backend page exists yet — render a placeholder. */
    data object Placeholder : WebPage()
}

/**
 * Convenience: the full URL for a [WebPage], or `null` for [WebPage.Placeholder].
 */
val WebPage.urlOrNull: String?
    get() = when (this) {
        is WebPage.Public -> url
        is WebPage.Authenticated -> url
        WebPage.Placeholder -> null
    }

val WebPage.requiresAuth: Boolean
    get() = this is WebPage.Authenticated

/**
 * Maps every [Destination] to its backend page.
 *
 * URL inventory was confirmed against `https://ngps.smartschool.bd` — see
 * `docs/TENANT_SETUP.md` for the full list of paths the Ramom platform
 * exposes and how to verify them for a new tenant.
 */
fun Destination.webPage(): WebPage = when (this) {
    Destination.Home -> WebPage.Placeholder

    // ── Academic ───────────────────────────────────────────────────────────
    // Results page is public so parents can look up grades without an account.
    Destination.Result -> WebPage.Public("exam_results")
    Destination.StudentList -> WebPage.Authenticated("student")
    Destination.ClassDetails -> WebPage.Authenticated("class")
    Destination.LatestActivity -> WebPage.Authenticated("dashboard")
    Destination.ClassRoutine -> WebPage.Authenticated("timetable")
    Destination.ExamRoutine -> WebPage.Authenticated("timetable_exam")
    Destination.Attendance -> WebPage.Authenticated("attendance")
    Destination.Assignments -> WebPage.Authenticated("assignment")
    Destination.Syllabus -> WebPage.Authenticated("syllabus")

    // ── Teacher ────────────────────────────────────────────────────────────
    // Teacher list is public on the school website (the "Our Teachers" page).
    Destination.TeacherList -> WebPage.Public("teachers")
    Destination.MarkEntry -> WebPage.Authenticated("mark")
    Destination.TeacherAttendance -> WebPage.Authenticated("attendance")
    Destination.LessonPlan -> WebPage.Authenticated("lesson_plan")
    Destination.LeaveApply -> WebPage.Authenticated("leave")
    Destination.Salary -> WebPage.Authenticated("salary")

    // ── Communication ──────────────────────────────────────────────────────
    Destination.Messages -> WebPage.Authenticated("message")
    Destination.Announcements -> WebPage.Authenticated("announcement")
    // No "helpline" page — point the user at the public Contact page which
    // has phone numbers, email and an embedded location map.
    Destination.Helpline -> WebPage.Public("contact")
    Destination.SmsAlerts -> WebPage.Authenticated("sms_module")
    Destination.Email -> WebPage.Authenticated("email")
    // Same channel as Messages on the Ramom backend (in-app inbox).
    Destination.LiveChat -> WebPage.Authenticated("message")

    // ── Resources ──────────────────────────────────────────────────────────
    Destination.Library -> WebPage.Authenticated("library")
    // Sports / labs are not standalone pages on the Ramom backend.
    // They get a clean placeholder so the button never opens a broken URL.
    Destination.Sports -> WebPage.Placeholder
    Destination.Transport -> WebPage.Authenticated("transport")
    Destination.Hostel -> WebPage.Authenticated("hostel")
    Destination.ScienceLab -> WebPage.Placeholder
    Destination.ComputerLab -> WebPage.Placeholder

    // ── Important ──────────────────────────────────────────────────────────
    // News / Notice page is public, no login needed.
    Destination.NoticeBoard -> WebPage.Public("news")
    Destination.Events -> WebPage.Authenticated("event_list")
    Destination.Contact -> WebPage.Public("contact")
    Destination.About -> WebPage.Public("about")
    Destination.Gallery -> WebPage.Public("gallery")
    Destination.Calendar -> WebPage.Authenticated("calendar")
    Destination.Holidays -> WebPage.Authenticated("holiday")
    // The school location is embedded inside the public Contact page.
    Destination.Map -> WebPage.Public("contact")

    // ── App-internal routes (no backend page) ──────────────────────────────
    Destination.Login -> WebPage.Placeholder
    Destination.Principal -> WebPage.Public("principal")
    Destination.Chairperson -> WebPage.Public("sovapoti")
    Destination.Admission -> WebPage.Public("admission")
    Destination.Certificates -> WebPage.Public("certificates")
    Destination.Video -> WebPage.Public("video")
    Destination.Homework -> WebPage.Authenticated("homework")
    Destination.StudyMaterial -> WebPage.Authenticated("study_material")
    Destination.OnlineExam -> WebPage.Authenticated("online_exam")
    Destination.Profile -> WebPage.Authenticated("profile")
    Destination.Dashboard -> WebPage.Authenticated("dashboard")

    // ── Administration ────────────────────────────────────────────────────
    // Every URL below was verified against ngps.smartschool.bd. They map to
    // the standard module landing pages on the Ramom Smart School backend,
    // so they work for any tenant without per-school config.
    Destination.FeesCollect -> WebPage.Authenticated("fees_collect")
    Destination.FeesInvoice -> WebPage.Authenticated("fees_invoice")
    Destination.FeesDue -> WebPage.Authenticated("fees_due")
    Destination.FeesDiscount -> WebPage.Authenticated("fees_discount")
    Destination.Income -> WebPage.Authenticated("income")
    Destination.Expense -> WebPage.Authenticated("expense")
    Destination.Accounting -> WebPage.Authenticated("office_accounting")
    Destination.ChartOfAccount -> WebPage.Authenticated("chart_of_account")
    Destination.BankAccount -> WebPage.Authenticated("bank_account")
    Destination.Payroll -> WebPage.Authenticated("payroll")
    Destination.AdvanceSalary -> WebPage.Authenticated("advance_salary")
    Destination.BonusPayment -> WebPage.Authenticated("bonus_payment")
    Destination.OnlineAdmission -> WebPage.Authenticated("online_admission")
    Destination.IdCard -> WebPage.Authenticated("id_card")
    Destination.CertificateTemplate -> WebPage.Authenticated("certificate")
    Destination.HumanResources -> WebPage.Authenticated("hr")
    Destination.Reports -> WebPage.Authenticated("report")
    Destination.Promotion -> WebPage.Authenticated("promotion")
    Destination.QuestionBank -> WebPage.Authenticated("question_bank")
    Destination.Alumni -> WebPage.Authenticated("alumni")
    Destination.Complaint -> WebPage.Authenticated("complaint")
    Destination.VisitorLog -> WebPage.Authenticated("visitor")
    Destination.PostalDispatch -> WebPage.Authenticated("postal_dispatch")
    Destination.PostalReceive -> WebPage.Authenticated("postal_receive")
    Destination.AdmissionEnquiry -> WebPage.Authenticated("enquiry")
}
