package TourismWiz.TourismWiz.ui.screens

import TourismWiz.TourismWiz.R
import TourismWiz.TourismWiz.data.darkBlue
import TourismWiz.TourismWiz.data.lightBlue
import TourismWiz.TourismWiz.model.Hotel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun HotelScreen(
    hotelUiState: HotelUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (hotelUiState){
        is HotelUiState.Loading -> LoadingScreen(modifier)
        is HotelUiState.Error -> ErrorScreen(retryAction, modifier)
        is HotelUiState.Success -> HotelGridScreen(hotels = hotelUiState.hotels, modifier)
    }
}

@Composable
fun HotelGridScreen(hotels: List<Hotel>, modifier: Modifier  = Modifier){
    val filteredHotels = remember { mutableStateListOf<Hotel>() }
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        TextField(
            value = searchQuery,
            onValueChange = { query -> searchQuery = query },
            label = { Text(stringResource(id = R.string.keyword)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                        )
                    }
                }
            }
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            filteredHotels.clear()
            filteredHotels.addAll(
                hotels.filter { hotel ->
                    hotel.HotelName.contains(searchQuery, ignoreCase = true) ||
                            hotel.Description?.contains(searchQuery, ignoreCase = true) == true
                }
            )
            items(items = filteredHotels, key = { hotel -> hotel.HotelID }) { hotel ->
                HotelCard(hotel)
            }
        }
    }
}

@Composable
fun HotelCard(hotel: Hotel, modifier: Modifier = Modifier
              //,onItemClick: (Hotel) -> Unit
){
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            //.clickable { onItemClick(hotel)}
                ,
        elevation = 8.dp,
        backgroundColor = lightBlue,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White)
            ) {
                ImageDisplay(hotel.Picture.PictureUrl1)
                Text(
                    text = hotel.HotelName,
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = darkBlue
                )

                Text(
                    text = hotel.Description?.take(80) ?: stringResource(R.string.default_hotel_description),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }
}