# Tenant setup — rebrand this APK for any Ramom Smart School site

This app is a thin native shell around a [Ramom Smart
School](https://codecanyon.net/item/ramom-school-multi-branch-school-management-system/24360267)
backend (the platform that powers `smartschool.bd` and its tenants like
`ngps.smartschool.bd`). Every grid button on the home screen opens a real
page on the chosen tenant inside a customized in-app browser:

- **Public pages** (News, Teachers, Gallery, Exam Results, Admit Card, About,
  Contact, Principal Message, …) open without login.
- **Authenticated pages** (Mark Entry, Salary, Leave Apply, Attendance, …)
  prompt the user to sign in via the native Login screen, then keep them
  signed in across screens by sharing the auth cookie with the WebView.

The whole app is **multi-tenant by configuration** — the same APK works on
any `*.smartschool.bd` deployment (or any Ramom Smart School host with a
similar URL structure). Pointing it at a new school is a one-file change.

## TL;DR — rebrand in 3 steps

1. **Edit `app/src/main/java/com/schoolnav/app/tenant/TenantConfig.kt`**:

   ```kotlin
   val ActiveTenant: TenantConfig = TenantConfig(
       displayName = "Your School Name",        // shown in drawer + app bar + login
       tagline     = "Public school dashboard", // small subtitle under the name
       baseUrl     = "https://YOURSCHOOL.smartschool.bd",
       frontendSlug = "yourschool",             // the slug in /<slug>/news, /<slug>/teachers, …
       brandColor  = Color(0xFF1A73E8),         // primary accent for the in-app chrome
       logoUrl     = null,                      // optional remote logo PNG
   )
   ```

2. **(Optional) Replace the launcher icon and app name**:
   - `app/src/main/res/values/strings.xml` → `app_name` and
     `default_notification_channel_id`.
   - `app/src/main/res/mipmap-*` → drop in your launcher icons (or run
     **File → New → Image Asset** in Android Studio).

3. **Build and ship**:
   ```bash
   ./gradlew assembleRelease
   ```

That's it — every screen now points at the new tenant.

## How to find the right `frontendSlug` for a tenant

Smart School publishes the public website at `https://host/<slug>/<page>`,
where `<slug>` is set per branch in the Ramom admin (System Settings →
General Settings → School Slug). The slug is usually shown in the URL of
the news / notice page. For example, on `ngps.smartschool.bd`:

```
https://ngps.smartschool.bd/ngps/news       ← slug = "ngps"
https://ngps.smartschool.bd/ngps/teachers
https://ngps.smartschool.bd/ngps/gallery
```

If you don't know the slug, open the tenant's homepage in a browser, click
the "Notice" or "Teachers" link from the public nav, and copy the first
URL segment after the host.

## The full URL inventory

Below is the mapping from each grid button to the backend URL it opens.
Auth-gated screens are marked **🔒** and require the user to sign in via
the in-app Login screen. Pages that don't exist on every tenant render a
clean "Coming soon" placeholder.

### Academic

| Button         | Backend page                                    | Auth |
| -------------- | ----------------------------------------------- | ---- |
| Result         | `<host>/<slug>/exam_results`                    |      |
| Student List   | `<host>/student`                                | 🔒   |
| Class Details  | `<host>/class`                                  | 🔒   |
| Latest Activity| `<host>/dashboard`                              | 🔒   |
| Class Routine  | `<host>/timetable`                              | 🔒   |
| Exam Routine   | `<host>/timetable_exam`                         | 🔒   |
| Attendance     | `<host>/attendance`                             | 🔒   |
| Assignments    | `<host>/assignment`                             | 🔒   |
| Syllabus       | `<host>/syllabus`                               | 🔒   |

### Teacher

| Button         | Backend page                                    | Auth |
| -------------- | ----------------------------------------------- | ---- |
| Teacher List   | `<host>/<slug>/teachers`                        |      |
| Mark Entry     | `<host>/mark`                                   | 🔒   |
| Attendance     | `<host>/attendance`                             | 🔒   |
| Lesson Plan    | `<host>/lesson_plan`                            | 🔒   |
| Leave Apply    | `<host>/leave`                                  | 🔒   |
| Salary         | `<host>/salary`                                 | 🔒   |

### Communication

| Button         | Backend page                                    | Auth |
| -------------- | ----------------------------------------------- | ---- |
| Messages       | `<host>/message`                                | 🔒   |
| Announcements  | `<host>/announcement`                           | 🔒   |
| Helpline       | `<host>/<slug>/contact`                         |      |
| SMS Alerts     | `<host>/sms_module`                             | 🔒   |
| Email          | `<host>/email`                                  | 🔒   |
| Live Chat      | `<host>/message`                                | 🔒   |

### Resources

| Button         | Backend page                                    | Auth |
| -------------- | ----------------------------------------------- | ---- |
| Library        | `<host>/library`                                | 🔒   |
| Transport      | `<host>/transport`                              | 🔒   |
| Hostel         | `<host>/hostel`                                 | 🔒   |
| Sports         | _Coming soon (no Ramom equivalent)_             | —    |
| Science Lab    | _Coming soon_                                   | —    |
| Computer Lab   | _Coming soon_                                   | —    |

### Important

| Button         | Backend page                                    | Auth |
| -------------- | ----------------------------------------------- | ---- |
| Notice         | `<host>/<slug>/news`                            |      |
| Events         | `<host>/event_list`                             | 🔒   |
| Contact        | `<host>/<slug>/contact`                         |      |
| About          | `<host>/<slug>/about`                           |      |
| Gallery        | `<host>/<slug>/gallery`                         |      |
| Calendar       | `<host>/calendar`                               | 🔒   |
| Holidays       | `<host>/holiday`                                | 🔒   |
| Map            | `<host>/<slug>/contact` (embedded map widget)   |      |

The full mapping table lives in
[`app/src/main/java/com/schoolnav/app/web/WebDestinations.kt`](../app/src/main/java/com/schoolnav/app/web/WebDestinations.kt).
If a tenant exposes extra pages, add a new `Destination` enum entry and a
matching `WebPage.Public(...)` / `WebPage.Authenticated(...)` row.

## How the login + session sharing works

1. The user enters their email/username + password on the native
   [`LoginScreen`](../app/src/main/java/com/schoolnav/app/ui/screens/LoginScreen.kt).
2. [`AuthRepository.signIn`](../app/src/main/java/com/schoolnav/app/auth/AuthRepository.kt)
   `GET`s `<host>/authentication` to grab the CSRF cookie + form token,
   then `POST`s the credentials. Ramom returns `302 → /dashboard` on
   success and `302 → /authentication?...` on failure.
3. The OkHttp `CookieJar` captures every `Set-Cookie` header (`rm_session`,
   `school_cookie_name`, …) and we push them into Android's global
   `android.webkit.CookieManager` for `<host>`.
4. Every subsequent `WebFeatureScreen` opens the embedded WebView with
   those cookies already attached — so the user lands directly on the
   logged-in version of the page.
5. The session is persisted to DataStore so a cold app start still has
   the user signed in (cookies are re-seeded into `CookieManager` from
   the persisted blob before the first WebView is created).

## What's injected into every WebView page

`WebFeatureScreen` makes the embedded pages feel native rather than like a
plain browser wrapper:

- **CSS injection** (see
  [`WebAssets.mobileChromeCss`](../app/src/main/java/com/schoolnav/app/web/WebAssets.kt))
  hides the website's own header / footer / sidebar / breadcrumb / FAB and
  replaces the typography + colors with the app's brand palette. The
  injection runs in both `onPageStarted` and `onPageFinished` so the
  user never sees the website chrome flash.
- **JS injection** forces a mobile viewport meta-tag and demotes any
  fixed-position banners that escaped the CSS rules.
- **WebSettings**: `domStorageEnabled`, `cacheMode = LOAD_DEFAULT`,
  `useWideViewPort`, `loadWithOverviewMode` — caches every request and
  renders the mobile breakpoint of the Bootstrap theme.
- **Pull-to-refresh** wraps the WebView via `SwipeRefreshLayout`.
- **Skeleton placeholder** covers the page until first paint, then fades
  out.
- **Error overlay** with a retry button replaces the "this webpage isn't
  available" default page on network errors.
- **External links** (other hosts, `mailto:`, `tel:`) bounce to the OS so
  the user doesn't get trapped inside the WebView.

## Verifying a brand-new tenant

Before shipping a rebranded APK, smoke-test:

1. Public pages render without a login: News, Teachers, Gallery, About,
   Contact, Exam Results, Admit Card.
2. The native Login screen accepts a known admin/teacher account and
   bounces back to the home grid on success.
3. After login, opening any 🔒 button shows real data (e.g. Mark Entry
   shows the exam list, Salary shows the staff list).
4. Sign-out clears the session — opening a 🔒 button afterwards shows the
   "Sign in to continue" prompt again.

If any public page returns 404, the tenant either uses a different slug
or hasn't enabled that page in **Frontend Settings → Pages** in the
Ramom admin.
