package com.test.github.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubSearch (
    @SerialName("total_count") val totalCount: Int,
    @SerialName("incomplete_results") val incompleteResults: Boolean,
    val items: List<Repository>
)

@Serializable
data class Repository(
    val name: String,
    val owner: Owner,
    @SerialName("html_url") val htmlUrl: String,
    val description: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("stargazers_count") val stargazersCount: Int,
    val forks: Int
)

@Serializable
data class Owner(
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("html_url") val htmlUrl: String
)
