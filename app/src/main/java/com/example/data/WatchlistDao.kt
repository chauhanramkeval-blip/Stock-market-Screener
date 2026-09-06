package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT c.* FROM companies c INNER JOIN watchlist w ON c.isin = w.company_isin ORDER BY w.added_at DESC")
    fun getWatchlistedCompanies(): Flow<List<Company>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE company_isin = :isin)")
    fun isWatchlisted(isin: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchlist(watchlist: Watchlist)

    @Query("DELETE FROM watchlist WHERE company_isin = :isin")
    suspend fun removeFromWatchlist(isin: String)
}
