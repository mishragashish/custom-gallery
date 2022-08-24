package com.customgallery.GalleryView.model

import android.net.Uri

data class ItemModel (
    val uri: Uri,
    var isSelected:Boolean,
    var count:Int,
    var position:Int = 0
)