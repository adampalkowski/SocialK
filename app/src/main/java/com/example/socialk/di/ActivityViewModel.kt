package com.example.socialk.di

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialk.ActiveUser
import com.example.socialk.home.HomeEvent
import com.example.socialk.model.Activity
import com.example.socialk.model.Response
import com.example.socialk.model.SocialException
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList
sealed class ActivityEvent{
    object DeleteActivity:ActivityEvent()
}

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
            _activitiesListState.value=Response.Failure(SocialException("getActivitiesForUser error id is null",Exception()))
        }else{
            viewModelScope.launch {
                val list_without_removed_activites:ArrayList<Activity> = ArrayList()
                repo.getActivitiesForUser(id).collect{ response->
                    when(response) {
                        is Response.Success ->{response.data.forEach {
                            list_without_removed_activites.add(it)
                            val time_left:String =calculateTimeLeft(it.date,it.start_time, deleteActivity = {event->
                                Log.d("activityViewModel","delete activity")
                                deleteActivity(it.id)
                                list_without_removed_activites.remove(it)
                            })
                            it.time_left=time_left


                            _activitiesListState.value=Response.Success(list_without_removed_activites as List<Activity>)
                        }}
                        is Response.Failure->{
                            _activitiesListState.value=response
                        }
                        is Response.Loading->{
                            _activitiesListState.value=response
                        }
                    }


                }
            }
        }

    }
    fun addTimes(time1: String, time2: String): String {
        val (hours1, minutes1) = time1.split(":").map { it.toInt() }
        val (hours2, minutes2) = time2.split(":").map { it.toInt() }
        val totalMinutes = (hours1 * 60 + minutes1) + (hours2 * 60 + minutes2)
        val totalHours = totalMinutes / 60
        val finalMinutes = totalMinutes % 60
        return String.format("%02d:%02d", totalHours, finalMinutes)
    }
    fun addActivity(activity:Activity){
        viewModelScope.launch {
            activity.end_time=addTimes(activity.start_time,activity.time_length)
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

    fun compareDates(date1: String, date2: String,deleteActivity:()->Unit): Long {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val d1 = format.parse(date1)
        val d2 = format.parse(date2)
        val diffInMilliseconds = d2.time - d1.time
        if(diffInMilliseconds<0){
            deleteActivity()
        }

        return TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS)
    }
    fun calculateTimeLeft( date:String, time_start:String,deleteActivity: (ActivityEvent) -> Unit) :String{
//time
        LocalTime.now().noSeconds().toString()

        val difference = compareDates(    LocalDate.now().toString(),date, deleteActivity ={deleteActivity(ActivityEvent.DeleteActivity)})
        if (difference.toString().equals("0")){

            return compareTimes(LocalTime.now().noSeconds().toString(), time_start, deleteActivity ={deleteActivity(ActivityEvent.DeleteActivity)})
        }else{
            return "$difference days"
        }

    }
    fun compareTimes(time1: String, time2: String,deleteActivity:()->Unit): String {
        val format = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val t1 = format.parse(time1)
        val t2 = format.parse(time2)
        val diffInMilliseconds = t2.time - t1.time
        val hours = TimeUnit.HOURS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS)
        val minutes = TimeUnit.MINUTES.convert(diffInMilliseconds, TimeUnit.MILLISECONDS) -
                TimeUnit.HOURS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS) * 60
        if(minutes<0 || hours<0){
            deleteActivity()
        }


        if (hours >0){
            return "$hours hours $minutes minutes"
        }else{
            return "$minutes minutes"

        }
    }

}
