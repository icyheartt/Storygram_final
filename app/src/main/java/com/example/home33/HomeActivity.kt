package com.example.home33

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import android.Manifest
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


class HomeActivity : AppCompatActivity() {
    private lateinit var DateView: TextView
    private lateinit var customLayout: LinearLayout
    private lateinit var dbHelper: DBHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    Toast.makeText(this@HomeActivity, "권한 허가", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Toast.makeText(
                        this@HomeActivity,
                        "권한 거부\n$deniedPermissions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .setDeniedMessage("스토리 사진을 보기 위해서는 갤러리 접근 권한이 필요해요\n\n[설정] > [권한]에서 권한을 허용하실 수 있어요.")
            .setPermissions(Manifest.permission.READ_MEDIA_IMAGES)
            .check();

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
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        val storyButton = findViewById<ImageButton>(R.id.storyButton)
        storyButton.setOnClickListener {
            val intent = Intent(this, StoryActivity::class.java)
            startActivity(intent)
        }

        val todolistButton = findViewById<ImageButton>(R.id.todolistButton)
        todolistButton.setOnClickListener {
            val intent = Intent(this, TodoActivity::class.java)
            startActivity(intent)
        }

        val reminderButton = findViewById<ImageButton>(R.id.reminderButton)
        reminderButton.setOnClickListener {
            val intent = Intent(this, ReminderMainActivity::class.java)
            startActivity(intent)
        }

        val exitButton = findViewById<ImageButton>(R.id.exitButton)
        exitButton.setOnClickListener {
            finish()
        }

        val addButton = findViewById<ImageButton>(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, StoryAdd::class.java)
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
        dbHelper = DBHelper(this)
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
                val tagsText = entry.tag?.takeIf { it.isNotEmpty() }?.joinToString(" ") ?: ""
                tagsTextView.text = tagsText

                contentTextView.text = entry.content

                Glide.with(this).load(entry.imageurl).into(photoImageView)

                // LinearLayout에 추가
                linearLayout.addView(childLayout)
            }
        }
    }
    companion object {
        const val REQ_PERMISSION_MAIN = 1001
    }
}
