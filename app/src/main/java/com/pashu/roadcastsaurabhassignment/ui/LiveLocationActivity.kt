package com.pashu.roadcastsaurabhassignment.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.pashu.roadcastsaurabhassignment.databinding.ActivityLiveLocationBinding
import com.pashu.roadcastsaurabhassignment.viewModel.LiveLocationViewModel

class LiveLocationActivity : AppCompatActivity() {
    private val liveLocationViewModel: LiveLocationViewModel by viewModels()
    lateinit var binding : ActivityLiveLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLiveLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setObserver()
    }

    private fun init() {

        liveLocationViewModel.getLocation(this)
        binding.fetch.setOnClickListener {
            binding.fetch.visibility = View.INVISIBLE
            binding.address.setText("")
            binding.progressBar.visibility = View.VISIBLE
            liveLocationViewModel.getLocation(this)
        }
    }

    private fun setObserver() {
        liveLocationViewModel.getScreenStatusFlowLiveData().observe(this) {
            when(it) {
                LiveLocationViewModel.WAITING_FOR_DATA -> waitingForData()
                LiveLocationViewModel.INVALID_STATE -> inValidState()
                LiveLocationViewModel.GET_LOCATION_SUCCESSFUL -> setData()
            }
        }
    }

    fun waitingForData() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun inValidState() {
        binding.progressBar.visibility = View.GONE
    }

    private fun setData() {
        binding.progressBar.visibility = View.GONE
        liveLocationViewModel.address?.let { binding.address.setText(
            "Device Address :- ${it.village}, ${it.city}, ${it.state}, ${it.country}, ${it.pin} " +
                    "\n Latitude :- ${it.lat} \n Longitude :- ${it.long}"
        ) }
        binding.fetch.visibility = View.VISIBLE


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (liveLocationViewModel.locationPermissionResult(
                requestCode,
                permissions,
                grantResults
            )
        ) {
            return
        }
    }

    override fun onResume() {
        super.onResume()
        if(!liveLocationViewModel.locationFetched && liveLocationViewModel.checkIfLocationOpened(this)){
            liveLocationViewModel.getLocation(this)
        }

    }


}