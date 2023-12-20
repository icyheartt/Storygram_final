package com.cookandroid.add_todolist

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TimePicker
import android.widget.ViewFlipper
import androidx.activity.result.contract.ActivityResultContracts
import com.greenfrvr.hashtagview.HashtagView
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity


class storyadd : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    var photouri : Uri? = null
    var photoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            photouri = it.data?.data

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.storyadd)
        var btnBack2 = findViewById<ImageButton>(R.id.Storyadd_ImageButton_back)
        var Image1 = findViewById<ImageView>(R.id.Storyadd_ImageView_image)
        var comment = findViewById<EditText>(R.id.Storyadd_EditText_comment)
        var viewFlipper = findViewById<ViewFlipper>(R.id.Storyadd_ViewFlipper_viewflipper)
        var next = findViewById<Button>(R.id.Storyadd_Button_next)
        var calendar1 = findViewById<CalendarView>(R.id.Storyadd_CalendarView_calendar)
        var time1 = findViewById<TimePicker>(R.id.Storyadd_TimePicker_time)
        var addtag = findViewById<HashtagView>(R.id.Storyadd_HashtagView_addtag)
        var previous = findViewById<Button>(R.id.Storyadd_Button_previous)

        btnBack2.setOnClickListener {
                finish()
        }

        next.setOnClickListener {
            if (viewFlipper.displayedChild != viewFlipper.childCount-1) {
                viewFlipper.showNext()
                if (viewFlipper.displayedChild != 0) {
                    previous.visibility = VISIBLE
                }
                if (viewFlipper.displayedChild == viewFlipper.childCount-1) {
                    next.text = "confirm"
                }
            }
            else {
                //이부분에 DB에다 데이터 전송
                finish()
                }
            }

        previous.setOnClickListener {
            viewFlipper.showPrevious()
            if (viewFlipper.displayedChild == 0) {
                previous.visibility = INVISIBLE
            }
            if (viewFlipper.displayedChild != viewFlipper.childCount-1) {
                next.text = "next"
            }
        }

        Image1.setOnClickListener {
            val photoPickerintent = Intent(Intent.ACTION_PICK)
            photoPickerintent.type = "image/*"
            photoResult.launch(photoPickerintent)
            }
        }
    }