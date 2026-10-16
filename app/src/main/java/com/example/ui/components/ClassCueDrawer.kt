package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentLilac
import com.example.ui.theme.DarkAppBackground
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryGray

@Composable
fun ClassCueDrawerContent(
  studentName: String,
  totalClassesWeekly: Int,
  attendanceCount: Int,
  onOpenAttendance: () -> Unit,
  onNewSemester: () -> Unit,
  onOpenBreaks: () -> Unit,
  onOpenExams: () -> Unit,
  onOpenShare: () -> Unit,
  onImportRoutine: () -> Unit,
  onScanExamRoutine: () -> Unit,
  onOpenSettings: () -> Unit,
  onCloseDrawer: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxHeight()
      .width(310.dp)
      .background(Color(0xFF0C0D15))
      .padding(horizontal = 20.dp, vertical = 28.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      // Header: Purple "C" App Icon + Student Name (Image 2)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 24.dp)
      ) {
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF121325)),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.app_icon_logo),
            contentDescription = "Class Planner Logo",
            modifier = Modifier
              .size(52.dp)
              .clip(RoundedCornerShape(16.dp))
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = studentName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryWhite,
            fontSize = 22.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$totalClassesWeekly classes a week",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryGray,
            fontSize = 13.sp
          )
        }
      }

      // SECTION 1: YOUR ROUTINE
      DrawerSectionHeader(title = "YOUR ROUTINE")

      DrawerMenuItem(
        icon = Icons.Default.Person,
        label = "Attendance",
        badge = "$attendanceCount",
        onClick = {
          onCloseDrawer()
          onOpenAttendance()
        },
        testTag = "drawer_attendance"
      )

      DrawerMenuItem(
        icon = Icons.Default.Refresh,
        label = "New semester",
        onClick = {
          onCloseDrawer()
          onNewSemester()
        },
        testTag = "drawer_new_semester"
      )

      Spacer(modifier = Modifier.height(18.dp))

      // SECTION 2: TIME OFF
      DrawerSectionHeader(title = "TIME OFF")

      DrawerMenuItem(
        icon = Icons.Default.BeachAccess,
        label = "Breaks & holidays",
        onClick = {
          onCloseDrawer()
          onOpenBreaks()
        },
        testTag = "drawer_breaks"
      )

      DrawerMenuItem(
        icon = Icons.Default.EventNote,
        label = "Exam periods",
        onClick = {
          onCloseDrawer()
          onOpenExams()
        },
        testTag = "drawer_exams"
      )

      DrawerMenuItem(
        icon = Icons.Default.NotificationsOff,
        label = "Pause reminders",
        onClick = {
          onCloseDrawer()
          onOpenSettings()
        },
        testTag = "drawer_reminders"
      )

      Spacer(modifier = Modifier.height(18.dp))

      // SECTION 3: SHARE
      DrawerSectionHeader(title = "SHARE")

      DrawerMenuItem(
        icon = Icons.Default.Share,
        label = "Share my routine",
        onClick = {
          onCloseDrawer()
          onOpenShare()
        },
        testTag = "drawer_share"
      )

      DrawerMenuItem(
        icon = Icons.Default.FileDownload,
        label = "Import a routine",
        onClick = {
          onCloseDrawer()
          onImportRoutine()
        },
        testTag = "drawer_import"
      )

      DrawerMenuItem(
        icon = Icons.Default.EventNote,
        label = "Import exam routine",
        onClick = {
          onCloseDrawer()
          onScanExamRoutine()
        },
        testTag = "drawer_import_exam"
      )

      Spacer(modifier = Modifier.height(18.dp))

      // SECTION 4: APP
      DrawerSectionHeader(title = "APP")

      DrawerMenuItem(
        icon = Icons.Default.Settings,
        label = "Settings",
        onClick = {
          onCloseDrawer()
          onOpenSettings()
        },
        testTag = "drawer_settings"
      )
    }

    // Bottom Pro Card: "ClassCue Plus · No ads · every theme & font" (Image 2)
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = Color(0xFF1B1C2A),
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 20.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Star,
              contentDescription = null,
              tint = AccentLilac,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = "ClassCue Plus",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = TextPrimaryWhite
            )
            Text(
              text = "No ads · every theme & font",
              fontSize = 11.sp,
              color = TextSecondaryGray
            )
          }
        }

        Icon(
          Icons.AutoMirrored.Filled.ArrowForwardIos,
          contentDescription = null,
          tint = TextSecondaryGray,
          modifier = Modifier.size(14.dp)
        )
      }
    }
  }
}

@Composable
fun DrawerSectionHeader(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.labelSmall,
    fontWeight = FontWeight.Bold,
    color = TextMutedGray,
    letterSpacing = 1.sp,
    modifier = Modifier.padding(vertical = 8.dp)
  )
}

@Composable
fun DrawerMenuItem(
  icon: ImageVector,
  label: String,
  badge: String? = null,
  onClick: () -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(vertical = 10.dp, horizontal = 4.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFF161726)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          icon,
          contentDescription = null,
          tint = TextSecondaryGray,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = TextPrimaryWhite,
        fontSize = 15.sp
      )
    }

    if (badge != null) {
      Surface(
        shape = CircleShape,
        color = Color(0xFF222336),
        modifier = Modifier.size(24.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = badge,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryGray
          )
        }
      }
    }
  }
}
