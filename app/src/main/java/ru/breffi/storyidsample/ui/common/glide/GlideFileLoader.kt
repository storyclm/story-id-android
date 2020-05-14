package ru.breffi.storyidsample.ui.common.glide

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.loader.glide.GlideLoaderException
import com.github.piasy.biv.loader.glide.GlideProgressSupport
import com.github.piasy.biv.loader.glide.ImageDownloadTarget
import com.github.piasy.biv.loader.glide.PrefetchTarget
import com.github.piasy.biv.metadata.ImageInfoExtractor
import okhttp3.OkHttpClient

import java.io.File
import java.util.concurrent.ConcurrentHashMap

class GlideFileLoader constructor(context: Context, okHttpClient: OkHttpClient?) : ImageLoader {

    private val mRequestManager: RequestManager
    private val mRequestTargetMap = ConcurrentHashMap<Int, ImageDownloadTarget>()

    init {
        GlideProgressSupport.init(Glide.get(context), okHttpClient)
        mRequestManager = Glide.with(context)
    }

    override fun loadImage(requestId: Int, uri: Uri, callback: ImageLoader.Callback) {
        val target = object : ImageDownloadTarget(uri.toString()) {
            override fun onResourceReady(
                resource: File,
                transition: Transition<in File>?
            ) {
                super.onResourceReady(resource, transition)
                // we don't need delete this image file, so it behaves like cache hit
                callback.onCacheHit(ImageInfoExtractor.getImageType(resource), resource)
                callback.onSuccess(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                callback.onFail(GlideLoaderException(errorDrawable))
            }

            override fun onDownloadStart() {
                callback.onStart()
            }

            override fun onProgress(progress: Int) {
                callback.onProgress(progress)
            }

            override fun onDownloadFinish() {
                callback.onFinish()
            }
        }
        clearTarget(requestId)
        saveTarget(requestId, target)

        downloadImageInto(File(uri.path), target)
    }

    private fun downloadImageInto(file: File, target: Target<File>) {
        mRequestManager
            .downloadOnly()
            .load(file)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(target)
    }

    override fun prefetch(uri: Uri) {
        downloadImageInto(File(uri.path), PrefetchTarget())
    }

    override fun cancel(requestId: Int) {
        clearTarget(requestId)
    }

    private fun saveTarget(requestId: Int, target: ImageDownloadTarget) {
        mRequestTargetMap[requestId] = target
    }

    private fun clearTarget(requestId: Int) {
        val target = mRequestTargetMap.remove(requestId)
        if (target != null) {
            mRequestManager.clear(target)
        }
    }

    companion object {

        fun with(context: Context, okHttpClient: OkHttpClient? = null): GlideFileLoader {
            return GlideFileLoader(context, okHttpClient)
        }
    }
}