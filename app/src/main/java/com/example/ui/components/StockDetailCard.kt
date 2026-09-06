package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Company
import com.example.data.network.FinancialStatementDto
import com.example.data.network.StockOverviewDto
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen

@Composable
fun StockDetailCard(
    company: Company,
    overview: StockOverviewDto?,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(), // Smooth expansion animation
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Row (Always Visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = company.nseSymbol,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = company.companyName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }

                if (overview != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${"%.2f".format(overview.currentPrice)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Expanded Content (P&L Trends & Ratios)
            if (expanded && overview != null) {
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Key Ratios
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem(label = "P/E", value = "%.2f".format(overview.peRatio))
                        MetricItem(label = "Mkt Cap", value = "₹${"%.0f".format(overview.marketCap)}Cr")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Historical Financials (₹ Cr)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!overview.financials.isNullOrEmpty()) {
                        FinancialsTable(financials = overview.financials)
                    } else {
                        Text(
                            text = "No historical data available.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FinancialsTable(financials: List<FinancialStatementDto>) {
    val scrollState = rememberScrollState()
    
    // Sort financials by year ascending
    val sortedFinancials = financials.sortedBy { it.year }

    Row(modifier = Modifier.fillMaxWidth()) {
        // Frozen First Column (Labels)
        Column(
            modifier = Modifier
                .width(100.dp)
                .background(MaterialTheme.colorScheme.surface),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Year", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(text = "Revenue", style = MaterialTheme.typography.labelMedium)
            Text(text = "Net Profit", style = MaterialTheme.typography.labelMedium)
            Text(text = "Assets", style = MaterialTheme.typography.labelMedium)
            Text(text = "Liabilities", style = MaterialTheme.typography.labelMedium)
        }

        // Scrollable Data Columns
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            sortedFinancials.forEach { statement ->
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = statement.year.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "%.0f".format(statement.revenue),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "%.0f".format(statement.netProfit),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (statement.netProfit >= 0) BullishGreen else BearishRed
                    )
                    Text(
                        text = "%.0f".format(statement.totalAssets),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "%.0f".format(statement.totalLiabilities),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
