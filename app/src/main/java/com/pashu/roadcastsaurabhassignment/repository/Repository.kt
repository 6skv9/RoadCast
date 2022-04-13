package com.pashu.roadcastsaurabhassignment.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pashu.roadcastsaurabhassignment.ApiClient.ApiClient
import com.pashu.roadcastsaurabhassignment.ApiClient.ApiInterface
import com.pashu.roadcastsaurabhassignment.data.EntriesResponse
import java.io.*
import java.lang.reflect.Type

object Repository {

    lateinit var entries: EntriesResponse

    suspend fun getEntries(){
        ApiClient.getApiService<ApiInterface>().getEntries().let {
            return if(it.isSuccessful) {
                entries = it.body()!!
            }else {
                throw Exception("Failed in API")
            }
        }
    }
}