package com.example.home33

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView

class StoryActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {
    private lateinit var dbHelper: DBHelper
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)
        dbHelper = DBHelper(this)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout_story)
        val menuButton = findViewById<ImageButton>(R.id.menuButton_story)

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

        val addButton = findViewById<ImageButton>(R.id.addButton_story)
        addButton.setOnClickListener {
            val intent = Intent(this, StoryAdd::class.java)
            startActivity(intent)
        }

        /*val storyList = arrayListOf(
                    StoryItem( "점심시간에 친구들과 같이 샐러드를 먹었다. 이렇게 맛있는 샐러드는 처음이었다. 수박도 맛있었다.","2023.10.20", "E:\\DiaryProject2\\app\\src\\main\\res\\drawable\\feed1.jpg",
                        arrayListOf("샐러드", "친구"), ),
                    StoryItem("아빠에게 짜증을 냈다. 미안했다.","2023.10.25", "",
                        arrayListOf("화해바람", "다툼"), ),
                    StoryItem( "워터파크를 다녀왔다. 미끄럼틀 짱재미짐","2023.10.31", "",
                        arrayListOf("미끄럼틀", "워터파크"), )
                )*/

        val stories : ArrayList<StoryItem> = dbHelper.getStoryItem()
        for(story in stories) {
            story.printStoryItem()
        }
        val rvProfile = findViewById<RecyclerView>(R.id.rv_profile)
        rvProfile.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvProfile.setHasFixedSize(true)

        if (stories.isEmpty()) {
            // 데이터베이스에 저장된 정보가 없을 경우
            Log.i("adapter" , "StoryActivity에 데이터가 없어요!")
            val noDataTextView = TextView(this)
            noDataTextView.text = "어서 글을 작성해 보세요!"
            rvProfile.addView(noDataTextView)
        }
        else { // 데이터베이스에 저장된 정보가 존재할 경우
            Log.i("adapter" , "StoryActivity에 데이터가 있어요!")
            rvProfile.adapter = StoryAdapter(stories, applicationContext)
        }

    }
    override fun onResume() {
        super.onResume()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

}


