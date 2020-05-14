package ru.breffi.storyidsample.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import ru.breffi.storyidsample.repository.permissons.PermissionResult
import ru.breffi.storyidsample.repository.permissons.SuspendPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.breffi.storyidsample.BuildConfig
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.BasePageInjectableFragment
import ru.breffi.storyidsample.repository.FilesRepository
import ru.breffi.storyidsample.ui.image_preview.ImagePreviewActivity
import java.io.File
import javax.inject.Inject

abstract class ImageFragment : BasePageInjectableFragment() {

    @Inject
    lateinit var filesRepository: FilesRepository

    var imageFileName: String? = null

    companion object {
        const val RC_CAMERA = 1
        const val RC_GALLERY = 2
        const val RC_DELETE = 3
    }

    fun deleteImage(file: File) {
        imageFileName = file.name
        ImagePreviewActivity.startForResult(this, file.path, RC_DELETE)
    }

    fun selectImage(fileName: String) {
        imageFileName = fileName
        AlertDialog.Builder(activity, R.style.DialogTheme)
            .setItems(R.array.items_add_photo) { _, position ->
                when (position) {
                    0 -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            val permissionResult = SuspendPermission(activity!!).requestPermissionsAsync(
                                Manifest.permission.CAMERA
                            )
                            if (permissionResult is PermissionResult.Granted) {

                                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                                val f = filesRepository.getFile(fileName)
                                val fileUri = activity?.let { FileProvider.getUriForFile(it, BuildConfig.AUTHORITY, f) }

                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

                                startActivityForResult(cameraIntent, RC_CAMERA)
                            }
                        }

                    }
                    1 -> {
                        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        pickIntent.type = "image/*"
                        val chooserIntent = Intent.createChooser(pickIntent, getString(R.string.label_choose_file))
                        startActivityForResult(chooserIntent, RC_GALLERY)
                    }
                }
            }
            .create()
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            imageFileName?.let { fileName ->
                when (requestCode) {
                    RC_CAMERA -> {
                        setImage(fileName)
                    }
                    RC_GALLERY -> {
                        data?.data?.let {
                            setImage(it, fileName)
                        }
                    }
                    RC_DELETE -> {
                        onDeleteImage(fileName)
                    }
                }
                return@let
            }
        }
    }

    private fun setImage(fileName: String) {
        val file = filesRepository.getFileFromCamera(fileName)
        onSelectImage(file)
    }

    private fun setImage(uri: Uri, fileName: String) {
        val file = filesRepository.getFile(fileName)
        filesRepository.copy(uri, file)
        onSelectImage(file)
    }

    abstract fun onSelectImage(file: File)

    abstract fun onDeleteImage(fileName: String)
}