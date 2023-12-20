package com.example.home33

import android.content.ContentValues
import android.content.Context

class DatabaseManager(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addPost(post: Post) {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put("title", post.title)
        values.put("content", post.content)
        values.put("date", post.date)
        values.put("tag", post.tag)
        values.put("imageUrl", post.imageUrl)

        val newRowId: Long = db.insertOrThrow("PostDB", null, values)

        db.close()
    }





    fun getLatestPost(): Post? {

        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM PostDB ORDER BY date DESC LIMIT 1", null)

        return if (cursor.moveToFirst()) {
            val post = Post(
                id = cursor.getLong(cursor.getColumnIndex("id")),
                title = cursor.getString(cursor.getColumnIndex("title")),
                content = cursor.getString(cursor.getColumnIndex("content")),
                date = cursor.getString(cursor.getColumnIndex("date")),
                tag = cursor.getString(cursor.getColumnIndex("tag")),
                imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"))
            )
            cursor.close()
            post
        } else {
            cursor.close()
            null
        }
    }
}
