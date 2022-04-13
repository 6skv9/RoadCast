package com.pashu.roadcastsaurabhassignment.service

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.pashu.roadcastsaurabhassignment.data.AddressData
import java.util.*

class AddressService (val context: Context) {

    fun getAddressByLatLong(lat: Double, long: Double) : AddressData? {
        val latitude = lat
        val longitude = long
        try {
            val geocoder = Geocoder(context, Locale("en"))
            val addresses: List<Address> = geocoder.getFromLocation(lat, long, 1)
            val cityName = addresses[0].subAdminArea
            val stateName = addresses[0].adminArea
            val villageName = addresses[0].locality
            val pincode = addresses[0].postalCode
            val country = addresses[0].countryName
            return AddressData(latitude,
                longitude,
                villageName ?: "",
                cityName ?: "",
                stateName ?: "",
                country ?: "",
                pincode ?: "")

        }catch (e:Exception) {
            return null
        }
    }


}