package com.example.home33

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.home33.databinding.TodoActivityMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TodoActivity : AppCompatActivity() {

    private lateinit var todayTodoItem: MutableList<TodoItem>
    private lateinit var tomorrowTodoItem: MutableList<TodoItem>
    private lateinit var futureTodoItem: MutableList<TodoItem>
    private lateinit var pastTodoItem: MutableList<TodoItem>

    // 날짜 범위 계산
    private val currentDate = LocalDate.now()
    private val tomorrowDate = currentDate.plusDays(1)
    private val futureStartDate = currentDate.plusDays(2) // 오늘로부터 2일 이상 이후
    private val pastEndDate = currentDate.minusDays(1) // 오늘 이전


    // 날짜 형식 지정
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val dbHelper = TodoDBHelper(this)

    companion object {
        const val ADD_TODO_REQUEST_CODE = 1 // 아무 정수나 사용 가능 => 구별 위함
        const val EDIT_TODO_REQUEST_CODE = 2
    }

    //todo_activity_main.xml의 전체 레이아웃을 참조
    private val binding: TodoActivityMainBinding by lazy { TodoActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.todoBtnMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                Log.d("TodoActivity", "Drawer closed")
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                Log.d("TodoActivity", "Drawer opened")
            }
        }

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.calendarButton.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        binding.storyButton.setOnClickListener {
            val intent = Intent(this, StoryActivity::class.java)
            startActivity(intent)
        }

        binding.todolistButton.setOnClickListener {
            val intent = Intent(this, TodoActivity::class.java)
            startActivity(intent)
        }

        binding.reminderButton.setOnClickListener {
            val intent = Intent(this, ReminderMainActivity::class.java)
            startActivity(intent)
        }

        binding.exitButton.setOnClickListener {
            finish()
        }

         binding.completedSwitch.setOnCheckedChangeListener { _, isChecked ->
            // 스위치 상태가 변경될 때마다 숨김 처리 수행
            hideCheckedItems(binding.todoLinearLayout1)
            hideCheckedItems(binding.todoLinearLayout2)
            hideCheckedItems(binding.todoLinearLayout3)
            hideCheckedItems(binding.todoLinearLayout4)
        }

        binding.btnAdd.setOnClickListener {
            val addIntent = Intent(this, AddTodoActivity::class.java)
            addIntent.putExtra("mode", "add")
            startActivityForResult(addIntent, ADD_TODO_REQUEST_CODE)
        }

        // 카드뷰에 나타날 날짜 형식
        val cardViewFormatter = DateTimeFormatter.ofPattern("MM/dd(EEE)")

        // 오늘 날짜를 TextView에 설정
        binding.todayDateTextView.text = currentDate.format(cardViewFormatter)

        // 내일 날짜를 TextView에 설정
        binding.tomorrowDateTextView.text = tomorrowDate.format(cardViewFormatter)

        val todoLinearLayout1 = binding.todoLinearLayout1
        val todoLinearLayout2 = binding.todoLinearLayout2
        val todoLinearLayout3 = binding.todoLinearLayout3
        val todoLinearLayout4 = binding.todoLinearLayout4


        todayTodoItem = dbHelper.getTodoItemsByDateRange(currentDate.format(formatter), currentDate.format(formatter))
        tomorrowTodoItem = dbHelper.getTodoItemsByDateRange(tomorrowDate.format(formatter), tomorrowDate.format(formatter))
        futureTodoItem = dbHelper.getTodoItemsByDateRange(futureStartDate.format(formatter), "9999-12-31")
        pastTodoItem = dbHelper.getTodoItemsByDateRange("0001-01-01", pastEndDate.format(formatter))

        setupToggleView(binding.layoutBtn01, binding.layoutDetail01, binding.layoutBtn01, todoLinearLayout1, todayTodoItem)
        setupToggleView(binding.layout01, binding.layoutDetail01, binding.layoutBtn01, todoLinearLayout1, todayTodoItem)

        setupToggleView(binding.layoutBtn02, binding.layoutDetail02, binding.layoutBtn02, todoLinearLayout2, tomorrowTodoItem)
        setupToggleView(binding.layout02, binding.layoutDetail02, binding.layoutBtn02, todoLinearLayout2, tomorrowTodoItem)

        setupToggleView(binding.layoutBtn03, binding.layoutDetail03, binding.layoutBtn03, todoLinearLayout3, futureTodoItem)
        setupToggleView(binding.layout03, binding.layoutDetail03, binding.layoutBtn03, todoLinearLayout3, futureTodoItem)

        setupToggleView(binding.layoutBtn04, binding.layoutDetail04, binding.layoutBtn04, todoLinearLayout4, pastTodoItem)
        setupToggleView(binding.layout04, binding.layoutDetail04, binding.layoutBtn04, todoLinearLayout4, pastTodoItem)
    }

    // 메인 액티비티에서 onActivityResult 메서드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_TODO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // AddTodoActivity에서 전달한 새로운 TodoItem의 ID를 받음
            val newTodoItemId = data?.getIntExtra("newTodoItemId", -1)

            if (newTodoItemId != null && newTodoItemId != -1) {
                // 새로운 TodoItem을 데이터베이스에서 가져와서 적절한 리스트에 추가
                val newTodoItem = dbHelper.getTodoItemById(newTodoItemId)
                addTodoItemToAppropriateList(newTodoItem)
            }
        } else if (requestCode == EDIT_TODO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 수정 모드 처리
            val editedTodoItemId = data?.getIntExtra("editedTodoItemId", -1)
            if (editedTodoItemId != null && editedTodoItemId != -1) {
                val editedTodoItem = dbHelper.getTodoItemById(editedTodoItemId)
                editTodoItemToAppropriateList(editedTodoItem)
            }
        }
    }

    //해당하는 날짜의 리스트에 항목 추가
    private fun editTodoItemToAppropriateList(editedTodoItem: TodoItem) {
        when (editedTodoItem.date) {
            currentDate.format(formatter) -> {
                val index = todayTodoItem.indexOfFirst { it.id == editedTodoItem.id }
                if (index != -1) {
                    todayTodoItem[index] = editedTodoItem
                    populateTodoItems(binding.todoLinearLayout1, todayTodoItem)
                }
            }
            tomorrowDate.format(formatter) -> {
                val index = tomorrowTodoItem.indexOfFirst { it.id == editedTodoItem.id }
                if (index != -1) {
                    tomorrowTodoItem[index] = editedTodoItem
                    populateTodoItems(binding.todoLinearLayout2, tomorrowTodoItem)
                }
            }
            in (futureStartDate.format(formatter)).."9999-12-31" -> {
                val index = futureTodoItem.indexOfFirst { it.id == editedTodoItem.id }
                if (index != -1) {
                    futureTodoItem[index] = editedTodoItem
                    populateTodoItems(binding.todoLinearLayout3, futureTodoItem)
                }
            }
            in "0001-01-01" .. (pastEndDate.format(formatter)) -> {
                val index = pastTodoItem.indexOfFirst { it.id == editedTodoItem.id }
                if (index != -1) {
                    pastTodoItem[index] = editedTodoItem
                    populateTodoItems(binding.todoLinearLayout4, pastTodoItem)
                }
            }
        }
    }


    //해당하는 날짜의 리스트에 항목 추가
    private fun addTodoItemToAppropriateList(newTodoItem: TodoItem) {
        when (newTodoItem.date) {
            currentDate.format(formatter) -> {
                todayTodoItem.add(newTodoItem)
                populateTodoItems(binding.todoLinearLayout1, todayTodoItem)
            }
            tomorrowDate.format(formatter) -> {
                tomorrowTodoItem.add(newTodoItem)
                populateTodoItems(binding.todoLinearLayout2, tomorrowTodoItem)
            }
            in (futureStartDate.format(formatter)).."9999-12-31" -> {
                futureTodoItem.add(newTodoItem)
                populateTodoItems(binding.todoLinearLayout3, futureTodoItem)
            }
            in "0001-01-01" .. (pastEndDate.format(formatter)) -> {
                pastTodoItem.add(newTodoItem)
                populateTodoItems(binding.todoLinearLayout4, pastTodoItem)
            }
        }
    }

    //카드뷰 열고 닫기
    private fun setupToggleView(
        triggerView: View,
        detailLayout: View,
        rotationBtn: ImageButton,
        linearLayout: LinearLayout,
        todoItems: MutableList<TodoItem>
    ) {
        triggerView.setOnClickListener {
            if (detailLayout.visibility == View.VISIBLE) {
                detailLayout.visibility = View.GONE
                rotationBtn.animate().apply {
                    duration = 300
                    rotation(0f)
                }
                linearLayout.removeAllViews()
            } else {
                detailLayout.visibility = View.VISIBLE
                rotationBtn.animate().apply {
                    duration = 300
                    rotation(90f)
                }
                populateTodoItems(linearLayout, todoItems)
            }
        }
    }

    //할일 리스트 추가
    private fun populateTodoItems(linearLayout: LinearLayout, todoItems: MutableList<TodoItem>) {
        linearLayout.removeAllViews()

        val layoutInflater = LayoutInflater.from(this)
        var i = 0
        for (item in todoItems) {
            //todoView 변수에 R.layout.todo_item에서 인플레이트된 뷰 저장
            val todoView = layoutInflater.inflate(R.layout.todo_item, null)
            val dueDateTextView = todoView.findViewById<TextView>(R.id.todo_DueDateTextView)
            val taskTextView = todoView.findViewById<TextView>(R.id.todo_TextViewTask)
            val checkBox = todoView.findViewById<CheckBox>(R.id.todo_CheckBox)
            val btnEdit = todoView.findViewById<ImageButton>(R.id.todo_btnEdit)
            val btnDelete = todoView.findViewById<ImageButton>(R.id.todo_btnDelete)
            val itemId = item.id  // 해당 항목의 ID를 얻어옴

            taskTextView.text = item.task
            checkBox.isChecked = item.completion
            dueDateTextView.text = item.date
            linearLayout.addView(todoView)

            // 초기 체크 상태에 따라 가운데 줄 설정
            updateCancleline(taskTextView, checkBox.isChecked)

            // 체크 박스의 상태가 변경될 때의 리스너 설정
            checkBox.setOnCheckedChangeListener { _, completion ->
                // 체크 상태에 따라 가운데 줄 설정
                updateCancleline(taskTextView, completion)
                item.completion = completion
                dbHelper.updateTodoItem(item.id, item.date, item.task, completion)
            }


            // 수정 버튼에 대한 클릭 리스너
            btnEdit.setOnClickListener {
                val editIntent = Intent(this, AddTodoActivity::class.java)
                editIntent.putExtra("mode", "edit")
                editIntent.putExtra("itemId", itemId)  // 수정할 아이템의 ID 전달
                startActivityForResult(editIntent, EDIT_TODO_REQUEST_CODE)
            }

            // 삭제 버튼에 대한 클릭 리스너
            btnDelete.setOnClickListener {
                // 다이얼로그 생성
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("삭제 확인")
                    .setMessage("이 할 일 항목을 삭제하시겠습니까?")
                    .setPositiveButton("확인") { _, _ ->
                        // 사용자가 확인을 선택한 경우
                        // 데이터베이스에서 해당 ID 항목 삭제
                        dbHelper.deleteTodoItem(itemId)
                        //할당했던 리스트에서 삭제
                        todoItems.remove(item)
                        // 해당 뷰를 LinearLayout에서 제거
                        linearLayout.removeView(todoView)
                    }
                    .setNegativeButton("취소") { _, _ ->
                        // 아무 동작도 수행하지 않음
                    }
                    .create()

                // 다이얼로그 표시
                alertDialog.show()
            }

        }
    }

    // 체크 상태에 따라 가운데 줄 설정
    private fun updateCancleline(textView: TextView, isChecked: Boolean) {
        if (isChecked) {
            // 체크된 경우 가운데 줄 추가
            textView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            // 체크가 해제된 경우 가운데 줄 제거
            textView.paintFlags = 0
        }
    }

    //완료 항목 숨기기
    private fun hideCheckedItems(linearLayout: LinearLayout) {
        // 스위치의 상태 가져오기
        val isSwitchChecked = binding.completedSwitch.isChecked

        // 모든 레이아웃 반복
        for (i in 0 until linearLayout.childCount) {
            val todoView = linearLayout.getChildAt(i) as LinearLayout
            val checkBox = todoView.findViewById<CheckBox>(R.id.todo_CheckBox)

            // 스위치 on이고 체크 true면 숨기기
            if (isSwitchChecked && checkBox.isChecked) {
                todoView.visibility = View.GONE
            } else {
                // 스위치가 off이거나 체크 false면 보이기
                todoView.visibility = View.VISIBLE
            }
        }
    }
}
