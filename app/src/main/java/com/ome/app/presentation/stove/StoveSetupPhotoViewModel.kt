package com.ome.app.presentation.stove

import android.graphics.Bitmap
import com.ome.app.domain.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject


@HiltViewModel
class StoveSetupPhotoViewModel @Inject constructor(userRepository: UserRepository) :
    BasePhotoViewModel(userRepository) {
    private val _photoList = MutableStateFlow<MutableList<File>>(mutableListOf())
    val photoList: StateFlow<List<File>> = _photoList.asStateFlow()
    private val _photoThumbList = MutableStateFlow<MutableList<Bitmap>>(mutableListOf())
    val photoThumbList: StateFlow<List<Bitmap>> = _photoThumbList.asStateFlow()

//    var processingCount = 0
//    val isProcessing
//        get() = processingCount > _photoList.value.size

    var isProcessing = false


    fun addPhoto(file: File) {
//        val oldList =_photoList.value.toMutableList()
//        oldList.add(0, file)
        _photoList.value = mutableListOf(file)
    }

    fun addPhotoThumb(file: Bitmap) {
//        val oldList =_photoThumbList.value.toMutableList()
//        oldList.add(0, file)
        _photoThumbList.value = mutableListOf(file)
    }

    fun removePhoto(position: Int) {
        val oldList =_photoList.value.toMutableList()
        val old = oldList.getOrNull(position)
        old?.delete()
        oldList.removeAt(position)
        _photoList.value = oldList

        val oldList2 =_photoThumbList.value.toMutableList()
        oldList2.removeAt(position)
        _photoThumbList.value = oldList2
    }

    fun removeAllPhoto() {
        _photoList.value.forEach {
            it.delete()
        }
        _photoList.value = mutableListOf()
        _photoThumbList.value = mutableListOf()
    }

    fun clearList() {
//        _photoList.value = mutableListOf()
        _photoThumbList.value = mutableListOf()
    }

}
