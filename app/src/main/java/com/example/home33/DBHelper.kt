package com.example.home33

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Arrays

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    var mContext: Context? = context

    init {
        Log.d("con", "db init 실행!!")
        DB_PATH = "/data/data/" + context?.packageName + "/databases/"
        this.mContext = context;
        dataBaseCheck()
    }

    private fun dataBaseCheck() {
        Log.d("co4", "db BaseCheck 실행!!")
        val dbFile = File(DB_PATH + DB_NAME)
        if (dbFile.exists()) {
            dbCopy()
            Log.d(TAG, "Database is copied.")
        }
        else {
            Log.d(TAG, "Database is not copied!!!!!!! 왜 ")
        }
    }

    private fun dbCopy() {
        /*
        try {
            Log.d("on", "db copy 실행!!")
            val folder = File(DB_PATH)
            if (!folder.exists()) {
                folder.mkdir()
            }
            val inputStream = mContext!!.assets.open(DB_NAME)
            val out_filename = DB_PATH + DB_NAME
            val outputStream: OutputStream = FileOutputStream(out_filename)
            val mBuffer = ByteArray(1024)
            var mLength: Int
            while (inputStream.read(mBuffer).also { mLength = it } > 0) {
                outputStream.write(mBuffer, 0, mLength)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("dbCopy", "IOException 발생함")
        }*/
    }


    override fun onCreate(db: SQLiteDatabase) { //DBHelper가 데이터베이스가 처음 생성되었을때에 대한 상태값을 가지고 올 수 있다
        // 데이터베이스가 생성되었을때 호출
        // 데이터베이스 -> 테이블 -> 컬럼 -> 값
        Log.d("con", "db oncreate 실행!!")

        db.execSQL("CREATE TABLE IF NOT EXISTS HashtagDB (id INTEGER, name TEXT);")
        db.execSQL("CREATE TABLE IF NOT EXISTS PostDB (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT, date TEXT NOT NULL, imageurl TEXT);") //sql쿼리문
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        //Toast.makeText(mContext,"onOpen()",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onOpen() : DB Opening!")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS HashtagDB;")
        db.execSQL("DROP TABLE IF EXISTS PostDB;")

        onCreate(db)
    }

    @SuppressLint("Range")
    fun getStoryItem(): ArrayList<StoryItem> {
        // val storyList: ArrayList<StoryItem> get() {
        // SELECT 문 (스토리 전체 목록을 조회)
        val storyItems: ArrayList<StoryItem> = ArrayList<StoryItem>()
        val tagList = Array(10, {""}) // 태그는 최대 10개까지 저장
        val db = this.writableDatabase
        var cursor = db.rawQuery("SELECT * FROM PostDB ORDER BY date DESC;", null)  // id ASC;
        var cursor_cnt = db.rawQuery("select count(*) from HashtagDB;", null)
        cursor_cnt.moveToFirst()
        var cnt = cursor_cnt.getInt(0)
        Log.i("cnt", "cnt : " + cnt)
        if (cursor.count != 0) {
            // 조건문 데이터가 있을때 내부 수행
            while (cursor.moveToNext()) {
                Arrays.fill(tagList, null) // tagList 임시배열 초기화
                var id = cursor.getInt(cursor.getColumnIndex("id"))
                var content = cursor.getString(cursor.getColumnIndex("content"))
                var writeDate = cursor.getString(cursor.getColumnIndex("date"))
                var imageurl = cursor.getString(cursor.getColumnIndex("imageurl"))

                var cursor1 =
                    db.rawQuery("SELECT * FROM HashtagDB WHERE id = '$id';", null)

                var i = 0
                var close = 0

                if (cursor1 != null && cursor1.moveToFirst()) {
                    while(cursor1.moveToNext()) {
                        if(i == 0) { // 첫번째 데이터 인식하기 위해 moveToFirst() 사용
                            var mtf = cursor1.moveToFirst()
                            Log.d("mtf", "cursor1 movetofirst" + mtf) // 내부에 데이터 존재하면 mtf true 반환
                        }
                        var idx = cursor1.getColumnIndex("name")
                        Log.d("cusor1", "cursor1 name column index" + idx) // name column index = 1
                        Log.d("tag", "cursor1의 name컬럼 string 가져오기 " + cursor1.getString(idx))
                        i++
                        try {
                            tagList[i] = cursor1.getString(idx)
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            Log.d("er","Error: the array is empty!")
                        }
                    }
                }
                // tagList는 Array, tags는 ArrayList => ArrayList에서 인덱스를 통한 수정 기능 동작 어려움, 추가된 항목의 인덱스 문제 등으로 임의의 Array배열 tagList 만듬
                var tags : ArrayList<String> = arrayListOf<String>()
                for(tag in tagList) {
                    if(tag != null) {
                        tags.add(tag)
                    }
                }
                Arrays.fill(tagList, null)
                var emList : ArrayList<String> = arrayListOf<String>()
                Log.d("storyItem", "storyitem 내용물" + content + " " + writeDate + " " + imageurl + " " + tags.toString())
                // storyItem 매개변수 전달해서 생성자 호출할때 ArrayList끼리 얕은 복사가 발생하므로 setPostTag()함수를 만들어 따로 깊은 복사 구현
                var storyItem = StoryItem(content, writeDate, imageurl, emList)
                storyItem?.setPostTag(tags)
                storyItems.add(storyItem)

                tags.clear()
                close++
                if(close == cnt) cursor1.close()
            }
        }

        cursor.close()

        return storyItems
    }

    fun insertStory(story: StoryItem) {
        val db : SQLiteDatabase = this.writableDatabase
        var cursor = db.rawQuery("select id from PostDB ORDER BY id DESC LIMIT 1;", null)

        db.beginTransaction()
        try {
                var content: String? = story.content
                var date: String? = story.date
                var imageurl: String? = story.imageurl
                var tags: ArrayList<String> = ArrayList<String>()
                story.tag?.let { tags.addAll(it) }

                var values: ContentValues = ContentValues()
                values.put("imageurl", imageurl)
                values.put("date", date)
                values.put("content", content)
                var id = db.insert(TABLE_POST, null, values)

                Log.d("tag", "dbhelper안에 있는 insertStory tag " + tags.toString())
                if (tags != null) {
                    for (tag in tags) {
                        var value: ContentValues = ContentValues()
                        value.put("name", tag)
                        value.put("id", id)
                        db.insert(TABLE_HASH, null, value)
                    }
                }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        db.close()
    }

    fun getStoryItemByDate(date: String): StoryItem? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM PostDB WHERE date = ?", arrayOf(date))

        return if (cursor.moveToFirst()) {
            val content = cursor.getString(cursor.getColumnIndex("content"))
            val imageurl = cursor.getString(cursor.getColumnIndex("imageurl"))
            StoryItem(content, date, imageurl, null)
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }

    fun getImageData(): List<Pair<String, String>> {
        val imageDataList = mutableListOf<Pair<String, String>>()

        // 데이터베이스 쿼리 수행
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT date, imageurl FROM PostDB", null)

        // 결과에서 날짜와 이미지 URL 추출
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(cursor.getColumnIndex("date"))
                val imageUrl = cursor.getString(cursor.getColumnIndex("imageurl"))
                imageDataList.add(Pair(date, imageUrl))
            } while (cursor.moveToNext())
        }

        // 리소스 해제
        cursor.close()

        return imageDataList
    }



    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "DB2.db"
        private const val TABLE_POST = "PostDB"
        private const val TABLE_HASH = "HashtagDB"
        private var DB_PATH = ""

    }
}
