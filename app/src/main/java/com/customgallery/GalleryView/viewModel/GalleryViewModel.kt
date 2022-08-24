package com.customgallery.GalleryView.viewModel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.customgallery.FolderNameWithPathModel
import com.customgallery.base.BaseViewModel
import com.customgallery.repository.Repository
import kotlinx.coroutines.*
import java.io.File

class GalleryViewModel(val repository: Repository) : BaseViewModel() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    val folderList: MutableLiveData<MutableList<FolderNameWithPathModel?>> = MutableLiveData()
    val imagesList:MutableLiveData<MutableList<File?>> = MutableLiveData()

    fun getFolderNameWithPath(activity: Activity) {
        job = scope.launch {
            try {
                val resp = repository.getFolderNameWithPath(activity)

                if (resp != null) {
                    folderList.postValue(resp)
                } else {
                    errMsgObserve.postValue("No data found.")
                }

            } catch (e:Exception) {
                catchException(e)
            }
        }
    }

    fun getImageListFromFolder(path:String) {
        job = scope.launch {
            try {
                val resp = repository.getImageListFromFolder(path)
                if (resp != null) {
                    imagesList.postValue(resp)
                } else {
                    errMsgObserve.postValue("No data found!")
                }
            } catch (e:Exception) {
                catchException(e)
            }
        }
    }

    fun allImages(activity: Activity) {
        job = scope.launch {
            try {
                val resp = repository.allImages(activity)
                if (resp != null) {
                    imagesList.postValue(resp)
                } else {
                    errMsgObserve.postValue("No data found!")
                }
            } catch (e:Exception) {
                catchException(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        scope.coroutineContext.cancel()
    }

}