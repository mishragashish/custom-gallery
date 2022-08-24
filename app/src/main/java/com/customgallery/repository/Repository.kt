package com.customgallery.repository

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import com.customgallery.FolderNameWithPathModel
import com.customgallery.fileHelper.FileHelper
import java.io.File

class Repository(val fileHelper: FileHelper) {

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: Repository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance
                    ?: Repository(FileHelper.getInstance(context)!!).also { instance = it }
            }
    }


    suspend fun getFolderNameWithPath(activity: Activity) : MutableList<FolderNameWithPathModel?>? {
        return fileHelper.getFolderNameWithPath(activity)
    }

    suspend fun getImageListFromFolder(path:String) : MutableList<File?>? {
        return fileHelper.getImageListFromFolder(path)
    }

    suspend fun allImages(activity: Activity) : MutableList<File?>? {
        return fileHelper.allImages(activity)
    }


}