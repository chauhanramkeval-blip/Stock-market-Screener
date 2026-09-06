package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FinancialStatementDto(
    val year: Int,
    val revenue: Double,
    @Json(name = "net_profit") val netProfit: Double,
    @Json(name = "total_assets") val totalAssets: Double,
    @Json(name = "total_liabilities") val totalLiabilities: Double
)

@JsonClass(generateAdapter = true)
data class StockOverviewDto(
    val isin: String,
    val symbol: String,
    @Json(name = "company_name") val companyName: String,
    @Json(name = "current_price") val currentPrice: Double,
    @Json(name = "pe_ratio") val peRatio: Double,
    @Json(name = "market_cap") val marketCap: Double,
    val financials: List<FinancialStatementDto>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class ScreenerQueryDto(
    @Json(name = "pe_min") val peMin: Double? = null,
    @Json(name = "pe_max") val peMax: Double? = null,
    @Json(name = "min_roce") val minRoce: Double? = null,
    @Json(name = "max_debt_to_equity") val maxDebtToEquity: Double? = null,
    val sector: String? = null,
    val limit: Int = 50,
    val offset: Int = 0
)

@JsonClass(generateAdapter = true)
data class ScreenerResultDto(
    @Json(name = "nse_symbol") val nseSymbol: String,
    @Json(name = "close_price") val closePrice: Double,
    @Json(name = "pe_ratio") val peRatio: Double,
    val roce: Double
)
