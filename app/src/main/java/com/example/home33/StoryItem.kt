package com.example.home33

import android.util.Log

open class StoryItem(var content: String? = null, var date: String? = null, var imageurl: String? = null, var tag: ArrayList<String>? = null) {
    fun setPostTag(t: ArrayList<String>) {
        Log.d("t", "매개변수 tag = " + t.toString())
        Log.d("o", "원래 객체변수 tag = " + tag.toString())
        // StoryItem의 생성자 매개변수 tag는 DBHelper의 emList(emptyList)라는 비어있는 ArrayList로 초기화된다.
        // setPostTag의 매개변수 t는 태그가 담긴 ArrayList이며, addAll을 통해 storyItem 객체 멤버변수 tag에 깊은 복사를 실행한다.
        if(tag != null) tag?.clear()
        tag?.addAll(t)
        Log.i("초", "StoryItem에서 tag 초기화 했어요" + tag.toString())
    }

    // StoryItem 객체 값 출력하는 함수(확인용)
    fun printStoryItem() {
        Log.i("print", "printStoryItem 실행!")
        Log.d("story", "content : " + content)
        Log.d("story1", "date : " + date)
        Log.d("imageurl", "imageurl : " + imageurl)
        if (this.tag == null) Log.d("tagnull", "tag가 null이네요")
        else {
            Log.d("notnull", "휴 다행히 tag가 null이 아니네")
            Log.d("tag", "tag : " + this.tag.toString())
        }
    }
}


