package TourismWiz.TourismWiz.ui.screens

import TourismWiz.TourismWiz.RestaurantApplication
import TourismWiz.TourismWiz.model.Restaurant
import TourismWiz.TourismWiz.data.RestaurantRepository
import TourismWiz.TourismWiz.model.City
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface RestaurantUiState{
    data class Success(val restaurants: List<Restaurant>): RestaurantUiState
    object Error: RestaurantUiState
    object Loading: RestaurantUiState
}

class RestaurantViewModel(private val restaurantRepository:RestaurantRepository) : ViewModel() {
    var restaurantUiState : RestaurantUiState by mutableStateOf(RestaurantUiState.Loading)
        private set

    private var reachableRestaurant: MutableSet<String> = mutableSetOf()

    init{
        getRestaurants(City.defaultCity)
    }

    fun getRestaurants(city:String){
        viewModelScope.launch {
            restaurantUiState = RestaurantUiState.Loading
            restaurantUiState = try {
                RestaurantUiState.Success(restaurantRepository.getRestaurants(city))
            } catch (error: IOException){
                RestaurantUiState.Error
            } catch (error: HttpException){
                RestaurantUiState.Error
            }
        }
    }

    companion object{
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                //val application = (this[APPLICATION_KEY] as RestaurantApplication)
                val application = RestaurantApplication()
                val restaurantRepository = application.container.restaurantRepository
                RestaurantViewModel(restaurantRepository = restaurantRepository)
            }
        }
    }
}