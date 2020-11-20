package com.blackchalk.news_feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.blackchalk.news_feature.newsloader.adapter.NewsAdapter
import com.blackchalk.news_feature.newsloader.datasource.DataRepository
import kotlinx.android.synthetic.main.activity_news_loader.*

class NewsLoaderActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_news_loader)
		setupUI()
		getData()
	}

	private fun setupUI() {
		supportActionBar?.title = "NewsLoaderActivity"
	}

	private fun getData() {
		val layoutManager = LinearLayoutManager(this)
		recyclerView.layoutManager = layoutManager
		recyclerView.adapter = NewsAdapter(DataRepository.getNews())
		val dividerItemDecoration = DividerItemDecoration(
			recyclerView.context,
			layoutManager.orientation
		)
		recyclerView.addItemDecoration(dividerItemDecoration)
	}
}
