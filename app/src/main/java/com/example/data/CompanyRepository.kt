package com.example.data

import com.example.data.network.ScreenerQueryDto
import com.example.data.network.ScreenerResultDto
import com.example.data.network.StockApiService
import com.example.data.network.StockOverviewDto
import kotlinx.coroutines.flow.Flow

class CompanyRepository(
    private val companyDao: CompanyDao,
    private val apiService: StockApiService
) {
    val allCompanies: Flow<List<Company>> = companyDao.getAllCompanies()

    fun getCompany(isin: String): Flow<Company?> {
        return companyDao.getCompanyByIsin(isin)
    }

    suspend fun insert(company: Company) {
        companyDao.insertCompany(company)
    }

    suspend fun insertAll(companies: List<Company>) {
        companyDao.insertCompanies(companies)
    }

    // Network Calls
    suspend fun fetchStockFundamentals(symbol: String): StockOverviewDto {
        return apiService.getFundamentals(symbol)
    }

    suspend fun runScreener(query: ScreenerQueryDto): List<ScreenerResultDto> {
        return apiService.runScreener(query)
    }
}
