package org.example.project.platform

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
actual fun rememberAppExiter(): () -> Unit {
    val context = LocalContext.current
    return remember(context) {
        {
            context.findActivity()?.finishAndRemoveTask()
        }
    }
}
