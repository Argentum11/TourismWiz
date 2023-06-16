package TourismWiz.TourismWiz.ui.screens

import TourismWiz.TourismWiz.R
import TourismWiz.TourismWiz.data.CommentAdd
import TourismWiz.TourismWiz.data.MyUser
import TourismWiz.TourismWiz.data.darkBlue
import TourismWiz.TourismWiz.data.lightBlue
import TourismWiz.TourismWiz.model.Hotel
import TourismWiz.TourismWiz.model.commentList
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun HotelScreen(
    hotelUiState: HotelUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    keyword: String
) {
    Log.e("qpyt", keyword)
    val navController = rememberNavController()
    var hotels by remember { mutableStateOf(mutableListOf<Hotel>()) }
    var isShow by rememberSaveable { mutableStateOf(false) }
    var selectedHotelId by remember { mutableStateOf("") }
    var first_tap by remember { mutableStateOf(true) }
    val derivedSearchQueryForHotel = remember(keyword) {
        derivedStateOf { keyword }
    }
    when(isShow){
        true->{
            if(first_tap){
                first_tap=false
                isShow=!isShow
            }
            when (hotelUiState) {
                is HotelUiState.Loading -> LoadingScreen(modifier)
                is HotelUiState.Error -> ErrorScreen(retryAction, modifier)
                is HotelUiState.Success -> {
                    hotels = MyUser.hotelList
                    NavHost(navController = navController, startDestination = "hotelGrid") {
                        composable("hotelGrid") {
                            Column {
                                LoginScreen(field = "Hotel", myItem = null,isShow || first_tap, saveList = {
                                    isShow = isShow == false
                                })
                                HotelGridScreen(
                                    hotels = hotels,
                                    modifier,
                                    onItemClick = { hotel ->
                                        isShow=false
                                        first_tap=true
                                        selectedHotelId = hotel.HotelID
                                        navController.navigate("hotelDetail")
                                    },
                                    keyword = derivedSearchQueryForHotel.value
                                )
                            }
                        }
                        composable("hotelDetail") {
                            val restaurant =
                                hotelUiState.hotels.find { it.HotelID == selectedHotelId }
                            restaurant?.let { HotelDetailScreen(hotel = it) }
                        }
                    }
                }
            }
        }
        false->{
            when (hotelUiState) {
                is HotelUiState.Loading -> LoadingScreen(modifier)
                is HotelUiState.Error -> ErrorScreen(retryAction, modifier)
                is HotelUiState.Success -> {
                    hotels = hotelUiState.hotels as MutableList<Hotel>
                    NavHost(navController = navController, startDestination = "hotelGrid") {
                        composable("hotelGrid") {
                            Column {
                                LoginScreen(field = "Hotel", myItem = null,isShow || first_tap, saveList = {
                                    isShow = isShow == false
                                })
                                HotelGridScreen(
                                    hotels = hotels,
                                    modifier,
                                    onItemClick = { hotel ->
                                        isShow=false
                                        first_tap=true
                                        selectedHotelId = hotel.HotelID
                                        navController.navigate("hotelDetail")
                                    },
                                    keyword = derivedSearchQueryForHotel.value
                                )
                            }
                        }
                        composable("hotelDetail") {
                            val restaurant =
                                hotelUiState.hotels.find { it.HotelID == selectedHotelId }
                            restaurant?.let { HotelDetailScreen(hotel = it) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HotelGridScreen(
    hotels: List<Hotel>,
    modifier: Modifier = Modifier,
    onItemClick: (Hotel) -> Unit,
    keyword: String
) {
    val filteredHotels = remember { mutableStateListOf<Hotel>() }
    var total by remember { mutableStateOf(hotels.size) }

    Column(modifier = modifier.fillMaxWidth()) {
        when(total) {
            0 -> NoResult()
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    filteredHotels.clear()
                    filteredHotels.addAll(
                        hotels.filter { hotel ->
                            hotel.HotelName.contains(keyword, ignoreCase = true) ||
                                    hotel.Description?.contains(
                                        keyword,
                                        ignoreCase = true
                                    ) == true
                        }
                    )
                    items(items = filteredHotels, key = { hotel -> hotel.HotelID }) { hotel ->
                        HotelCard(hotel, onItemClick = onItemClick)
                    }
                }
            }
        }
    }
}

@Composable
fun HotelCard(
    hotel: Hotel, modifier: Modifier = Modifier
    ,onItemClick: (Hotel) -> Unit
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onItemClick(hotel) }
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
                    text = hotel.Description?.take(80)
                        ?: stringResource(R.string.default_hotel_description),
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

@Composable
fun HotelDetailScreen(hotel: Hotel) {
    val commentList = commentList(id = hotel.HotelID)
    val context = LocalContext.current
    val phoneNumber = "0" + hotel.Phone.replace("-", "").removePrefix("886")
    val phoneNumberClick: () -> Unit = {
        val phoneUri = "tel:${phoneNumber}"
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneUri))
        context.startActivity(intent)
    }
    val addressClick: () -> Unit = {
        val mapUri = Uri.parse("geo:0,0?q=${hotel.Address}")
        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
        mapIntent.setPackage("com.google.android.apps.maps") // 指定使用 Google 地图应用
        context.startActivity(mapIntent)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            LoginScreen(field = "Hotel", myItem = hotel,false, saveList = {})
        }
        item {
            CommentAdd(id = hotel.HotelID)
        }
        item {
            ImageDisplay(hotel.Picture.PictureUrl1)
            Text(
                text = hotel.HotelName,
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Cyan
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 16.dp)
                    .background(Color(0xFFE0E0E0))
                    .padding(16.dp)
            ) {

                Text(
                    text = stringResource(R.string.related_info) + " : ",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .clickable(onClick = addressClick)
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.maps),
                        contentDescription = "Location icon",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp)
                    )
                    val fontSize = if (hotel.Address.length > 20) 16.sp else 24.sp
                    Text(
                        text = hotel.Address,
                        fontSize = fontSize
                    )
                }

                Row(
                    modifier = Modifier
                        .clickable(onClick = phoneNumberClick)
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.call),
                        contentDescription = "Phone icon",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = phoneNumber,
                        fontSize = 24.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                ) {
                    if (hotel.Spec != null) {
                    Image(
                        painter = painterResource(R.drawable.open),
                        contentDescription = "Open sign icon",//TODO
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp)
                    )
                        Text(
                            text = hotel.Spec,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 16.dp)
                    .background(Color(0xFFE0E0E0))
                    .padding(16.dp)
            ) {

                Text(
                    text = stringResource(R.string.detailed_description) + " : ",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = hotel.Description ?: stringResource(id = R.string.default_hotel_description),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(Color(0xFFE0E0E0))
                        .padding(16.dp)
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.data_update_date) + " : " + hotel.UpdateTime,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .background(Color(0xFFE0E0E0))
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(8.dp)),
                color = Color.Black
            )
        }
        items(commentList) { comment ->
            CommentDisplay(comment = comment)
        }
    }
}