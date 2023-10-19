package com.example.hadedahunter

interface DataFetchCallback {
    fun onDataFetched(birds: List<Bird>)
}