package com.example.minstalesapp.MainPage

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.minstalesapp.Api.ApiHelper
import com.example.minstalesapp.Model.Story
import com.example.minstalesapp.R
import com.example.minstalesapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val listOfStoryTypes = arrayOf("Fantasy", "History", "Medieval", "Pirate", "Horror", "Science-Fiction", "Post-Apocalyptic", "Policier")
    var mappedStories = mapOf<String, MutableList<Story>>()
    private var isMarketPlaceDataLoaded = false
    private val compteurToUpdateMarketPlace: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listOfPagesId = arrayOf(R.layout.fragment_activity_main_librairie, R.layout.fragment_activity_main_marketplace)
        compteurToUpdateMarketPlace.value = listOfStoryTypes.size

        mappedStories = mapOf(
            "Fantasy" to mutableListOf<Story>(),
            "History" to mutableListOf<Story>(),
            "Medieval" to mutableListOf<Story>(),
            "Pirate" to mutableListOf<Story>(),
            "Horror" to mutableListOf<Story>(),
            "Science-Fiction" to mutableListOf<Story>(),
            "Post-Apocalyptic" to mutableListOf<Story>(),
            "Policier" to mutableListOf<Story>()
        )

        binding.mainViewPager.adapter = MainPagerAdapterMainActivity(this, listOfPagesId, mappedStories, listOfStoryTypes)
        binding.mainViewPager.setPageScrollEnabled(false)
        binding.mainViewPager.setCurrentItem(0, false)


        binding.toggleSwitchButton.check(R.id.stories_toggle_button)

        binding.toggleSwitchButton.setOnCheckedChangeListener { _, optionId ->
            when (optionId) {
                // Event handler on "Librairie" button's click
                R.id.stories_toggle_button -> {
                    binding.mainViewPager.setCurrentItem(0, true)
                }
                // Event handler on "Market Place" button's click
                R.id.marketplace_toggle_button -> {
                    if(!isMarketPlaceDataLoaded){
                        isMarketPlaceDataLoaded = !isMarketPlaceDataLoaded
                        compteurToUpdateMarketPlace.observe(this, Observer {
                            if(compteurToUpdateMarketPlace.value == 0){
                                (binding.mainViewPager.adapter as MainPagerAdapterMainActivity).isSetup = true
                                (binding.mainViewPager.adapter as MainPagerAdapterMainActivity).setListOfStory(mappedStories)
                                (binding.mainViewPager.adapter as MainPagerAdapterMainActivity).notifyDataSetChanged()
                            }
                        })

                        val queue = Volley.newRequestQueue(this)

                        listOfStoryTypes.forEach { type ->

                            val url = ApiHelper.getStoriesFromTypes(type)

                            //Récupération de la liste d'histoires correspondant au type
                            val request = JsonObjectRequest(
                                Request.Method.GET, url, null,
                                { response ->
                                    //Parsing des données récupérées
                                    for (i in 0 until response.getJSONArray("story").length()) {
                                        val item = response.getJSONArray("story").getJSONObject(i)
                                        mappedStories[type]?.add(Story(
                                            item.getInt("idStory"),
                                            item.getString("title"),
                                            item.getString("display"),
                                            item.getString("description"),
                                            item.getString("urlFolder"),
                                            item.getString("urlIcon"),
                                            item.getDouble("price").toFloat(),
                                            item.getString("author"),
                                            item.getInt("nbDownload")
                                        ))
                                    }
                                    compteurToUpdateMarketPlace.value = compteurToUpdateMarketPlace.value!! -1
                                    Log.i("compteur", compteurToUpdateMarketPlace.value.toString())
                                },
                                { error ->
                                    Log.i("ERROR", error.toString())
                                }
                            )
                            queue.add(request)
                        }

                        (binding.mainViewPager.adapter as MainPagerAdapterMainActivity).setListOfStory(mappedStories)
                    }
                    binding.mainViewPager.setCurrentItem(1, true)
                }
            }
        }

        binding.refreshButton.setOnClickListener {
            (binding.mainViewPager.adapter as MainPagerAdapterMainActivity).notifyDataSetChanged()
        }
    }
}

// Custom View Pager to avoid manual scrolling between fragments
class CustomViewPager : ViewPager {
    private var isPageScrollEnabled = true

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return isPageScrollEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return isPageScrollEnabled && super.onInterceptTouchEvent(event)
    }

    fun setPageScrollEnabled(isPageScrollEnabled: Boolean) {
        this.isPageScrollEnabled = isPageScrollEnabled
    }
}
