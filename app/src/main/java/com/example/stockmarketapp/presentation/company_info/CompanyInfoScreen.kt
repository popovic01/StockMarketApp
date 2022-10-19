package com.example.stockmarketapp.presentation.company_info

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stockmarketapp.ui.theme.Blue
import com.example.stockmarketapp.ui.theme.Purple
import com.example.stockmarketapp.ui.theme.TextWhite
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination

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
                    Spacer(modifier = Modifier.height(8.dp))
                    Address(address = company.address)
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

@Composable
fun Address(address: String) {
    //creating an annotated string
    val annotatedLinkString = buildAnnotatedString {

        //creating a string to display in the Text
        val str = "Address: $address - click here to see location"

        val startIndex = str.indexOf("click")
        val endIndex = startIndex + 26

        append(str)
        addStyle(
            style = SpanStyle(
                color = Color(0xffDFD8D6)
            ), start = 0, end = startIndex
        )
        addStyle(
            style = SpanStyle(
                color = Blue,
                textDecoration = TextDecoration.Underline
            ), start = startIndex, end = endIndex
        )
    }

    val navigationIntentUri: Uri = Uri.parse("geo:0,0?q=$address")
    val mapIntent = Intent(Intent.ACTION_VIEW, navigationIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps") //to use google maps
    val context = LocalContext.current

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ClickableText(
            text = annotatedLinkString,
            onClick = {
                startActivity(context, mapIntent, null)
            }
        )
    }
}
