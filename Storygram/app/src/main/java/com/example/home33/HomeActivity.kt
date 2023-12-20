package com.example.home33

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.*
import com.example.home33.DBHelper
import com.example.home33.StoryItem

class HomeActivity : AppCompatActivity() {
    private lateinit var DateView: TextView
    private lateinit var customLayout: LinearLayout
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        DateView = findViewById(R.id.DateView)
        customLayout = findViewById(R.id.customLayout)
        dbHelper = DBHelper(this)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val menuButton = findViewById<ImageButton>(R.id.menuButton)

        menuButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val calendarButton = findViewById<ImageButton>(R.id.calendarButton)
        calendarButton.setOnClickListener {
            val intent = Intent(this, calendarActivity::class.java)
            startActivity(intent)
        }

        val storyButton = findViewById<ImageButton>(R.id.storyButton)
        storyButton.setOnClickListener {
            val intent = Intent(this, StoryActivity::class.java)
            startActivity(intent)
        }

        val todolistButton = findViewById<ImageButton>(R.id.todolistButton)
        todolistButton.setOnClickListener {
            val intent = Intent(this, todoActivity::class.java)
            startActivity(intent)
        }

        val reminderButton = findViewById<ImageButton>(R.id.reminderButton)
        reminderButton.setOnClickListener {
            val intent = Intent(this, reminderActivity::class.java)
            startActivity(intent)
        }

        val exitButton = findViewById<ImageButton>(R.id.exitButton)
        exitButton.setOnClickListener {
            finish()
        }

        val addButton = findViewById<ImageButton>(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, storyadd::class.java)
            startActivity(intent)
        }

        displayDiaryEntries()
    }

    override fun onResume() {
        super.onResume()

        displayDiaryEntries()
        // 현재 날짜 표시
        val timeZone = TimeZone.getDefault()
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        sdf.timeZone = timeZone
        val currentDate = sdf.format(Date())
        findViewById<TextView>(R.id.DateView).text = currentDate
    }

    private fun displayDiaryEntries() {
        // 데이터베이스에서 스토리 엔트리 가져오기
        val entries: ArrayList<StoryItem> = dbHelper.getStoryItem()

        // LinearLayout 찾기
        val linearLayout = findViewById<LinearLayout>(R.id.diaryLayout)

        linearLayout.removeAllViews()

        if (entries.isEmpty()) {
            // 데이터베이스에 저장된 정보가 없을 경우
            val noDataTextView = TextView(this)
            noDataTextView.text = "어서 글을 작성해 보세요!"
            linearLayout.addView(noDataTextView)
        } else {
            // 가져온 스토리 엔트리를 UI에 동적으로 추가
            for (entry in entries) {
                // 새로운 레이아웃 인플레이트
                val childLayout = layoutInflater.inflate(R.layout.home_entry, null)

                // TextView와 ImageView 찾기
                val dateTextView = childLayout.findViewById<TextView>(R.id.dateTextView)
                val tagsTextView = childLayout.findViewById<TextView>(R.id.tagsTextView)
                val contentTextView = childLayout.findViewById<TextView>(R.id.contentTextView)
                val photoImageView = childLayout.findViewById<ImageView>(R.id.photoImageView)

                dateTextView.text = entry.date
                tagsTextView.text = entry.tag?.joinToString(" ") { "#$it" } ?: ""
                contentTextView.text = entry.content

                // 이미지 로딩 (Glide 등 사용)
                //Glide.with(this).load(entry.imageUrl).into(photoImageView)

                // LinearLayout에 추가
                linearLayout.addView(childLayout)
            }
        }
    }
}
