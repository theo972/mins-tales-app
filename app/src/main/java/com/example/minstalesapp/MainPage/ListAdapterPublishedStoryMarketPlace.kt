package com.example.minstalesapp.MainPage

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.minstalesapp.Model.Story
import com.example.minstalesapp.R
import com.example.minstalesapp.databinding.ItemStoryMarketplaceBinding
import java.net.URL
import kotlin.concurrent.thread


class ListAdapterPublishedStoryMarketPlace(
    private val context: Activity,
    private val listOfStory: MutableList<Story>
):RecyclerView.Adapter<ListAdapterPublishedStoryMarketPlace.ViewHolder>() {

    private lateinit var binding: ItemStoryMarketplaceBinding

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = context.layoutInflater
        binding = ItemStoryMarketplaceBinding.inflate(inflater)
        val marketStoriesView = binding.root

        return ViewHolder(marketStoriesView)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        //initialize the story image
        var bitmapImage: Bitmap? = null

        //get the image with the url
        thread {
            bitmapImage = BitmapFactory.decodeStream(URL(listOfStory[position].url_icon).openStream())
        }.join()

        //setting the image after the previous thread finished
        context.runOnUiThread(Runnable {
            binding.marketStoryIcon.setImageBitmap(bitmapImage)
        })

        binding.marketStoryTitle.text = listOfStory[position].title
    }

    override fun getItemCount(): Int {
        return listOfStory.size
    }
}