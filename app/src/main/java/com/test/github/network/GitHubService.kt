package com.test.github.network

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.test.github.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object GitHubServiceModule {
    @Provides
    @Singleton
    fun provideGitHubService(@ApplicationContext context: Context): GitHubService {
        return ApiGitHubService(context)
    }
}

interface GitHubService {
    val gitHubRepository: GitHubRepository
}

class ApiGitHubService @Inject constructor(context: Context) : GitHubService {
    private val baseUrl = context.getString(R.string.url_base)

    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }

    override val gitHubRepository: GitHubRepository by lazy {
        NetworkGitHubRepository(retrofitService)
    }
}