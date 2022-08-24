package com.customgallery.fileHelper

import android.app.Activity
import android.content.Context
import android.provider.MediaStore
import com.customgallery.FolderNameWithPathModel
import java.io.File
import java.io.FilenameFilter

class FileHelper(val context: Context) {

    companion object {
        private var mInstance: FileHelper? = null

        @Synchronized
        fun getInstance(mCtx: Context): FileHelper? {
            if (mInstance == null) {
                mInstance = FileHelper(mCtx)
            }
            return mInstance
        }
    }

    fun getFolderNameWithPath(activity:Activity) : MutableList<FolderNameWithPathModel?>? {
        val list:MutableList<FolderNameWithPathModel?> = mutableListOf()
        val listParentPath:ArrayList<String> = ArrayList()

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        val cursor = activity.getContentResolver().query(uri!!, projection, null, null, "$orderBy DESC")
        val column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        val column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor!!.moveToNext()) {
            val absolutePathOfImage = cursor!!.getString(column_index_data)
            val folderPath = cursor!!.getString(column_index_data)
//            listOfAllImages.add(absolutePathOfImage!!)
//            list.add(FolderNameWithPathModel(folderPath,folderPath+"  Folder name"))
            val file:File = File(absolutePathOfImage)
            val parentP = file.parent
            if (!listParentPath.contains(parentP)) {
                listParentPath.add(parentP)
            }
        }

        if (listParentPath.size > 0) {
            listParentPath.forEach {
                val path = it.split("/")
                val folderName:String = path.get(path.size - 1)
                list.add(FolderNameWithPathModel(it,folderName))
            }
        }
        return list
    }

    fun allImages(activity: Activity) : MutableList<File?>? {
        val mutableList:MutableList<File?> = mutableListOf()
        try {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            val orderBy = MediaStore.Images.Media.DATE_TAKEN
            val cursor = activity.getContentResolver().query(uri!!, projection, null, null, "$orderBy DESC")
            val column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            while (cursor!!.moveToNext()) {
                val absolutePathOfImage = cursor!!.getString(column_index_data)
                val file:File = File(absolutePathOfImage)
                mutableList.add(file)
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
        return mutableList
    }

    fun getImageListFromFolder(path:String) : MutableList<File?>? {
        val list:MutableList<File?> = mutableListOf()
        val folder = File(path)
        if (folder.exists()) {
            val allFiles: Array<File> = folder.listFiles(object : FilenameFilter {
                override fun accept(dir: File?, name: String): Boolean {
                    return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
                }
            })
            list.addAll(allFiles)
        }
        return list
    }
}