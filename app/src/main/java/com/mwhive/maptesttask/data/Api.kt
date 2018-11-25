package com.mwhive.maptesttask.data

import com.mwhive.maptesttask.domain.models.route.DirectionsResponse
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Denis Kolomiets on 25-Nov-18.
 */

interface Api {

    @GET("maps/api/directions/json")
    fun getDirections(
        @Query("origin") origin:String,
        @Query("destination" ) destination:String,
        @Query("sensor") sensor: Boolean,
        @Query("mode") mode: String,
        @Query("key") apiKey: String

    ): Flowable<DirectionsResponse>

}