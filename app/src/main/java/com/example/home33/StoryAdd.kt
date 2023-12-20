package com.example.home33

import android.R.attr.path
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TimePicker
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID





@Suppress("DEPRECATION")
class StoryAdd : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    var imagePath: String? = null
    var photouri : Uri? = null
    var photoResult = registerForActivityResult(StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            photouri = it.data?.data

        }
    }

    private fun saveBitmapToCache(bitmap: Bitmap) {
        Log.d("dir", "cachedir = $cacheDir")

        val file = File(cacheDir, "cached_image.jpg")
        val newFileName = "${UUID.randomUUID()}.jpg"
        val newFile = File(cacheDir, newFileName)
        val newFilePath = newFile.toPath()


        if (file.exists()) {
            val newFileName = "${UUID.randomUUID()}.jpg"
            val newFilePath = File(cacheDir, newFileName).toPath()

            try {
                Files.move(file.toPath(), newFilePath, StandardCopyOption.REPLACE_EXISTING)
                Log.i("new", "newFileName: $newFileName")
                } catch (e: Exception) {
                    Log.e("Error", "Failed to rename the file", e)
                }
        } else {
            Log.i("Error", "File이 겹치지 않음!")
        }
        try {
                val outputStream = FileOutputStream(newFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                outputStream.flush()
                outputStream.close()

                // 이미지의 경로(file.absolutePath)를 사용하면 됩니다.
                imagePath = newFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun tag(editText: EditText): ArrayList<String> {
        val inputText = editText.text.toString()
        val result = ArrayList<String>() // 태그값을 저장할 배열

        val tagRegex = "#\\S+".toRegex() // '#'으로 시작하고 그 뒤에 공백이 아닌 문자가 하나 이상인 패턴을 찾는 정규표현식

        tagRegex.findAll(inputText).forEach { matchResult ->
            try {
                val tag = matchResult.value
                result.add(tag)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return result
    }


    @SuppressLint("MissingInflatedId")
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
        var addtag = findViewById<EditText>(R.id.add_tag)
        var previous = findViewById<Button>(R.id.Storyadd_Button_previous)

        btnBack2.setOnClickListener {
            finish()
        }

        var selectedDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date())

        calendar1.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(calendar.time)
        }


        next.setOnClickListener {
            if (viewFlipper.displayedChild != viewFlipper.childCount - 1) {
                viewFlipper.showNext()
                if (viewFlipper.displayedChild != 0) {
                    previous.visibility = VISIBLE
                }
                if (viewFlipper.displayedChild == viewFlipper.childCount - 1) {
                    next.text = "confirm"
                }
            } else {
                //이부분에 DB에다 데이터 전송
                try {
                    //이미지 캐싱
                    Glide.with(this)
                        .asBitmap()
                        .load(photouri)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                // 이미지가 로드되면 캐시 파일로 저장
                                saveBitmapToCache(resource)

                                // DBHelper 인스턴스 생성
                                val dpHelper = DBHelper(this@StoryAdd)
                                // 빈 arrayList emtags 생성
                                val emtags : ArrayList<String> = ArrayList<String>()

                                // StoryItem 객체 생성
                                val item = StoryItem(
                                    comment.text.toString(),
                                    selectedDate,
                                    imagePath,
                                    emtags
                                )

                                var tags : ArrayList<String> =  tag(addtag)
                                Log.d("addtag",tags.toString())
                                item.setPostTag(tags) // 깊은복사
                                Log.d("all", "storyItem 데이터 삽입 : " + comment.text.toString() + " " + SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(calendar1.date)) + " " + imagePath + " ")
                                // StoryItem 객체를 데이터베이스에 저장
                                dpHelper.insertStory(item)
                                // Activity 종료
                                finish()
                            }
                        })
                } catch (e: Exception) {
                    Log.e("StoryAdd", "Exception: ${e.javaClass.simpleName}", e)
                    var errMsg =
                        Toast.makeText(applicationContext, "모든 데이터를 기재해주세요.", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        }

        previous.setOnClickListener {
            viewFlipper.showPrevious()
            if (viewFlipper.displayedChild == 0) {
                previous.visibility = INVISIBLE
            }
            if (viewFlipper.displayedChild != viewFlipper.childCount - 1) {
                next.text = "next"
            }
        }

        var photoResult = registerForActivityResult(StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                photouri = it.data?.data
                Image1.setImageURI(photouri) // 이미지 선택 후에 이미지 뷰에 설정
            }
        }

        Image1.setOnClickListener {
            val photoPickerintent = Intent(Intent.ACTION_PICK)
            photoPickerintent.type = "image/*"
            photoResult.launch(photoPickerintent)
            try {
                Image1.setImageURI(photouri)// 이미지 선택 후에 이미지 뷰에 설정
            } catch(e : Exception) {
                e.printStackTrace()
                Log.e("err", "사진 설정 안됨")
            }
        }
    }
}