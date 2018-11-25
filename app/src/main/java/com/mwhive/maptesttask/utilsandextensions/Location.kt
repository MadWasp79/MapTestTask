package com.mwhive.maptesttask.utilsandextensions

import com.google.android.gms.maps.model.LatLng


/**
 * Created by Denis Kolomiets on 25-Nov-18.
 */


//Helper fun to build url for web service
fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
    val str_origin = "origin=" + origin.latitude + "," + origin.longitude
    val str_dest = "destination=" + dest.latitude + "," + dest.longitude
    val sensor = "sensor=false"
    val mode = "mode=driving"
    val parameters = "$str_origin&$str_dest&$sensor&$mode"
    val output = "json"
    return "maps/api/directions/$output?$parameters"
}