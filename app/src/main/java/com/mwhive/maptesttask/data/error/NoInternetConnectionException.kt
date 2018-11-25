package com.mwhive.maptesttask.data.error


import com.mwhive.maptesttask.App
import com.mwhive.maptesttask.R

class NoInternetConnectionException
    : Exception(App.applicationContext().getString(R.string.exception_no_internet_connection))