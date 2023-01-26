package com.ome.app.ui.stove

import android.net.Uri
import com.ome.app.base.BaseViewModel
import com.ome.app.base.SingleLiveEvent
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.model.base.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


@HiltViewModel
class StoveSetupPhotoViewModel @Inject constructor(val userRepository: UserRepository) :
    BaseViewModel() {

    var fileName = "shaft"

    var currentContentUri: Uri? = null
    var currentFile: File? = null

    val imageUploadedLiveData = SingleLiveEvent<Boolean>()

    fun uploadImage(file: File) = launch(dispatcher = ioContext) {
        currentFile = file
        val urlToUploadResponse = userRepository.getUrlToUploadImage("$fileName.png")
        if(urlToUploadResponse is ResponseWrapper.Success){
            val uploadTo = urlToUploadResponse.value.uploadTo
            val requestBody = file.asRequestBody("image/png".toMediaType())
            userRepository.uploadImage(uploadTo, requestBody)
            imageUploadedLiveData.postValue(true)
        }
    }

}
