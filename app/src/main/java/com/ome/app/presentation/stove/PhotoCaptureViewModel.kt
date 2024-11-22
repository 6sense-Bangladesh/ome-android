package com.ome.app.presentation.stove

import android.graphics.Bitmap
import android.net.Uri
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PhotoCaptureViewModel @Inject constructor(val userRepository: UserRepository) : BaseViewModel() {

    private var fileName = "shaft"

    var currentContentUri: Uri? = null
    var currentFile: File? = null

    val imageUploadedLiveData = SingleLiveEvent<Boolean>()

    private val _photoList = MutableStateFlow<MutableList<File>>(mutableListOf())
    val photoList: StateFlow<List<File>> = _photoList.asStateFlow()
    private val _photoThumbList = MutableStateFlow<MutableList<Bitmap>>(mutableListOf())
    val photoThumbList: StateFlow<List<Bitmap>> = _photoThumbList.asStateFlow()

//    var processingCount = 0
//    val isProcessing
//        get() = processingCount > _photoList.value.size

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

    fun uploadImage() = launch(ioContext) {
        currentFile?.let {
            loadingLiveData.postValue(true)
            val urlToUploadResponse = userRepository.getUrlToUploadImage("$fileName.png")
            if(urlToUploadResponse is ResponseWrapper.Success){
                val uploadTo = urlToUploadResponse.value.uploadTo
                val requestBody = it.asRequestBody("image/png".toMediaType())
                userRepository.uploadImage(uploadTo, requestBody)
                imageUploadedLiveData.postValue(true)
                loadingLiveData.postValue(false)
            }else{
                loadingLiveData.postValue(false)
                error("Something went wrong")
            }
        } ?: run {
            error("Capture image first")
        }
    }
}
