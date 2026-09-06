package com.example.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StockApiService {
    @GET("api/v1/stocks/{symbol}/fundamentals")
    suspend fun getFundamentals(@Path("symbol") symbol: String): StockOverviewDto

    @POST("api/v1/screener/filter")
    suspend fun runScreener(@Body query: ScreenerQueryDto): List<ScreenerResultDto>
}
