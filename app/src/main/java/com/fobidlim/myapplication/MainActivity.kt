package com.fobidlim.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.github.fobid.linkabletext.view.OnLinkClickListener
import com.github.fobid.linkabletext.widget.LinkableTextView
import java.util.regex.Pattern

@SuppressLint("InflateParams")
class MainActivity : AppCompatActivity() {

    private val textView by lazy {
        findViewById<LinkableTextView>(R.id.text)
    }

    private val editText by lazy {
        findViewById<MentionEditText>(R.id.edit)
    }

    private val mentionPopup by lazy {
        PopupWindow(
            LayoutInflater.from(this).inflate(R.layout.popup_mention, null),
            ViewGroup.LayoutParams.MATCH_PARENT,
            600
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        parseMention()

        textView.apply {
            setMentionPattern("@(\\S+)")
            setOnLinkClickListener(object : OnLinkClickListener {
                override fun onHashtagClick(hashtag: String?) {
                    Toast.makeText(this@MainActivity, "hashtag, $hashtag", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onMentionClick(mention: String?) {
                    Toast.makeText(this@MainActivity, "mention, $mention", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onEmailAddressClick(email: String?) {
                    Toast.makeText(this@MainActivity, "email, $email", Toast.LENGTH_SHORT).show()
                }

                override fun onWebUrlClick(url: String?) {
                    Toast.makeText(this@MainActivity, "url, $url", Toast.LENGTH_SHORT).show()
                }

                override fun onPhoneClick(phone: String?) {
                    Toast.makeText(this@MainActivity, "phone, $phone", Toast.LENGTH_SHORT).show()
                }

            })
        }

        editText.apply {
            doOnTextChanged { text, start, before, count ->
                if (!text.isNullOrEmpty()) {

                    if (text.substring(0, selectionEnd).lastIndexOf("@") >
                        text.substring(0, selectionEnd).lastIndexOf(" ")
                    ) {
                        mentionPopup.showAtLocation(rootView, Gravity.TOP, 0, 0)
                    } else {
                        mentionPopup.dismiss()
                    }
                } else {
                    mentionPopup.dismiss()
                }
            }
        }

        mentionPopup.contentView.findViewById<View>(R.id.name).setOnClickListener {
            editText.insertSpan("@[콩콩이맘](609012752b22ec412ca54868)")

            mentionPopup.dismiss()
        }
    }

    override fun onBackPressed() {
        if (mentionPopup.isShowing) {
            mentionPopup.dismiss()
        } else {
            super.onBackPressed()
        }
    }

    private fun parseMention() {
        val comment =
            "오 이런 사이트도 있군요 감사합니다!! @[콩콩이맘](609012752b22ec412ca54868) @[mommom](609012752b22ec412ca54869)"
        val spannableString = SpannableStringBuilder(comment)

        val matcher = Pattern.compile(REGEX_MENTION).matcher(comment)

        var range = 0
        var count = 0

        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()

            val startRange: Int
            val endRange: Int

            val mention = comment.subSequence(start, end)
            val split = mention.toString().split("](")

            val name = split[0].substring(2, split[0].length)
            val id = split[1].substring(0, split[1].length - 1)

            if (count != 0) {
                startRange = start - range
                endRange = end - range
            } else {
                startRange = start
                endRange = end
            }

            range += id.length + 4

            spannableString.replace(startRange, endRange, "@$name")
            count++
        }

        textView.text = spannableString.toString()
    }

    companion object {
        const val REGEX_MENTION = "@\\[\\S+\\]\\(\\S+\\)"
    }
}
