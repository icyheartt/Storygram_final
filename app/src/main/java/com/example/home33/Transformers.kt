package com.example.home33

import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.greenfrvr.hashtagview.HashtagView
import com.greenfrvr.hashtagview.HashtagView.DataStateTransform
import com.greenfrvr.hashtagview.HashtagView.DataTransform

class Transformers : DataStateTransform<String> {
    val HASH: DataTransform<String> = object : DataStateTransform<String> {
        override fun prepare(item: String): CharSequence {
            val spannableString = SpannableString("#$item")
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#black")),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannableString
        }
        //16번째 줄 object 때문에 선언해야함
        override fun prepareSelected(item: String?): CharSequence {
            val spannableString = SpannableStringBuilder("#$item")
            spannableString.insert(0, item)
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("red")),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            return spannableString
        }


    }
    //DataStateTransform에서 상속받으므로 선언해주어야함
    override fun prepare(item: String?): CharSequence {
        val spannableString = SpannableStringBuilder("#$item")
        spannableString.insert(0, item)
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("red")),
            0,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    override fun prepareSelected(item: String?): CharSequence {
        val spannableString = SpannableStringBuilder("#$item")
        spannableString.insert(0, item)
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("red")),
            0,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }
    // 실제 코드 사용부분 HASH_SELECTED
    companion object HASH_SELECTED: HashtagView.DataTransform<String> {
        override fun prepare(item: String): CharSequence {
            val spannableString = SpannableStringBuilder("$item")
            // spannableString.insert(0, item)
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("black")),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            Log.d("sapn","spannable toString " + spannableString.toString())
            return spannableString
        }
    }
}
