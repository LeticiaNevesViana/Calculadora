package com.example.calculator

import android.database.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface CalculateApi {
    @GET("total")
    fun getTotalValues(): Single<ApiResponse>
}

//interface CalculateApi {
//@GET("/calculate/total")
// suspend fun getTotalValues(): ApiResponse
//}