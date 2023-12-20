package com.example.home33

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTodoActivity : Activity() {
    private lateinit var dateButton: Button
    private lateinit var btnBack: Button
    private lateinit var btnOK: Button
    private lateinit var textTask : EditText
    private val dbHelper = TodoDBHelper(this)
    private var itemId: Int = -1
    private lateinit var existingItem: TodoItem
    private val selectedDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_todo)


        val mode = intent.getStringExtra("mode")

        dateButton = findViewById<Button>(R.id.todo_dateButton)
        btnBack = findViewById<Button>(R.id.todo_btnBack)
        btnOK = findViewById<Button>(R.id.todo_btnOK)
        textTask = findViewById<EditText>(R.id.todo_EditTextTask)

        if (mode == "edit") {
            itemId = intent.getIntExtra("itemId", -1)
            existingItem = dbHelper.getTodoItemById(itemId)

            // 기존 항목의 정보를 가져와서 UI에 반영
            textTask.setText(existingItem.task)
            selectedDate.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(existingItem.date)
            val dateFormat = SimpleDateFormat("yyyy.MM.dd.(EEE)", Locale.KOREA)
            dateButton.text = dateFormat.format(selectedDate.time)
        } else {
            // 추가 모드인 경우 현재 날짜로 초기화
            updateDateButton()
        }




        // dateButton 클릭 시 DatePickerDialog 표시
        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        //뒤로가기 누르면 추가화면 인텐트 종료
        btnBack.setOnClickListener{
            finish()
        }

        //확인 클릭 시 데이터베이스에 날짜, 할 일 내용 저장
        btnOK.setOnClickListener{
            // 날짜 및 할 일 내용 가져오기
            val dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
            val todoTask = textTask.text.toString()  // EditText의 내용 가져오기

            // 에디트텍스트가 비어있는지 확인
            if (todoTask.isBlank()) {
                Log.d("YourTag", "에디트텍스트 내용이 비어있습니다.")
                // 에디트텍스트에 아무 내용도 입력되지 않았을 때
                Toast.makeText(this, "내용을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener  // 함수 종료
            }

            // 수정 모드일 경우, 해당 아이템 업데이트
            if (mode == "edit") {
                dbHelper.updateTodoItem(itemId, dueDate, todoTask, existingItem.completion)
                val resultIntent = Intent()
                resultIntent.putExtra("editedTodoItemId", itemId)
                setResult(Activity.RESULT_OK, resultIntent)
            } else if (mode == "add"){
                // 추가 모드일 경우, 새로운 아이템 추가
                val newTodoItemId = dbHelper.insertTodoItem(dueDate, todoTask, false)
                val resultIntent = Intent()
                resultIntent.putExtra("newTodoItemId", newTodoItemId.toInt())
                setResult(Activity.RESULT_OK, resultIntent)
            }
            finish()
        }
    }


    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                // 사용자가 날짜를 선택하면 Calendar 객체 업데이트
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, monthOfYear)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // 선택한 날짜로 dateButton 업데이트
                updateDateButton()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateDateButton() {
        // 선택된 날짜를 dateButton에 표시
        val dateFormat = SimpleDateFormat("yyyy.MM.dd.(EEE)", Locale.KOREA)
        dateButton.text = dateFormat.format(selectedDate.time)
    }
}
