package TourismWiz.TourismWiz.ui.screens

import TourismWiz.TourismWiz.R
import TourismWiz.TourismWiz.data.darkBlue
import TourismWiz.TourismWiz.data.lightBlue
import TourismWiz.TourismWiz.model.ScenicSpot
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ScenicSpotScreen(
    scenicSpotUiState: ScenicSpotUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (scenicSpotUiState){
        is ScenicSpotUiState.Loading -> LoadingScreen(modifier)
        is ScenicSpotUiState.Error -> ErrorScreen(retryAction, modifier)
        is ScenicSpotUiState.Success -> ScenicSpotGridScreen(scenicSpots = scenicSpotUiState.scenicSpots, modifier)
    }
}

@Composable
fun ScenicSpotGridScreen(scenicSpots: List<ScenicSpot>, modifier: Modifier  = Modifier){
    val filteredScenicSpots = remember { mutableStateListOf<ScenicSpot>() }
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
            filteredScenicSpots.clear()
            filteredScenicSpots.addAll(
                scenicSpots.filter { scenicSpot ->
                    scenicSpot.ScenicSpotName.contains(searchQuery, ignoreCase = true) ||
                            scenicSpot.Description?.contains(searchQuery, ignoreCase = true) == true
                }
            )
            items(
                items = filteredScenicSpots,
                key = { scenicSpot -> scenicSpot.ScenicSpotID }) { scenicSpot ->
                ScenicSpotCard(scenicSpot)
            }
        }
    }
}

@Composable
fun ScenicSpotCard(scenicSpot: ScenicSpot, modifier: Modifier = Modifier
//,onItemClick: (ScenicSpot) -> Unit
){
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            //.clickable { onItemClick(scenicSpot) }
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
                ImageDisplay(scenicSpot.Picture.PictureUrl1)
                Text(
                    text = scenicSpot.ScenicSpotName,
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = darkBlue
                )

                Text(
                    text = scenicSpot.Description?.take(80) ?: stringResource(R.string.default_scenicspot_description),
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