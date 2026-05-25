package com.schoolnav.app.rating

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode

/**
 * Drives the in-app rating prompt. Uses Google Play's In-App Review API when
 * available so the user never leaves the app to drop a review. If the API is
 * unavailable (e.g. side-loaded build, no Play Store) we fall back to launching
 * the Play Store listing for this package.
 *
 * Call [AppRating.request] from a UI surface; it expects an Activity context so
 * the Play overlay can attach to a window token.
 */
object AppRating {

    fun request(activity: Activity) {
        val manager: ReviewManager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                manager.launchReviewFlow(activity, task.result).addOnCompleteListener {
                    // The flow always succeeds from the caller's perspective — there
                    // is no way to know whether the user actually rated. That is by
                    // design (Play does not leak the user's decision).
                }
            } else {
                val code = (task.exception as? ReviewException)?.errorCode
                openPlayStoreFallback(activity, code)
            }
        }
    }

    private fun openPlayStoreFallback(context: Context, @ReviewErrorCode code: Int?) {
        val packageName = context.packageName
        val marketIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$packageName"),
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(marketIntent)
        } catch (_: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName"),
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
            )
        }
        // `code` left intentionally unused — diagnostic only; Play's error codes
        // are not actionable from the caller's perspective.
    }
}
