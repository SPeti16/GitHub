package com.test.github.network

import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {
    @GET("search/repositories")
    suspend fun getSearch(@Query("q") search: String, @Query("page") page: Int): GitHubSearch

}