package com.benmohammad.unote.model

import android.net.Uri

class MediaMetaData(uri: Uri, extension: String, path: String = "") {
    val uri = uri
    val ext = extension
    val filePath = path
}