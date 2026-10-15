package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.theme.CuteDarkText
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.theme.PastelPrimary
import com.example.ui.theme.QuicksandFontFamily
import com.example.ui.viewmodel.DayTabItem
import java.time.LocalDate

@Composable
fun WeekDaySelector(
    days: List<DayTabItem>,
    onDayClick: (LocalDate) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEach { item ->
                val isSelected = item.isSelected
                val isToday = item.isToday

                val bgColor = when {
                    isSelected -> PastelPrimary
                    isToday -> PastelPinkBackground
                    else -> Color.Transparent
                }

                val textColor = when {
                    isSelected -> Color.White
                    isToday -> PastelPrimary
                    else -> CuteDarkText
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = bgColor,
                    border = if (isToday && !isSelected) BorderStroke(1.5.dp, PastelPrimary.copy(alpha = 0.5f)) else null,
                    modifier = Modifier
                        .size(42.dp, 54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onDayClick(item.date) }
                        .testTag("day_tab_${item.dayLetter}_${item.dayNumber}")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = item.dayLetter,
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = textColor
                        )
                        Text(
                            text = item.dayNumber.toString(),
                            fontFamily = QuicksandFontFamily,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}
