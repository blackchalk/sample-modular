package com.blackchalk.news_feature.newsloader.datasource

import com.blackchalk.news_feature.newsloader.model.NewsDataClass

/**
 * Created by Alvin L. Raygon on 20/11/2020.
 */
object DataRepository {
	fun getNews(): List<NewsDataClass> {
		val output = ArrayList<NewsDataClass>()
		(1..5).forEach { index ->
			output.add(NewsDataClass(index, "News Title $index", "News Description $index", index * 2))
		}

		return output
	}
}
