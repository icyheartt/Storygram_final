package com.example.storyactivity

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.greenfrvr.hashtagview.HashtagView
import com.example.storyactivity.Transformers


class StoryAdapter(val storyList: ArrayList<StoryItem>) : RecyclerView.Adapter<StoryAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                val curPos : Int = adapterPosition
                val story: StoryItem = storyList.get(curPos)
            }
        }
    }

    override fun getItemCount(): Int {
        return storyList.size
    }
    override fun onBindViewHolder(holder: StoryAdapter.CustomViewHolder, position: Int) {
        val feed_uri : Uri = Uri.parse(storyList.get(position).imageurl)
        holder.feed.setImageURI(feed_uri)
        holder.userimage.setImageResource(R.mipmap.ic_launcher)
        holder.content.text = storyList.get(position).content
        holder.date.text = storyList.get(position).date
        holder.hashtag.setData(storyList.get(position).tag, Transformers.HASH)

    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimage = itemView.findViewById<ImageView>(R.id.iv_userimage)
        val feed = itemView.findViewById<ImageView>(R.id.iv_feed)
        val date = itemView.findViewById<TextView>(R.id.tv_date)
        val content = itemView.findViewById<TextView>(R.id.tv_text)
        val hashtag = itemView.findViewById<HashtagView>(R.id.tag)

    }

}

