package com.example.home33
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import java.util.Random

class ReminderMainActivity : AppCompatActivity() {

    private lateinit var reminderCardView: CardView
    private lateinit var rotatingImage: ImageView
    private lateinit var reminderDate : TextView
    private lateinit var reminderBtnMenu : ImageButton
    private lateinit var drawerLayout : DrawerLayout

    private lateinit var dbHelper: DBHelper

    private var imageDataList: List<Pair<String, String>> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reminder_activity_main)

        reminderBtnMenu = findViewById(R.id.reminderBtnMenu)
        reminderCardView = findViewById(R.id.reminder_CardView)
        rotatingImage = findViewById(R.id.rotatingImage)
        drawerLayout = findViewById(R.id.drawer_layout)

        dbHelper = DBHelper(this)
        imageDataList = dbHelper.getImageData()

        rotatingImage.setImageResource(R.drawable.guide_image)

        rotatingImage.setOnClickListener {
            executeReminder()
        }

        reminderBtnMenu.setOnClickListener {
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

    }

    private fun executeReminder() {
        if (imageDataList.isEmpty()) {
            return
        }
        //랜덤한 인덱스 번호 추출
        val randomIndex = Random().nextInt(imageDataList.size)
        val randomImage = imageDataList[randomIndex].second

        Glide.with(this).load(randomImage).into(rotatingImage)

        // 이미지뷰에 랜덤 이미지 설정
        reminderDate=findViewById(R.id.reminderDate)
        reminderDate.text = imageDataList[randomIndex].first
        rotateImageView()
    }

    private fun rotateImageView() {
        // 2바퀴 회전 애니메이션 설정
        val rotateAnimation = RotateAnimation(
            0f, 720f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 4000
        rotateAnimation.fillAfter = true

        rotatingImage.startAnimation(rotateAnimation)
    }
}
