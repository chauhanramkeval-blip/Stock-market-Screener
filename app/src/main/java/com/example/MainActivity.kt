package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import com.example.data.AppDatabase
import com.example.data.Company
import com.example.data.CompanyRepository
import com.example.data.network.ApiClient
import com.example.ui.CompanyViewModel
import com.example.ui.CompanyViewModelFactory
import com.example.ui.components.CompanyDashboardCard
import com.example.ui.components.GridStockCard
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Simulated Data Model
data class MarketTicker(
    val company: Company,
    var price: Double,
    var changePercent: Double
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val context = LocalContext.current
        val database = remember { AppDatabase.getDatabase(context) }
        val repository = remember { CompanyRepository(database.companyDao(), database.watchlistDao(), ApiClient.stockApiService) }
        val factory = remember { CompanyViewModelFactory(repository) }
        val viewModel: CompanyViewModel = viewModel(factory = factory)

        DashboardScreen(viewModel)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: CompanyViewModel) {
    // Generate initial simulated data
    val initialGainers = remember {
        listOf(
            MarketTicker(Company("INE002A01018", "RELIANCE", "Reliance Ind", "Energy", "Oil"), 2950.0, 2.4),
            MarketTicker(Company("INE467B01029", "TCS", "Tata Consultancy", "IT", "Software"), 4120.5, 1.8),
            MarketTicker(Company("INE154A01025", "ITC", "ITC Ltd", "FMCG", "Tobacco"), 450.2, 3.1),
            MarketTicker(Company("INE090A01021", "ICICIBANK", "ICICI Bank", "Financials", "Banking"), 1100.8, 1.2)
        )
    }
    
    val initialLosers = remember {
        listOf(
            MarketTicker(Company("INE040A01034", "HDFCBANK", "HDFC Bank", "Financials", "Banking"), 1430.2, -1.5),
            MarketTicker(Company("INE009A01021", "INFY", "Infosys Ltd", "IT", "Software"), 1600.0, -2.1),
            MarketTicker(Company("INE238A01034", "AXISBANK", "Axis Bank", "Financials", "Banking"), 1050.4, -0.8),
            MarketTicker(Company("INE062A01020", "SBIN", "State Bank", "Financials", "Banking"), 750.6, -1.2)
        )
    }

    // Seed database for Search
    val allCompanies by viewModel.allCompanies.collectAsState()
    LaunchedEffect(allCompanies.isEmpty()) {
        if (allCompanies.isEmpty()) {
            initialGainers.forEach { viewModel.addCompany(it.company) }
            initialLosers.forEach { viewModel.addCompany(it.company) }
        }
    }

    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    var gainers by remember { mutableStateOf(initialGainers) }
    var losers by remember { mutableStateOf(initialLosers) }

    // Filter State
    var selectedSector by remember { mutableStateOf("All") }
    val sectors = listOf("All", "Financials", "IT", "Energy", "FMCG")
    
    val filteredGainers = remember(gainers, selectedSector) {
        if (selectedSector == "All") gainers else gainers.filter { it.company.sector == selectedSector }
    }
    
    val filteredLosers = remember(losers, selectedSector) {
        if (selectedSector == "All") losers else losers.filter { it.company.sector == selectedSector }
    }

    // Pull-to-Refresh State
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Market Discovery") })
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true
                    // Simulate network fetch delay
                    delay(1200)
                    
                    // Perturb the simulated metrics to mimic real-time updates
                    gainers = gainers.map { 
                        it.copy(
                            price = it.price * (1 + Random.nextDouble(-0.01, 0.02)),
                            changePercent = it.changePercent + Random.nextDouble(-0.5, 0.5)
                        )
                    }
                    losers = losers.map { 
                        it.copy(
                            price = it.price * (1 + Random.nextDouble(-0.02, 0.01)),
                            changePercent = it.changePercent + Random.nextDouble(-0.5, 0.5)
                        )
                    }
                    isRefreshing = false
                }
            },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Search Bar
                item(span = { GridItemSpan(maxLineSpan) }) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        placeholder = { Text("Search by symbol or name...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        singleLine = true
                    )
                }

                if (searchQuery.isNotBlank()) {
                    // Search Results
                    items(searchResults, span = { GridItemSpan(maxLineSpan) }) { company ->
                        CompanyDashboardCard(
                            company = company,
                            onClick = {}
                        )
                    }
                } else {
                    // Filter Chips Section
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sectors.forEach { sector ->
                                androidx.compose.material3.FilterChip(
                                    selected = selectedSector == sector,
                                    onClick = { selectedSector = sector },
                                    label = { Text(sector) }
                                )
                            }
                        }
                    }

                    // Top Gainers Section
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Top Gainers",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                    }
                    items(filteredGainers) { ticker ->
                        GridStockCard(
                            company = ticker.company,
                            currentPrice = ticker.price,
                            changePercent = ticker.changePercent,
                            onClick = {}
                        )
                    }

                    // Top Losers Section
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Top Losers",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp, top = 24.dp)
                        )
                    }
                    items(filteredLosers) { ticker ->
                        GridStockCard(
                            company = ticker.company,
                            currentPrice = ticker.price,
                            changePercent = ticker.changePercent,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}
