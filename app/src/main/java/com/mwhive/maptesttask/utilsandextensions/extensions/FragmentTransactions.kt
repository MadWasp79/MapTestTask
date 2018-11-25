package com.mwhive.maptesttask.utilsandextensions.extensions

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity


/**
 * to add or replace fragment inside AppCompatActivity we just need to call functions like:
 *
 * addFragment(fragment, R.id.fragment_container)
 * replaceFragment(fragment, R.id.fragment_container)
 */

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int){
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}


fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction{ replace(frameId, fragment) }
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.()-> FragmentTransaction) {
    beginTransaction().func().commit()
}