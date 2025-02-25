package com.test.github.network

interface GitHubRepository {
    suspend fun getSearch(search: String, page: Int): GitHubSearch
}

class NetworkGitHubRepository(
    private val pokemonApiService: GitHubApiService
) : GitHubRepository {
    override suspend fun getSearch(search: String, page: Int): GitHubSearch = pokemonApiService.getSearch(search, page)
}