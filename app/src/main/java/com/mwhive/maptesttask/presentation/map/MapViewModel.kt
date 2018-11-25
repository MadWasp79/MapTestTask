package com.mwhive.maptesttask.presentation.map

import android.arch.lifecycle.MutableLiveData
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.mwhive.maptesttask.App
import com.mwhive.maptesttask.R
import com.mwhive.maptesttask.base.BaseViewModel
import com.mwhive.maptesttask.domain.models.route.Bounds
import com.mwhive.maptesttask.domain.models.route.DirectionsResponse
import com.mwhive.maptesttask.domain.models.route.Route
import com.mwhive.maptesttask.utilsandextensions.NetworkConnectivity
import com.mwhive.maptesttask.utilsandextensions.extensions.cropToString
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import org.xml.sax.ErrorHandler
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap


/**
 * Created by Denis Kolomiets on 25-Nov-18.
 */

class MapViewModel : BaseViewModel() {
    val pointA = MutableLiveData<String>()
    val pointB = MutableLiveData<String>()
    val distance = MutableLiveData<String>()
    val time = MutableLiveData<String>()
    val myCurrentLocation = MutableLiveData<Location>()
    val markersLiveData = MutableLiveData<MutableList<LatLng>>()
    val modeObservable = MutableLiveData<Int>()
    val cameraUpdateLive = MutableLiveData<CameraUpdate>()
    val pathUpdate = MutableLiveData<PolylineOptions>()
    var isStarted = true

    private val pinsList = mutableListOf<LatLng>()

    var mode: Int = 0
    private val modeList = arrayListOf("walking", "bicycling", "driving")

    val key = App.applicationContext().resources.getString(R.string.google_maps_key)

    fun onMapClick(latLng: LatLng) = pinsList.add(latLng)
    fun checkListSize(): Int = pinsList.size
    fun clearList() = pinsList.clear()

    init { modeObservable.value = mode }

    fun updatePath() {
        val origin = "${pinsList[0].latitude},${pinsList[0].longitude}"
        val destination = "${pinsList[1].latitude},${pinsList[1].longitude}"

        compositeDisposable.add(
            NetworkConnectivity.getStateFlowable()
                .flatMap { repository.getDirections(origin, destination, false, modeList[mode], key) }
                .doOnSubscribe { showLoading() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        hideLoading()
                        onGetPathData(it)
                    },
                    {
                        hideLoading()
                        showError(it.message.toString())
                    }
                )
        )

//        getCompleteAddressString(pinsList[0])
//        getCompleteAddressString(pinsList[1])
    }

    private fun onGetPathData(directionsResponse: DirectionsResponse) {
        Timber.i(directionsResponse.routes.toString())
        distance.postValue(directionsResponse.routes[0].legs[0].distance.text)
        time.postValue(directionsResponse.routes[0].legs[0].duration.text)
        if(directionsResponse.routes[0].legs[0].start_address.isNotEmpty())
            pointA.postValue(directionsResponse.routes[0].legs[0].start_address)
        else pointA.postValue("lat: ${directionsResponse.routes[0].legs[0].start_location.lat.cropToString(4)}, " +
                "lng:${directionsResponse.routes[0].legs[0].start_location.lng.cropToString(5)}")
        if(directionsResponse.routes[0].legs[0].end_address.isNotEmpty())
            pointB.postValue(directionsResponse.routes[0].legs[0].end_address)
        else pointB.postValue("lat: ${directionsResponse.routes[0].legs[0].end_location.lat.cropToString(4)}, " +
                "lng:${directionsResponse.routes[0].legs[0].end_location.lng.cropToString(5)}")


        parsePath(directionsResponse.routes)
    }


    fun parsePath(routes: List<Route>) {
        calculateBounds(routes[0].bounds)

        val listOfPoints = mutableListOf<LatLng>()

        routes.forEach {
            listOfPoints.clear()
            val polylineOptions = PolylineOptions()


            it.legs.forEach{leg ->
                leg.steps.forEach { step ->
                    if(listOfPoints.size==0) {
                    listOfPoints.add(LatLng(step.start_location.lat, step.start_location.lng))
                    listOfPoints.add(LatLng(step.end_location.lat, step.end_location.lng))}
                    else {
                    listOfPoints.add(LatLng(step.end_location.lat, step.end_location.lng))}
                }
            }

            polylineOptions.addAll(listOfPoints)
            polylineOptions.width(16.0f)
            polylineOptions.color(Color.BLUE)
            polylineOptions.geodesic(true)
            polylineOptions.jointType(JointType.ROUND)
            polylineOptions.startCap(RoundCap())
            polylineOptions.endCap(RoundCap())
            pathUpdate.postValue(polylineOptions)

        }

    }

    private fun calculateBounds(bounds: Bounds) {
        val padding = 120

        val builder = LatLngBounds.Builder()
        builder.include(LatLng(bounds.northeast.lat, bounds.northeast.lng))
        builder.include(LatLng(bounds.southwest.lat, bounds.southwest.lng))
        val bounds = builder.build()
        val camUpd = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        cameraUpdateLive.postValue(camUpd)
    }

    //Fun to get and parse street address (could be expanded to return country and more)
    private fun getCompleteAddressString(latLng: LatLng): String {
        var strAdd = ""
        val geocoder = Geocoder(App.applicationContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")

                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("Current address", strReturnedAddress.toString())
            } else {
                Log.w("Current address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("Current address", "Canont get Address!")
        }

        return strAdd
    }

}