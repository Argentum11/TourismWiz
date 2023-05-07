package TourismWiz.TourismWiz.ui.screens

import TourismWiz.TourismWiz.model.Restaurant
import TourismWiz.TourismWiz.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RestaurantScreen(
    restaurantUiState: RestaurantUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (restaurantUiState){
        is RestaurantUiState.Loading -> LoadingScreen(modifier)
        is RestaurantUiState.Error -> ErrorScreen(retryAction, modifier)
        is RestaurantUiState.Success -> RestaurantGridScreen(restaurants = restaurantUiState.restaurants, modifier)
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier){
    Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()){
        Text("TODO loading")
    }
}
@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier){
    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
            ){
        Text(stringResource(R.string.search_failed))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun RestaurantGridScreen(restaurants: List<Restaurant>, modifier: Modifier  = Modifier){
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items = restaurants, key = {restaurant -> restaurant.RestaurantID}) {restaurant ->
            RestaurantCard(restaurant)
        }
    }
}

@Composable
fun RestaurantCard(restaurant: Restaurant, modifier: Modifier = Modifier){
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = 8.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.default),
                contentDescription = "android_developers",
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.8f))
            ) {
                Text(
                    text = restaurant.RestaurantName,
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = restaurant.Description,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
