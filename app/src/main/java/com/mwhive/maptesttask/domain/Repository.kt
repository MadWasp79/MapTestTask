package com.mwhive.maptesttask.domain

import com.mwhive.maptesttask.App
import com.mwhive.maptesttask.R
import com.mwhive.maptesttask.data.Api
import com.mwhive.maptesttask.data.RemoteDataStore
import com.mwhive.maptesttask.domain.models.route.DirectionsResponse
import io.reactivex.Flowable


/**
 * Created by Denis Kolomiets on 25-Nov-18.
 */

object Repository: Api {



    override fun getDirections(
        origin: String,
        destination: String,
        sensor: Boolean,
        mode: String,
        apiKey: String
    ): Flowable<DirectionsResponse>  = remoteDataStore.getDirections(origin,destination,sensor,mode, apiKey)

    private val remoteDataStore = RemoteDataStore


}