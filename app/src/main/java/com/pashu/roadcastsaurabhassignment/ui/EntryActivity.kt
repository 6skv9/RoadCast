package com.pashu.roadcastsaurabhassignment.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.pashu.roadcastsaurabhassignment.adapter.RecyclerViewAdapter
import com.pashu.roadcastsaurabhassignment.data.Entries
import com.pashu.roadcastsaurabhassignment.databinding.ActivityEntryBinding
import com.pashu.roadcastsaurabhassignment.viewModel.ApiEntryViewModel

class EntryActivity : AppCompatActivity(), RecyclerViewAdapter.SelectEntry {

    private val apiEntryViewModel: ApiEntryViewModel by viewModels()
    private lateinit var binding: ActivityEntryBinding
    var mAdapter: RecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setObserver()
    }

    private fun init() {
//        binding.ivBackArrowFilterCattle.setOnClickListener(this::onBackClicked)
//        binding.tvClearFilters.setOnClickListener(this::clearFilters)
        apiEntryViewModel.getEntries()
        mAdapter = RecyclerViewAdapter(mutableListOf<Entries>(),this)
        val mLayoutManager = LinearLayoutManager(this)
        binding.rcEntries.layoutManager = mLayoutManager
        binding.rcEntries.itemAnimator = DefaultItemAnimator()
        binding.rcEntries.adapter = mAdapter
    }

    private fun setObserver() {
        apiEntryViewModel.getScreenStatusFlowLiveData().observe(this) {
            when(it) {
                ApiEntryViewModel.WAITING_FOR_DATA -> waitingForData()
                ApiEntryViewModel.INVALID_STATE -> inValidState()
                ApiEntryViewModel.GET_ENTRIES_SUCCESSFUL -> setData()
            }
        }
    }

    fun waitingForData() {
        binding.progress.visibility = View.VISIBLE
    }

    private fun inValidState() {
        binding.progress.visibility = View.GONE
    }

    private fun setData() {
        binding.progress.visibility = View.GONE
        apiEntryViewModel.entriesLiveData().let {
            showEntries()
        }

    }

    private fun showEntries() {
        mAdapter?.updateData(apiEntryViewModel.entries)

    }

    override fun notifyPageNumber() {
        binding.progress.visibility = View.VISIBLE
        Handler().postDelayed({
            setData()
        }, 400)

    }

}
