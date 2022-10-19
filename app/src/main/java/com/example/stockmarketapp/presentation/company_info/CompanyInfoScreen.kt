package com.example.stockmarketapp.presentation.company_info

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stockmarketapp.ui.theme.Pink
import com.example.stockmarketapp.ui.theme.Purple
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import java.time.LocalDateTime
import java.util.*

@Composable
@Destination
fun CompanyInfoScreen(
    symbol : String,
    viewModel: CompanyInfoViewModel = hiltViewModel()
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state
    
    if (state.errorCompanyInfo == null) {
        SwipeRefresh(state = swipeRefreshState, onRefresh = {
            viewModel.onEvent(CompanyInfoEvent.Refresh)
        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Purple)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()) //needed for refreshing
            ) {
                //as soon company is not null, this will show
                state.company?.let { company ->
                    Text(
                        text = company.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = company.symbol,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (company.industry != "") "Industry: ${company.industry}" else "",
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Country: ${company.country}",
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = company.description,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    //if we have entries in the intraday info list, we want to show the stock chart
                    if (state.stockInfos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Market Summary"
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        StockChart(
                            infos = state.stockInfos,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp) //we need to pass the height for the canvas
                                .align(CenterHorizontally)
                        )
                    } else if (state.errorIntradayInfo != null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                            Text(
                                text = "${state.errorIntradayInfo}",
                                color = MaterialTheme.colors.error
                            )
                        }
                    }
                }
            }
        }
    }

  Box(
      modifier = Modifier
          .fillMaxSize(),
      contentAlignment = Center
  ) {
      if (state.isLoading) {
          CircularProgressIndicator()
      }
      if (state.errorCompanyInfo != null) {
          Text(
              text = state.errorCompanyInfo,
              color = MaterialTheme.colors.error
          )
      }
  }
}
