package com.dicoding.picodiploma.loginwithanimation.view.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.StoryItemBinding
import com.dicoding.picodiploma.loginwithanimation.retrofit.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailActivity

class MainAdapter: PagingDataAdapter<ListStoryItem, MainAdapter.MyViewHolder>(DIFF_CALLBACK) {

    inner class MyViewHolder(val binding: StoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            val name = binding.userName
            val description = binding.storyDesc
            val image = binding.storyPhoto
            name.text = story.name
            description.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(image)

            itemView.setOnClickListener{
                val moveWithDataIntent = Intent(itemView.context, DetailActivity::class.java)
                moveWithDataIntent.putExtra("EXTRA_ID", story.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(name, "name"),
                        Pair(description, "description"),
                        Pair(image, "image"),
                    )
                itemView.context.startActivity(moveWithDataIntent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}