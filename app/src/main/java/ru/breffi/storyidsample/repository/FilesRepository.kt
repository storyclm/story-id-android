package ru.breffi.storyidsample.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import ru.breffi.storyidsample.utils.handleSamplingAndRotationBitmap
import java.io.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FilesRepository @Inject
constructor(val context: Context) {

    private val filesDir = File(context.filesDir, "tmp")

    init {
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }
    }

    fun getFileFromCamera(name: String): File {
        val file = File(filesDir, name)
        val b = file.toUri().handleSamplingAndRotationBitmap(context)
        return copyFromBitmap(b, name)
    }

    fun getFile(name: String): File {
        return File(filesDir, name)
    }

    fun getFileIfExists(name: String): File? {
        val file = File(filesDir, name)
        return if (file.exists()) file else null
    }

    fun delete(file: File?) {
        if (file != null && file.exists()) {
            file.delete()
        }
    }

    fun delete(fileName: String) {
        delete(getFile(fileName))
    }

    fun clearFiles() {
        filesDir.list()?.forEach { File(filesDir, it).delete() }
    }

    fun copyFromBitmap(bitmap: Bitmap, name: String): File {
        val file = getFile(name)
        file.createNewFile()

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapData: ByteArray = bos.toByteArray()

        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()

        return file
    }

    fun copy(uri: Uri, dst: File) {
        var input: InputStream? = null
        var out: OutputStream? = null
        try {

            input = context.contentResolver.openInputStream(uri)
            out = FileOutputStream(dst)

            if (input != null) {
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int = input.read(buf)
                while (len > 0) {
                    out.write(buf, 0, len)
                    len = input.read(buf)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    //silently
                }

            }
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    //silently
                }

            }
        }
    }

}
