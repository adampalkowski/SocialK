package com.example.socialk.di

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialk.model.Activity
import com.example.socialk.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val repo : ActivityRepository
) : ViewModel() {
    var openDialogState = mutableStateOf(false)

    private val _activitiesListState= mutableStateOf<Response<List<Activity>>>(Response.Loading)
    val activitiesListState : State<Response<List<Activity>>> =_activitiesListState

    private val _activityState= mutableStateOf<Response<Activity>>(Response.Loading)
    val activityState : State<Response<Activity>> =_activityState

    private val _isActivityAddedState = mutableStateOf<Response<Void?>?>(Response.Success(null))
    val isActivityAddedState: State<Response<Void?>?> = _isActivityAddedState

    private val _isActivityDeletedState = mutableStateOf<Response<Void?>>(Response.Success(null))
    val isActivityDeletedState: State<Response<Void?>> = _isActivityDeletedState

    init {
       // getActivities()
    }

    fun getActivity(id:String){
        viewModelScope.launch {
            repo.getActivity(id).collect{ response->
                _activityState.value=response
            }
        }
    }
    fun getActivitiesForUser(id:String?){
        if (id==null){
            _activitiesListState.value=Response.Failure(Exception())
        }else{
            viewModelScope.launch {
                repo.getActivitiesForUser(id).collect{ response->
                    _activitiesListState.value=response
                }
            }
        }

    }
    fun addActivity(activity:Activity){
        viewModelScope.launch {
            repo.addActivity(activity).collect{ response ->
                _isActivityAddedState.value=response
            }
        }
    }
    fun activityAdded(){
        _isActivityAddedState.value=null
    }
   fun deleteActivity(id:String){
        viewModelScope.launch {
            repo.deleteActivity(id).collect{ response ->
                _isActivityDeletedState.value=response
            }
        }
    }
}
