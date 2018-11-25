package com.mwhive.maptesttask.base

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity


abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {

    protected val viewModel: VM by lazy { setupViewModel() }

    @LayoutRes
    protected abstract fun layoutResId(): Int

    protected abstract fun viewModelClass(): Class<VM>

    protected abstract fun onChangeProgressBarVisibility(isVisible: Boolean)

    protected abstract fun onShowError(message: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        onBindLiveData()
        super.onCreate(savedInstanceState)
        setContentView(layoutResId())
    }

    protected open fun onBindLiveData() {
        observe(viewModel.progressBarVisibility, ::onChangeProgressBarVisibility)
        observe(viewModel.errorVisibility, ::onShowError)
    }

    protected fun <T, LD : LiveData<T>> observe(liveData: LD, onChanged: (T) -> Unit) {
        liveData.observe(this, Observer { value ->
            value?.let(onChanged)
        })
    }

    private fun setupViewModel() = ViewModelProviders.of(this).get(viewModelClass())
}