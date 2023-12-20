package com.example.storyactivity

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SuperscriptSpan
import com.greenfrvr.hashtagview.HashtagView
import com.greenfrvr.hashtagview.HashtagView.DataStateTransform
import com.greenfrvr.hashtagview.HashtagView.DataTransform
import java.util.Locale


class Transformers {
    val HASH_SELECTED: DataTransform<String> = object : DataStateTransform<String> {
        override fun prepare(item: String): CharSequence {
            val spannableString = SpannableString("#$item")
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#85F5F5F5")),
                0,
                1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannableString
        }

        override fun prepareSelected(item: String): CharSequence {
            val spannableString = SpannableString("#$item")
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#85F5F5F5")),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
                StrikethroughSpan(),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannableString
        }
    }

    companion object {
        val HASH = DataTransform<String> { item ->
            val spannableString = SpannableString("#$item")
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#85F5F5F5")),
                0,
                1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString
        }
    }
}