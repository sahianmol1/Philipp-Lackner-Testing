package com.androiddevs.shoppinglisttestingyt.data.remote

import retrofit2.http.GET
import com.androiddevs.shoppinglisttestingyt.data.remote.responses.ImageResponse
import retrofit2.Response
import retrofit2.http.Query

interface PixabayAPI {

    @GET("/api/")
    suspend fun searchForImage(
        @Query("q") searchQuery: String,
        @Query("key") apiKey: String = "16925861-b951484d287d0c419e6e9ef15"
    ) : Response<ImageResponse>
}