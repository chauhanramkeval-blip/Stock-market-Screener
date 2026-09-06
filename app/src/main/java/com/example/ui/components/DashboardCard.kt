package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.Company
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDashboardCard(
    company: Company,
    peRatio: Double? = null,
    roce: Double? = null,
    currentPrice: Double? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Symbol and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = company.nseSymbol,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (currentPrice != null) {
                    Text(
                        // Format to 2 decimal places for Indian Rupees
                        text = "₹${"%.2f".format(currentPrice)}",
                        style = MaterialTheme.typography.labelLarge, // Tabular Monospace
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Sub-header: Company Name
            Text(
                text = company.companyName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    label = "Sector",
                    value = company.sector ?: "N/A",
                    isMonospace = false
                )
                
                if (peRatio != null) {
                    MetricItem(
                        label = "P/E Ratio",
                        value = "%.2f".format(peRatio),
                        isMonospace = true
                    )
                }
                
                if (roce != null) {
                    MetricItem(
                        label = "ROCE",
                        value = "${"%.2f".format(roce)}%",
                        isMonospace = true,
                        // Apply semantic financial colors based on ROCE value (e.g. >15% is good)
                        valueColor = if (roce >= 15.0) BullishGreen else if (roce < 10.0) BearishRed else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun MetricItem(
    label: String,
    value: String,
    isMonospace: Boolean = true,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            // Use labelLarge for tabular monospace numbers, bodyLarge for standard text
            style = if (isMonospace) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyLarge,
            color = valueColor
        )
    }
}
