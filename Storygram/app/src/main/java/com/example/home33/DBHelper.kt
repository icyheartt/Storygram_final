//stortActivity의 DBHelper
package com.example.storyactivity

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlin.math.log


class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) { //DBHelper가 데이터베이스가 처음 생성되었을때에 대한 상태값을 가지고 올 수 있다
        // 데이터베이스가 생성되었을때 호출
        // 데이터베이스 -> 테이블 -> 컬럼 -> 값
        db.execSQL("CREATE TABLE IF NOT EXISTS HashtagDB (id INTEGER, name TEXT);")
        db.execSQL("CREATE TABLE IF NOT EXISTS PostDB (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT, date TEXT NOT NULL, imageurl TEXT);") //sql쿼리문
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS HashtagDB;")
        db.execSQL("DROP TABLE IF EXISTS PostDB;")

        onCreate(db)
    }

    fun getStoryItem(): ArrayList<StoryItem> {
        // val storyList: ArrayList<StoryItem> get() {
        // SELECT 문 (할일 목록을 조회)
        val storyItems: ArrayList<StoryItem> = ArrayList<StoryItem>()
        val tagList: ArrayList<String> = ArrayList<String>()
        val db = readableDatabase
        var cursor = db.rawQuery(
            "SELECT PostDB.id, PostDB.content, PostDB.date, PostDB.imageurl, HashtagDB.name FROM PostDB INNER JOIN HashtagDB ON PostDB.id = HashtagDB.id ORDER BY date DESC;",
            null
        )
        if (cursor.count != 0) {
            // 조건문 데이터가 있을때 내부 수행
            while (cursor.moveToNext()) {
                var id = cursor.getInt(cursor.getColumnIndex("id"))
                var content = cursor.getString(cursor.getColumnIndex("content"))
                var writeDate = cursor.getString(cursor.getColumnIndex("date"))
                var imageurl = cursor.getString(cursor.getColumnIndex("imageurl"))


                var cursor1 =
                    db.rawQuery("SELECT id = " + id + " FROM HashtagDB ORDER BY id;", null)
                for (i: Int in 0 until cursor1.count) {
                    Log.d("cursor1", "왜 안되냐아" + cursor1.getColumnIndex("name") )
                    val tag = cursor1.getString(cursor1.getColumnIndex("name"))
                    tagList.set(i, tag)
                }
                var storyItem = StoryItem(content, writeDate, imageurl, tagList)
                storyItem.setPostContent(content)
                storyItem.setPostDate(writeDate)
                storyItem.setPostTag(tagList)
                storyItems.add(storyItem)
            }
        }

        cursor.close()

        return storyItems
    }

    fun insertStory(storyItem : ArrayList<StoryItem>) {
        val db : SQLiteDatabase = this.writableDatabase
        db.beginTransaction()
        try {
            for(story in storyItem) {
                var content: String = story.content
                var date: String = story.date
                var imageurl: String = story.imageurl
                var tags: ArrayList<String> = story.tag
                for (tag in tags) {
                    var value: ContentValues = ContentValues()
                    value.put("name", tag)
                    db.insert(TABLE_HASH, null, value)
                }

                var values: ContentValues = ContentValues()
                values.put("content", content)
                values.put("date", date)
                values.put("imageurl", imageurl)
                db.insert(TABLE_POST, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        db.close()
    }




    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "DB2.db"
        private const val TABLE_POST = "PostDB"
        private const val TABLE_HASH = "HashtagDB"
    }
}
