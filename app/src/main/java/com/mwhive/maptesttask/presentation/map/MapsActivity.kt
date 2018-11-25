package com.mwhive.maptesttask.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.mwhive.devmindtestproject.utilsandextensions.visibleOrGone
import com.mwhive.maptesttask.R
import com.mwhive.maptesttask.base.BaseActivity
import kotlinx.android.synthetic.main.activity_maps.*
import timber.log.Timber
import kotlin.math.round

class MapsActivity : BaseActivity<MapViewModel>(), OnMapReadyCallback {

    override fun layoutResId(): Int = R.layout.activity_maps

    override fun viewModelClass(): Class<MapViewModel> = MapViewModel::class.java


    override fun onChangeProgressBarVisibility(isVisible: Boolean) {
        progress_bar?.apply { visibleOrGone(isVisible) }
    }

    override fun onShowError(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.error)
            setMessage(message)
            create()
            show()
        }
    }

    var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (map == null) {
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

        setupListeners()
    }

    private fun setupListeners() {
        walk_btn.setOnClickListener {
            var switchMode = 0
            when (viewModel.mode) {
                0 -> switchMode = 1
                1 -> switchMode = 2
                2 -> switchMode = 0
            }
            viewModel.modeObservable.postValue(switchMode)
            viewModel.mode = switchMode

        }
    }

    override fun onBindLiveData() {
        super.onBindLiveData()

        observe(viewModel.markersLiveData) {
            if (it.size > 1) {
                viewModel.markersLiveData.value?.clear()
                map?.clear()
            }

            if (it.size >= 2) {
            }
        }

        observe(viewModel.pointA) { pointA_tv.text = it }

        observe(viewModel.pointB) { pointB_tv.text = it }

        observe(viewModel.distance) { distance_tv.text = it }

        observe(viewModel.time) { duration_tv.text = it }

        observe(viewModel.modeObservable) {
            when (it) {
                0 -> {
                    walk_btn.setImageResource(R.drawable.ic_directions_walk_black_24dp)
                }
                1 -> {
                    walk_btn.setImageResource(R.drawable.ic_directions_bike_black_24dp)
                }
                2 -> {
                    walk_btn.setImageResource(R.drawable.ic_drive_eta_black_24dp)
                }
            }
        }

        observe(viewModel.cameraUpdateLive) { map?.animateCamera(it) }

        observe(viewModel.pathUpdate) { map?.addPolyline(it) }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        )
                ) {
                } else {
                    ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                    )
                }
            } else {
                getCurrentLocation()
            }
        } else {
            getCurrentLocation()
        }

        map?.animateCamera(CameraUpdateFactory.zoomTo(11.0f), 2000, null)

        map?.setOnMapClickListener { latLng ->

            if (viewModel.checkListSize() > 1) {
                viewModel.clearList()
                map?.clear()
            }

            val options = MarkerOptions()

            // Adding new object to the list
            viewModel.onMapClick(latLng)

            options.position(latLng)


            if (viewModel.checkListSize() == 1)
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            else if (viewModel.checkListSize() == 2) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }

            map?.addMarker(options)

            if (viewModel.checkListSize() >= 2) {
                viewModel.updatePath()
            }

        }

    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty()
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    getCurrentLocation()
                } else {
                    finish()
                }
                return
            }
            else -> {
                finish()
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0f,
                locationListener
        )
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val currentLocation = LatLng(location.latitude, location.longitude)
            if (viewModel.isStarted) {
                map?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                viewModel.myCurrentLocation.postValue(location)
                viewModel.isStarted = false
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Timber.d("onStatusChanged provider: $provider, status: $status")
        }

        override fun onProviderEnabled(provider: String) {
            Timber.d("onProviderEnabled provider: $provider")
        }

        override fun onProviderDisabled(provider: String) {
            Timber.d("onProviderDisabled provider: $provider")
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123
    }
}
