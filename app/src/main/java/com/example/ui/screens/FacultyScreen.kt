package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FacultyMember
import com.example.ui.theme.AccentLilac
import com.example.ui.theme.DarkAppBackground
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryGray

@Composable
fun FacultyScreen(
  facultyList: List<FacultyMember>,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkAppBackground)
      .testTag("faculty_screen_list"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
          text = "Faculty Directory",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryWhite
        )
        Text(
          text = "Course instructors, departments & office rooms",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryGray
        )
      }
    }

    if (facultyList.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = AccentLilac, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("No faculty recorded yet", fontWeight = FontWeight.Bold, color = TextPrimaryWhite)
            Text("Add courses with instructor names or scan your timetable to populate faculty contacts.", style = MaterialTheme.typography.bodySmall, color = TextSecondaryGray)
          }
        }
      }
    } else {
      items(facultyList, key = { it.id }) { faculty ->
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AccentLilac.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = faculty.name.take(2).uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = AccentLilac
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = faculty.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimaryWhite
              )
              Text(
                text = "${faculty.designation} · ${faculty.department}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryGray
              )

              if (faculty.roomOffice.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.LocationOn, contentDescription = null, tint = AccentLilac, modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(text = faculty.roomOffice, style = MaterialTheme.typography.bodySmall, color = TextSecondaryGray)
                }
              }

              if (faculty.courses.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  faculty.courses.forEach { code ->
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = Color(0xFF222336)
                    ) {
                      Text(
                        text = code,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentLilac,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
