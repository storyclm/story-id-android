package ru.breffi.storyidsample.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_profile.*
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.auth.AuthActivity
import ru.breffi.storyidsample.ui.common.glide.GlideApp
import ru.breffi.storyidsample.ui.itn.ItnFragment
import ru.breffi.storyidsample.ui.passport.PassportFragment
import ru.breffi.storyidsample.ui.personal_data.PersonalDataFragment
import ru.breffi.storyidsample.ui.snils.SnilsFragment
import ru.breffi.storyidsample.utils.ImageFragment

import java.io.File
import javax.inject.Inject

class ProfileFragment : ImageFragment() {

    companion object {

        const val TAG = "ProfileFragment"
        const val AVATAR_IMAGE_FILE = "avatar.jpg"

        fun newInstance(): ProfileFragment {

            return ProfileFragment().apply { fragmentTag =
                TAG
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ProfileViewModel by viewModels { viewModelFactory }

    override fun onSelectImage(file: File) {
        viewModel.setAvatarImage(file)
    }

    override fun onDeleteImage(fileName: String) {
        viewModel.deleteAvatarImage(fileName)
    }

    override fun getTitle(context: Context, toolbarFreeWidth: Int): CharSequence {
        return getString(R.string.title_profile)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_profile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView.setOnClickListener {
            val imageFile = viewModel.imageAvatar.value
            if (imageFile == null) {
                selectImage(AVATAR_IMAGE_FILE)
            } else {
                deleteImage(imageFile)
            }
        }

        addAvatar.setOnClickListener {
            selectImage(AVATAR_IMAGE_FILE)
        }

        profile.setOnClickListener {
            nextFragment(PersonalDataFragment.newInstance())
        }

        itn.setOnClickListener {
            nextFragment(ItnFragment.newInstance())
        }

        snils.setOnClickListener {
            nextFragment(SnilsFragment.newInstance())
        }

        passport.setOnClickListener {
            nextFragment(PassportFragment.newInstance())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.imageAvatar.observe(viewLifecycleOwner) { file ->
            if (file != null && file.exists()) {
                setAvatarImage(file)
            } else {
                setAvatarNoImage()
            }
        }
        viewModel.startLoading()
    }

    private fun setAvatarImage(image: File) {
        context?.let {
            GlideApp.with(it)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView)
        }
    }

    private fun setAvatarNoImage() {
        imageView.setImageDrawable(null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_logout, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionLogout) {
            AlertDialog.Builder(context, R.style.DialogTheme)
                .setTitle(R.string.title_logout)
                .setMessage(R.string.text_logout)
                .setPositiveButton(R.string.button_yes) { dialog, _ ->
                    viewModel.logout()
                    val starter = Intent(context, AuthActivity::class.java)
                    starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context?.startActivity(starter)

                    dialog.dismiss()
                }
                .setNegativeButton(R.string.button_no) { _, _ -> }
                .show()
        }
        return super.onOptionsItemSelected(item)
    }
}