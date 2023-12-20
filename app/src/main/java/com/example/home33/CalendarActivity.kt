package com.example.home33

import android.content.Intent
import android.widget.ImageView
import com.bumptech.glide.Glide
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.*

class CalendarActivity: AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar)

        dbHelper = DBHelper(this)

        val materialCalendarView = findViewById<MaterialCalendarView>(R.id.calendarView)
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

        val fabButton = findViewById<ImageButton>(R.id.fabButton)
        fabButton.setOnClickListener {
            val intent = Intent(this, StoryAdd::class.java)
            startActivity(intent)
        }

        // OnDateSelectedListener 설정
        materialCalendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE

        materialCalendarView.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
            // Get the image container
            val imageContainer = findViewById<GridLayout>(R.id.imageContainer)

            // If the date is selected
            if (selected) {
                // Your existing code to add ImageView
                val imageDataList = dbHelper.getImageData()

                // 선택한 날짜를 문자열로 받아오기
                val selectedDate = "${date.year}.${"%02d".format(date.month)}.${"%02d".format(date.day)}"

                // imageDataList에서 선택한 날짜와 일치하는 모든 이미지 URL을 찾음
                val imageUrls = imageDataList.filter { it.first == selectedDate }.map { it.second }

                // 각 이미지 URL에 대해 LinearLayout에 ImageView와 TextView를 동적으로 추가
                for (imageUrl in imageUrls) {
                    val linearLayout = LinearLayout(this)
                    linearLayout.orientation = LinearLayout.VERTICAL

                    val imageView = ImageView(this)
                    val imageLayoutParams = LinearLayout.LayoutParams(200, 200)
                    imageLayoutParams.setMargins(30, 30, 30, 10)
                    imageView.layoutParams = imageLayoutParams

                    Glide.with(this).load(imageUrl).circleCrop().into(imageView)
                    linearLayout.addView(imageView)

                    val textView = TextView(this)
                    textView.text = selectedDate
                    val textLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    textLayoutParams.setMargins(30, 10, 30, 30)
                    textView.layoutParams = textLayoutParams
                    linearLayout.addView(textView)

                    val gridLayoutParams = GridLayout.LayoutParams()
                    gridLayoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT
                    gridLayoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT
                    linearLayout.layoutParams = gridLayoutParams

                    imageContainer.addView(linearLayout)
                }
            } else {
                // 선택해제 이미지뷰 삭제
                var i = 0
                while (i < imageContainer.childCount) {
                    val child = imageContainer.getChildAt(i)
                    if (child is LinearLayout) {
                        val textView = child.getChildAt(1) as TextView
                        if (textView.text == "${date.year}.${"%02d".format(date.month)}.${"%02d".format(date.day)}") {
                            imageContainer.removeView(child)
                        } else {
                            i++
                        }
                    } else {
                        i++
                    }
                }
            }
        })

        // SaturdayDecorator,sundayDecorator 추가
        materialCalendarView.addDecorator(SaturdayDecorator())
        materialCalendarView.addDecorator(SundayDecorator())
    }
}

class SaturdayDecorator : DayViewDecorator {

    private val calendar: Calendar = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay): Boolean {
        calendar.set(day.year, day.month - 1, day.day) // Calendar 객체에 날짜 정보를 설정합니다.
        val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SATURDAY
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(Color.BLUE))
    }
}

class SundayDecorator : DayViewDecorator {

    private val calendar: Calendar = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay): Boolean {
        calendar.set(day.year, day.month - 1, day.day) // Calendar 객체에 날짜 정보를 설정합니다.
        val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SUNDAY
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(Color.RED))
    }
}