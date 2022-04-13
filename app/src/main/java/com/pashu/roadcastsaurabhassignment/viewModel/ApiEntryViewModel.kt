package com.pashu.roadcastsaurabhassignment.viewModel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pashu.roadcastsaurabhassignment.data.Entries
import com.pashu.roadcastsaurabhassignment.data.EntriesResponse
import com.pashu.roadcastsaurabhassignment.repository.Repository
import kotlinx.coroutines.*

class ApiEntryViewModel : ViewModel() {

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        setState(INVALID_STATE)
    }

    val loading = MutableLiveData<Boolean>()

    var entries = mutableListOf<Entries>()

    var index = 0

    companion object {
        const val  WAITING_FOR_DATA = 1
        const val  INVALID_STATE = 2
        const val  GET_ENTRIES_SUCCESSFUL = 3

    }
    private val screenStatusFlowLiveData = MutableLiveData<Int>()


    init {
        setState(WAITING_FOR_DATA)
    }

    private fun setState(stateValue : Int){
        screenStatusFlowLiveData.postValue(stateValue)
    }

    fun getScreenStatusFlowLiveData() = screenStatusFlowLiveData
    fun entriesLiveData(){
        Repository.entries.count?.let {
            if(index<it){
                val last = if(index + 20<it) index+20 else it-1
                entries.addAll( Repository.entries.entries?.subList(index,last)!!)
                index = last + 1
            }
        }

    }

    fun getEntries() {
        loading.value = true
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            Repository.getEntries()
            withContext(Dispatchers.Main) {
                withContext(Dispatchers.Main) {
                    setState(GET_ENTRIES_SUCCESSFUL)
                }
            }
        }

    }


}