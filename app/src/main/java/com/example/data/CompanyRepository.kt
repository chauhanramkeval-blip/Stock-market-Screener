package com.example.data

import com.example.data.network.ScreenerQueryDto
import com.example.data.network.ScreenerResultDto
import com.example.data.network.StockApiService
import com.example.data.network.StockOverviewDto
import kotlinx.coroutines.flow.Flow

class CompanyRepository(
    private val companyDao: CompanyDao,
    private val watchlistDao: WatchlistDao,
    private val apiService: StockApiService
) {
    val allCompanies: Flow<List<Company>> = companyDao.getAllCompanies()
    val watchlistedCompanies: Flow<List<Company>> = watchlistDao.getWatchlistedCompanies()

    fun searchCompanies(query: String): Flow<List<Company>> {
        return companyDao.searchCompanies(query)
    }

    fun getCompany(isin: String): Flow<Company?> {
        return companyDao.getCompanyByIsin(isin)
    }

    suspend fun insert(company: Company) {
        companyDao.insertCompany(company)
    }

    suspend fun insertAll(companies: List<Company>) {
        companyDao.insertCompanies(companies)
    }

    // Watchlist Actions
    fun isWatchlisted(isin: String): Flow<Boolean> = watchlistDao.isWatchlisted(isin)

    suspend fun toggleWatchlist(isin: String, isWatchlisted: Boolean) {
        if (isWatchlisted) {
            watchlistDao.removeFromWatchlist(isin)
        } else {
            watchlistDao.addToWatchlist(Watchlist(companyIsin = isin))
        }
    }

    // Network Calls
    suspend fun fetchStockFundamentals(symbol: String): StockOverviewDto {
        return apiService.getFundamentals(symbol)
    }

    suspend fun runScreener(query: ScreenerQueryDto): List<ScreenerResultDto> {
        return apiService.runScreener(query)
    }
}
