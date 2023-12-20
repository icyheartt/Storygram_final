package com.example.storygram_cal

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar)

        val materialCalendarView = findViewById<MaterialCalendarView>(R.id.calendarView)
        val dateTextView = findViewById<TextView>(R.id.dateTextView) // 선택한 날짜를 표시할 TextView
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val menuButton = findViewById<ImageButton>(R.id.menuButton)


        menuButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        materialCalendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE

        // OnDateSelectedListener 설정
        materialCalendarView.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
            // 선택한 모든 날짜를 가져옵니다.
            val selectedDates = widget.selectedDates

            // 선택한 모든 날짜를 문자열로 변환합니다.
            val selectedDatesString = selectedDates.joinToString(" ") { it.date.toString() }

            // 선택한 날짜를 TextView에 표시
            dateTextView.text = selectedDatesString
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