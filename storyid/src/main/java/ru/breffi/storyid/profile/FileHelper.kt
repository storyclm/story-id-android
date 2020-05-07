package ru.breffi.storyid.profile

import android.content.Context
import android.graphics.Bitmap
import java.io.*


internal class FileHelper(context: Context) {

    companion object {

        fun itnFilename(tmp: Boolean = false): String {
            return "${prefix(tmp)}storyid_itn.jpg"
        }

        fun snilsFilename(tmp: Boolean = false): String {
            return "${prefix(tmp)}storyid_snils.jpg"
        }

        fun passportPageFilename(page: Int, tmp: Boolean = false): String {
            return "${prefix(tmp)}storyid_passport$page.jpg"
        }

        private fun prefix(tmp: Boolean = false): String {
            return if (tmp) "_" else ""
        }
    }

    private val filesDir = File(context.filesDir, "storyid")

    init {
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }
    }

    fun getFile(name: String): File {
        return File(filesDir, name)
    }

    fun delete(name: String) {
        delete(getFile(name))
    }

    fun delete(file: File) {
        if (file.exists()) {
            file.delete()
        }
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

    fun move(fromFileName: String, toFileName: String) {
        copy(getFile(fromFileName), getFile(toFileName))
        delete(fromFileName)
    }

    fun copy(src: File, dst: String) {
        copy(src, getFile(dst))
    }

    fun copy(src: File, dst: File) {
        var input: InputStream? = null
        var out: OutputStream? = null
        try {

            input = FileInputStream(src)
            out = FileOutputStream(dst)

            // Transfer bytes from in to out
            val buf = ByteArray(1024)
            var len: Int = input.read(buf)
            while (len > 0) {
                out.write(buf, 0, len)
                len = input.read(buf)
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
