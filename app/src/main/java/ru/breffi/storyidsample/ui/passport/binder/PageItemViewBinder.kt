package ru.breffi.storyidsample.ui.passport.binder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.moqod.android.recycler.multitype.MultiTypeViewBinder
import kotlinx.android.synthetic.main.item_passport_page.view.*
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.glide.GlideApp
import ru.breffi.storyidsample.ui.passport.model.PassportPageUiModel

class PageItemViewBinder(@param:NonNull private val listener: Listener) : MultiTypeViewBinder<PageItemViewBinder.ViewHolder, PassportPageUiModel>() {

    interface Listener {
        fun onImageClick(pageUiModel: PassportPageUiModel)
    }

    override fun isValidModel(o: Any): Boolean {
        return o is PassportPageUiModel
    }

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, viewGroup: ViewGroup): ViewHolder {
        val view = layoutInflater.inflate(R.layout.item_passport_page, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, model: PassportPageUiModel) {
        val itemView = holder.itemView
        if (model.imageFile != null) {
            GlideApp.with(itemView.context)
                .load(model.imageFile.path)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(itemView.page_image)
            itemView.page_image.background = null
        } else {
            itemView.page_image.setImageBitmap(null)
            itemView.page_image.setBackgroundResource(R.drawable.ic_add_image)
        }
        itemView.page_title.text = itemView.resources
            .getString(R.string.passport_page_item_title, "${model.page * 2}-${model.page * 2 + 1}")
        itemView.setOnClickListener {
            listener.onImageClick(model)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
