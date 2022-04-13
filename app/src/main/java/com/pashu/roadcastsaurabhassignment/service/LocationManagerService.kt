package com.pashu.roadcastsaurabhassignment.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

class LocationManagerService (
    val context: Context,
    val onSuccess: (latitude: Double, longitude: Double) -> Unit,
    val onFailure: (reason: Int) -> Unit
) {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var handler = Handler()

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {

                onSuccess(location.latitude,location.longitude)
                stopLocationUpdates()
            }
        }
    }




    companion object {
        const val REQUEST_CHECK_SETTINGS = 214
        const val FAILURE_REASON_PERMISSION_NOT_GRANTED = 100
        const val PERMISSION_CODE_FOR_LOCATION_ACCESS = 111

    }

    fun getLocation() {
        if (isLocationPermissionGranted()) {
            onLocationPermissionGranted()
        } else {
            getPermissions()
        }
    }

    fun onLocationPermissionGranted() {
        onLocationSettings(fun() {
            location()
        }, onLocationSettingFailure)
    }

    private fun checkPermission(permission: String) = ActivityCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED


    fun isLocationPermissionGranted() =
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    fun getLocationSettingsRequest(): LocationSettingsRequest {
        return LocationSettingsRequest.Builder().apply {
            addLocationRequest(LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY))
            setAlwaysShow(true)
        }.build()
    }

    fun getSettingClient() = LocationServices.getSettingsClient(context)


    fun onLocationSettings(onSuccess: () -> Unit, onFailure: (e: ApiException) -> Unit) {
        getSettingClient().checkLocationSettings(getLocationSettingsRequest())
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e as ApiException) }
            .addOnCanceledListener {
                Log.e(
                    "GPS",
                    "checkLocationSettings -> onCanceled"
                )
            }
    }

    val onLocationSettingFailure = fun(e: ApiException) {
        when ((e).statusCode) {
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                val rae = e as ResolvableApiException
                rae.startResolutionForResult(context as Activity, REQUEST_CHECK_SETTINGS)

            } catch (sie: IntentSender.SendIntentException) {
                Log.e("GPS", "Unable to execute request.")
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.e(
                "GPS",
                "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
            )
        }
    }

    fun getPermissions() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        ActivityCompat.requestPermissions(
            context as Activity,
            permissions,
            PERMISSION_CODE_FOR_LOCATION_ACCESS
        )
    }

    @SuppressLint("MissingPermission")
    fun location() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val task = fusedLocationProviderClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY,
            object : CancellationToken() {
                override fun isCancellationRequested(): Boolean {
                    return false
                }

                override fun onCanceledRequested(onTokenCanceledListener: OnTokenCanceledListener): CancellationToken {
                    val cts = CancellationTokenSource()
                    return cts.token
                }
            }).addOnSuccessListener {
            it?.let {
                onSuccess(it.latitude, it.longitude)
            }

        }.addOnFailureListener {
        }

    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        when (requestCode) {
            PERMISSION_CODE_FOR_LOCATION_ACCESS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onLocationPermissionGranted()
                } else {
                    onFailure(FAILURE_REASON_PERMISSION_NOT_GRANTED)
                }
                return true
            }
        }
        return false
    }

    fun requestLocationUpdates() {
        val mLocationRequest: LocationRequest
        mLocationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime= 100
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context as Activity);
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.myLooper()!!
            )
        }
    }

    private fun stopLocationUpdates() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
        }

    }

}