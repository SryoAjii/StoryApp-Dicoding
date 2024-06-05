package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.retrofit.response.ListStoryItem

object DataDummy {
    fun generateDummyStory(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "create $i",
                "name $i",
                "description $i",
                null,
                "id $i",
                null
            )
            items.add(story)
        }
        return items
    }
}