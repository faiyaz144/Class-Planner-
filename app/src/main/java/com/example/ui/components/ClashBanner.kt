package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ClassRoutine

@Composable
fun ClashBanner(
  clashes: List<Pair<ClassRoutine, ClassRoutine>>,
  modifier: Modifier = Modifier
) {
  if (clashes.isEmpty()) return

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("clash_warning_banner"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = Color(0xFFFEF2F2) // Light red
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.Top
    ) {
      Icon(
        imageVector = Icons.Default.Warning,
        contentDescription = "Warning",
        tint = Color(0xFFDC2626),
        modifier = Modifier.size(24.dp)
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(
          text = "Schedule Conflict Detected (${clashes.size})",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF991B1B)
        )
        Text(
          text = "The following classes have overlapping time slots:",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF7F1D1D),
          modifier = Modifier.padding(vertical = 2.dp)
        )

        clashes.take(3).forEach { (c1, c2) ->
          Text(
            text = "• ${c1.dayOfWeek}: ${c1.courseCode} (${c1.startTime}-${c1.endTime}) ↔ ${c2.courseCode} (${c2.startTime}-${c2.endTime})",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFB91C1C),
            modifier = Modifier.padding(top = 2.dp)
          )
        }

        if (clashes.size > 3) {
          Text(
            text = "+ ${clashes.size - 3} more conflict(s)",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFB91C1C),
            modifier = Modifier.padding(top = 2.dp)
          )
        }
      }
    }
  }
}
