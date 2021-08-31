package com.androiddevs.shoppinglisttestingyt.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.shoppinglisttestingyt.data.local.ShoppingItem
import com.androiddevs.shoppinglisttestingyt.data.remote.responses.ImageResponse
import com.androiddevs.shoppinglisttestingyt.others.Constants.MAX_NAME_LENGTH
import com.androiddevs.shoppinglisttestingyt.others.Constants.MAX_PRICE_LENGTH
import com.androiddevs.shoppinglisttestingyt.others.Event
import com.androiddevs.shoppinglisttestingyt.others.Resource
import com.androiddevs.shoppinglisttestingyt.repositories.ShoppingRepository
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.ResponseCache

class ShoppingViewModel @ViewModelInject constructor(
    private val repository: ShoppingRepository
) : ViewModel() {

    val shoppingItems = repository.observeAllShoppingItems()

    val totalPrice = repository.observeTotalPrice()

    private val _images = MutableLiveData<Event<Resource<ImageResponse>>>()
    val images: LiveData<Event<Resource<ImageResponse>>> = _images

    private val _currImageUrl = MutableLiveData<String>()
    val currImageUrl: LiveData<String> = _currImageUrl

    private val _insertShoppingItemsStatus = MutableLiveData<Event<Resource<ShoppingItem>>>()
    val insertShoppingItemsStatus: LiveData<Event<Resource<ShoppingItem>>> =
        _insertShoppingItemsStatus


    fun setCurrentImageUrl(url: String) {
        _currImageUrl.postValue(url)
    }

    fun deleteShoppingItem(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.deleteShoppingItem(shoppingItem)
    }

    fun insertItemIntoDB(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.insertShoppingItem(shoppingItem)
    }

    fun insertShoppingItem(name: String, amount: String, price: String) {
        if (name.isEmpty() || amount.isEmpty() || price.isEmpty()) {
            _insertShoppingItemsStatus.postValue(
                Event(
                    Resource.error(
                        "The fields must be not empty",
                        null
                    )
                )
            )
            return
        }
        if (name.length > MAX_NAME_LENGTH) {
            _insertShoppingItemsStatus.postValue(Event(Resource.error("Name too long", null)))
            return
        }
        if (price.length > MAX_PRICE_LENGTH) {
            _insertShoppingItemsStatus.postValue(Event(Resource.error("Price too long", null)))
            return
        }
        val amount = try {
            amount.toInt()
        } catch (e: Exception) {
            _insertShoppingItemsStatus.postValue(Event(Resource.error("Amount not valid", null)))
            return
        }

        val shoppingItem = ShoppingItem(name, amount, price.toFloat(), _currImageUrl.value ?: "")

        insertItemIntoDB(shoppingItem)

        setCurrentImageUrl("")
        _insertShoppingItemsStatus.postValue(Event(Resource.success(shoppingItem)))
    }

    fun searchForImage(imageQuery: String) {
        if(imageQuery.isEmpty()) return
        _images.value = Event(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.searchForImage(imageQuery)
            _images.value = Event(response)
        }
    }

}