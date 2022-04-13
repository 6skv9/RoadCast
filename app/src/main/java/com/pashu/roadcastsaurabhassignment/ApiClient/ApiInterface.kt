package com.pashu.roadcastsaurabhassignment.ApiClient

import com.pashu.roadcastsaurabhassignment.data.EntriesResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET("entries")
    suspend fun getEntries(): Response<EntriesResponse>

}