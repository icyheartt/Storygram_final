package com.cookandroid.diary_recyclerview

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TodoDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "DB2"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "TodolistDB"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TASK = "text"
        private const val COLUMN_COMPLETED = "completion"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_DATE TEXT NOT NULL," +
                    "$COLUMN_TASK TEXT NOT NULL," +
                    "$COLUMN_COMPLETED INTEGER NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }


    fun insertTodoItem(date: String, task: String, completion: Boolean = false) :Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, date)
            put(COLUMN_TASK, task)
            put(COLUMN_COMPLETED, if (completion) 1 else 0)
        }
        // insert 메서드가 반환하는 값을 반환 (행의 ID)
        val newTodoItemId = db.insert(TABLE_NAME, null, values)

        db.close()  // 데이터베이스 연결 닫기
        return newTodoItemId
    }

    fun deleteTodoItem(itemId: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(itemId.toString()))
        db.close()
    }

    fun updateTodoItem(itemId: Int, date: String, content: String, completion: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, date)
            put(COLUMN_TASK, content)
            put(COLUMN_COMPLETED, if (completion) 1 else 0)
        }

        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(itemId.toString()))
        db.close()
    }


    fun getTodoItemsByDateRange(startDate: String, endDate: String): MutableList<TodoItem> {
        val todoItems = mutableListOf<TodoItem>()
        val db = readableDatabase
        val selection = "$COLUMN_DATE BETWEEN ? AND ?"
        val selectionArgs = arrayOf(startDate, endDate)
        val cursor: Cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "$COLUMN_DATE ASC")

        with(cursor) {
            while (moveToNext()) {
                val itemId = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val date = getString(getColumnIndexOrThrow(COLUMN_DATE))
                val task = getString(getColumnIndexOrThrow(COLUMN_TASK))
                val completion = getInt(getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1

                val todoItem = TodoItem(itemId, date, task, completion)
                todoItems.add(todoItem)
            }
        }
        cursor.close()
        db.close()

        return todoItems
    }

    fun getTodoItemById(itemId: Int): TodoItem {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_DATE, COLUMN_TASK, COLUMN_COMPLETED),
            "$COLUMN_ID = ?",
            arrayOf(itemId.toString()),
            null,
            null,
            null,
            null
        )

        lateinit var todoItem: TodoItem

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID)
            val dateIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE)
            val taskIndex = cursor.getColumnIndexOrThrow(COLUMN_TASK)
            val completionIndex = cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)

            // 컬럼이 존재하는 경우에만 데이터를 가져옴
            if (idIndex != -1 && dateIndex != -1 && taskIndex != -1 && completionIndex != -1) {
                val id = cursor.getInt(idIndex)
                val date = cursor.getString(dateIndex)
                val task = cursor.getString(taskIndex)
                val completion = cursor.getInt(completionIndex) == 1

                todoItem = TodoItem(id, date, task, completion)
            }
        }
        cursor.close()
        db.close()

        return todoItem
    }
}
