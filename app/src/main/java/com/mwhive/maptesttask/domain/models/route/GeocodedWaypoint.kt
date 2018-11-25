package com.mwhive.maptesttask.domain.models.route

data class GeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
)