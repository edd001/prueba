package com.example.domain.models

data class MoviePages(
    val page: Int,
    val results: ArrayList<Movie>,
    val total_pages: Int,
    val total_results: Int
)