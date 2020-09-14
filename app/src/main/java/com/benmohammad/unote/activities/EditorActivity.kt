package com.benmohammad.unote.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.benmohammad.unote.Constants
import com.benmohammad.unote.ConstantsKotlin
import com.benmohammad.unote.R
import com.benmohammad.unote.model.FileModal
import com.benmohammad.unote.model.Note
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Scheduler
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.activity_editor.*


import org.jetbrains.anko.coroutines.experimental.bg
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class EditorActivity: BaseActivity() {


    private val SELECT_IMAGE = 2
    val TAG = "EditorActivity"
    val activity = this
    var uId: Long = -1
    var NO_UID: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        checkPermission()

    }

    fun checkPermission() {
        val rxPermissions = RxPermissions(this)
        rxPermissions
            .request(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_CONTACTS)
            .subscribe{granted ->
                if(granted) {
                    handleIntent()
                } else {
                    Log.d(TAG, "Permission denied")
                }
            }
    }

    fun handleIntent() {
        val bundle = intent.extras
        if(bundle != null) {
            uId = bundle.getLong("uId", -1)

            if(uId != NO_UID) {
                showNoteFromDB()
            }
        }
    }

    fun showNoteFromDB() {
        val noteId = intent.extras!!.getString("noteId")
        val notes = appDatabase.notesDao().getNotesForUid(noteId!!)
        val obsSingle = object : SingleObserver<List<Note>> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "onSubscribe")
            }

            override fun onSuccess(t: List<Note>) {
                editorView.showNotesFromDB(t)
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "onError")
            }
        }

        notes.subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(obsSingle)
    }

    fun saveData() {
        val linearLayout = editorView.ll
        val editorList = editorView.notesList

        var uniqueId = Constants.getCurrentTime()
        if(uId != NO_UID) {
            uniqueId = uId.toString()

                bg { appDatabase.notesDao().deleteNotes(uId) }
        }
        val date = uniqueId
        val lastUpdated = Constants.getCurrentTime()
        var indexUri = 0
        for(i in 0..linearLayout.childCount -1) {
            val v = linearLayout.getChildAt(i)

            editorList[i].noteId = uniqueId
            editorList[i].creationDate = date
            editorList[i].lastUpdated = lastUpdated
            if(v is AppCompatEditText && v.text!!.isNotEmpty()) {
                editorList[i].text = v.text.toString()
                if(v.text!!.trim().isEmpty())
                    continue
            } else if(v is AppCompatImageView) {
                val uri = editorView.uriList[indexUri].uri
                val ext = editorView.uriList[indexUri].ext
                var filePath = ""
                if(editorView.uriList[indexUri].filePath.isEmpty()) {
                    filePath =saveImageToFile(uri, ext)
                } else {
                    filePath = editorView.uriList[indexUri].filePath
                }
                editorList[i].uri = editorView.uriList[indexUri].toString()
                editorList[i].fileName = filePath
                ++indexUri
            }
        }

        bg {
            appDatabase.notesDao().insertAll(editorList)
        }
    }


    fun saveImageToFile(uri: Uri, extension: String): String {
        val fileName = ConstantsKotlin().getCurrentTime("yyyyMMddhhmmssSSS") + "." + extension
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
        try {
            var c = inputStream!!.read()
            while(c != -1) {
                outputStream.write(c)
                c = inputStream.read()
            }
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream!!.close()
            outputStream!!.close()
        }
        return fileName
    }

    fun convertUriToTempFile(uri: Uri, mimeType: String): FileModal {
        Log.d(TAG, "mimeType: " + mimeType)
        val tempFile = File(this.filesDir.absolutePath, "temp_image" + mimeType)
        try {
            tempFile.createNewFile()
            Constants.copyAndClose(this.contentResolver.openInputStream(uri), FileOutputStream(tempFile))
            Log.d(TAG, "temp file name: ${tempFile.name} tempFileabs Path: ${tempFile.absolutePath}")
            Constants.saveImageFromDisk(this, tempFile, mimeType)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val tempUri = Uri.fromFile(tempFile)
        Log.d(TAG, "TempFIle: ${tempUri.path}")
        return FileModal("", "", tempUri, mimeType)
    }

    fun uploadThisUri(uri: Uri, mimeType: String) {

    }

    fun sendImageToEditor(fileModal: FileModal) {
        editorView.addImage(fileModal)
    }

    fun isValidFile(uri: Uri, targetMimeType: Array<String>) {
        var currentMimeType = ""
        if(uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            val mimeType2 = mime.getExtensionFromMimeType(contentResolver.getType(uri))
            Log.d(TAG, "isValid File (1) uri:$uri, Uri mimetype: $mimeType2")
            currentMimeType = mimeType2!!

            for(i in targetMimeType.indices) {
                if(currentMimeType.equals(targetMimeType[i], ignoreCase = true)) {
                    val uri2 = uri.toString()
                    val uri3 = Uri.parse(uri2)
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri3)
                    val extension = targetMimeType[i]
                    editorView.addBitmap(uri, bitmap, extension)
                    break
                }
                if(i == targetMimeType.size -1) {
                    Toast.makeText(this, "Invalid File Type::" , Toast.LENGTH_SHORT).show()
                    return
                }
            }
        } else {
            val extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
            currentMimeType = extension
            Log.d(TAG, "isValid File (2) uri: $uri, FIle: mimtype $extension")

            for(i in targetMimeType.indices) {
                if(currentMimeType.equals(targetMimeType[i], ignoreCase = true)) {
                    val fileModal = convertUriToTempFile(uri, extension)
                    uploadThisUri(uri, targetMimeType[i])
                    break
                }

                if(i == targetMimeType.size -1) {
                    Toast.makeText(this, "Invalid File Type", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            SELECT_IMAGE -> {
                if(resultCode == Activity.RESULT_OK) {
                    val mUri = data?.data
                    val arr = arrayOf("jpg", "png", "jpeg", "webp")
                    if(mUri != null) {
                        isValidFile(mUri, arr)
                    }
                }
            }
        }
    }

    fun getImageFromCamera(){}

    fun showDialogToTakePic() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick from below")
        val str = arrayOf("Gallery", "Camera")
        builder.setItems(str, {_, i ->
            when(i) {
                0 -> getImageFromGallery()
                1 -> getImageFromCamera()
            }
        })
        builder.show()
    }


    fun getImageFromGallery() {
        val intentImg = Intent(Intent.ACTION_GET_CONTENT)
        intentImg.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        intentImg.addCategory(Intent.CATEGORY_OPENABLE)
        intentImg.setType("image/*")
        try {
            startActivityForResult(Intent.createChooser(intentImg, "Select File to Upload"), SELECT_IMAGE)
        } catch (ex: android.content.ActivityNotFoundException) {
            ex.printStackTrace()
        }
    }


    inner class Async(val uri: Uri, val mimeType: String): AsyncTask<Unit, Unit, FileModal>() {
        override fun doInBackground(vararg p0: Unit?): FileModal {
            Log.d(TAG, "mimeType: $mimeType")
            val tempFile = File(activity.filesDir.absolutePath, "temp_image" + mimeType)
            try {
                tempFile.createNewFile()
                Constants.copyAndClose(contentResolver.openInputStream(uri), FileOutputStream(tempFile))
                Log.d(TAG, "temp file name: ${tempFile.name} tempFile abs path: ${tempFile.absolutePath}")
                Constants.saveImageFromDisk(activity, tempFile, mimeType)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val tempUri = Uri.fromFile(tempFile)
            Log.d(TAG, "TempFile: ${tempUri.path}")

            return FileModal("", "", tempUri, mimeType)
        }

        override fun onPostExecute(result: FileModal?) {
            super.onPostExecute(result)
            sendImageToEditor(result!!)
        }
    }


}