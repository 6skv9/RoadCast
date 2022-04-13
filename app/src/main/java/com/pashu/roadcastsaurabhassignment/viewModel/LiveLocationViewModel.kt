package com.pashu.roadcastsaurabhassignment.viewModel

import android.content.Context
import android.location.LocationManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pashu.roadcastsaurabhassignment.data.AddressData
import com.pashu.roadcastsaurabhassignment.service.AddressService
import com.pashu.roadcastsaurabhassignment.service.LocationManagerService
import kotlinx.coroutines.*

class LiveLocationViewModel : ViewModel() {

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        setState(INVALID_STATE)
    }

    lateinit var locationManagerService: LocationManagerService
    val loading = MutableLiveData<Boolean>()

    var address : AddressData? = null

    var locationFetched = false



    companion object {
        const val  WAITING_FOR_DATA = 1
        const val  INVALID_STATE = 2
        const val  GET_LOCATION_SUCCESSFUL = 3

    }
    private val screenStatusFlowLiveData = MutableLiveData<Int>()


    init {
        setState(WAITING_FOR_DATA)
    }

    private fun setState(stateValue : Int){
        screenStatusFlowLiveData.postValue(stateValue)
    }

    fun getScreenStatusFlowLiveData() = screenStatusFlowLiveData


    fun getLocation(context:  Context) {
            locationManagerService = LocationManagerService(context, fun(lat: Double, long: Double) {
                address = AddressService(context).getAddressByLatLong(lat,long)?.apply{
                    locationFetched = true
                }
                setState(GET_LOCATION_SUCCESSFUL)
            }, fun(reason: Int) {
                setState(INVALID_STATE)
            })

        locationManagerService.getLocation()

    }

    fun locationPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        if (locationManagerService.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        ) {
            return true
        }
        return false

    }

    fun checkIfLocationOpened(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


}