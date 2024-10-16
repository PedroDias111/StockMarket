package com.example.stockmarket.presentation.company_listings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.Delete
import com.example.stockmarket.R
import com.example.stockmarket.domain.model.CompanyInfo
import com.example.stockmarket.domain.model.CompanyListingModel
import com.example.stockmarket.presentation.destinations.CompanyInfoScreenDestination
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(start = true)
fun CompanyScreen (
    navigator: DestinationsNavigator,
    viewModel: CompanyListingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
    ){
        OutlinedTextField(
            value = state.value.searchQuery,
            onValueChange = {
                viewModel.onEvent(event = CompanyListingsEvents.OnSearchQueryChange(it))
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {Text(text = "Search...")},
            maxLines = 1,
            singleLine = true
        )

        val pullToRefreshState = rememberPullToRefreshState()

        Scaffold(
            modifier = Modifier.pullToRefresh(
                state = pullToRefreshState,
                isRefreshing = state.value.isRefreshing,
                onRefresh = {viewModel.onEvent(CompanyListingsEvents.Refresh)}
            ),
        ) {
            Box(
                Modifier.padding(it)
            ){
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (!state.value.isRefreshing) {
                        items(state.value.companies.size) { i ->
                            val company = state.value.companies[i]
                            CompanyItem(
                                company = company,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navigator.navigate(
                                            CompanyInfoScreenDestination(company.symbol)
                                        )
                                    }
                                    .padding(16.dp)
                            )

                            if (i < state.value.companies.size) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
                if (state.value.isRefreshing) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        progress = { pullToRefreshState.distanceFraction }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

