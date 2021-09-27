package com.fobidlim.myapplication

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

/**
 * author @fobidlim
 */
class MentionEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    fun insertSpan(text: String) {
        val span = SpannableStringBuilder(text)
        span.setSpan(null, 0, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        getText()?.replace(selectionStart - 1, selectionEnd, span)

        append(" ")
        movementMethod = LinkMovementMethod.getInstance()
        setSelection(length())
    }
}