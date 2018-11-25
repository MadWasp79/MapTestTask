package com.mwhive.maptesttask.domain.models.route

data class DirectionsResponse(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)