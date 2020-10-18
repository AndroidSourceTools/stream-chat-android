package com.getstream.sdk.chat.view.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

/**
 * Ensures the context being accessed in a View can be cast to Activity
 */
internal fun Context.ensure(): Context = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext
    else -> this
}
