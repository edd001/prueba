package com.example.moviedb.base

import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BaseViewModel @Inject constructor(): ViewModel() {
    var items = ArrayList<String>().toTypedArray()
    val backgroundForBlur = MutableLiveData<Bitmap>()
    val spinnerIndex = MutableLiveData<String>()
    val isCollapsedMode = MutableLiveData(false)
    var isReadyForNextChange = true
    var defaultCollapsedParams: ViewGroup.LayoutParams? = null
}