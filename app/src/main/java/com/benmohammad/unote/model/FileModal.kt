package com.benmohammad.unote.model

import android.net.Uri

class FileModal {

    internal var name: String? = null
    internal var size: String? = null
    internal var uri: Uri? = null
    internal var mimeType: String? = null

    constructor(name: String?, size: String?, uri: Uri?, mimeType: String?) {
        this.name = name
        this.size = size
        this.uri = uri
        this.mimeType = mimeType
    }
}