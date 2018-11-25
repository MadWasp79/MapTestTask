package com.mwhive.maptesttask.data

import com.mwhive.maptesttask.domain.models.route.DirectionsResponse
import io.reactivex.Flowable


/**
 * Created by Denis Kolomiets on 25-Nov-18.
 */

object RemoteDataStore : Api {

    private val api: Api by lazy { RetrofitCreator.initApi() }

    override fun getDirections(
        origin: String,
        destination: String,
        sensor: Boolean,
        mode: String,
        apiKey: String
    ): Flowable<DirectionsResponse> =
        api.getDirections(origin, destination, sensor, mode, apiKey)


}