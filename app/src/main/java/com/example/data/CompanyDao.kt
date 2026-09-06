package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {
    @Query("SELECT * FROM companies ORDER BY nse_symbol ASC")
    fun getAllCompanies(): Flow<List<Company>>

    @Query("SELECT * FROM companies WHERE isin = :isin")
    fun getCompanyByIsin(isin: String): Flow<Company?>

    @Query("SELECT * FROM companies WHERE nse_symbol LIKE '%' || :searchQuery || '%' OR company_name LIKE '%' || :searchQuery || '%' ORDER BY nse_symbol ASC")
    fun searchCompanies(searchQuery: String): Flow<List<Company>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompany(company: Company)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanies(companies: List<Company>)

    @Query("DELETE FROM companies")
    suspend fun deleteAllCompanies()
}
