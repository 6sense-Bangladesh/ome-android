package com.ome.app.ui.stove

import android.net.Uri
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.repo.UserRepository
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

abstract class BasePhotoViewModel(val userRepository: UserRepository) :
    BaseViewModel() {


    var fileName = "shaft"

    var currentContentUri: Uri? = null
    var currentFile: File? = null

    val imageUploadedLiveData = SingleLiveEvent<Boolean>()

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
