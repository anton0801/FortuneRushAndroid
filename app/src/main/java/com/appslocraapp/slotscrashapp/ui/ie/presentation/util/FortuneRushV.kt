package com.appslocraapp.slotscrashapp.ui.ie.presentation.util

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.appslocraapp.slotscrashapp.app.MainApplication
import com.appslocraapp.slotscrashapp.ui.ie.presentation.FortuneRushLoadingFragment
import org.koin.android.ext.android.inject

class FortuneRushV : Fragment() {

    private lateinit var eggLabelPhoto: Uri
    private var eggLabelFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val eggLabelTakeFile: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            eggLabelFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
            eggLabelFilePathFromChrome = null
        }

    private val eggLabelTakePhoto: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                eggLabelFilePathFromChrome?.onReceiveValue(arrayOf(eggLabelPhoto))
                eggLabelFilePathFromChrome = null
            } else {
                eggLabelFilePathFromChrome?.onReceiveValue(null)
                eggLabelFilePathFromChrome = null
            }
        }

    private val fortRushhDataStore by activityViewModels<FortRushhDataStore>()


    private val forrrttRushhhhhViFun by inject<ForrrttRushhhhhViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(MainApplication.FORTUNE_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (fortRushhDataStore.forrrtttRusshhhhView.canGoBack()) {
                        fortRushhDataStore.forrrtttRusshhhhView.goBack()
                    } else if (fortRushhDataStore.forrrtttRusshhhhViList.size > 1) {
                        fortRushhDataStore.forrrtttRusshhhhViList.removeAt(fortRushhDataStore.forrrtttRusshhhhViList.lastIndex)
                        fortRushhDataStore.forrrtttRusshhhhView.destroy()
                        val previousWebView = fortRushhDataStore.forrrtttRusshhhhViList.last()
                        eggLabelAttachWebViewToContainer(previousWebView)
                        fortRushhDataStore.forrrtttRusshhhhView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (fortRushhDataStore.feedMixIsFirstCreate) {
            fortRushhDataStore.feedMixIsFirstCreate = false
            fortRushhDataStore.feedMixContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return fortRushhDataStore.feedMixContainerView
        } else {
            return fortRushhDataStore.feedMixContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (fortRushhDataStore.forrrtttRusshhhhViList.isEmpty()) {
            fortRushhDataStore.forrrtttRusshhhhView = ForrrtttRusshhhhVi(requireContext(), object :
                ForrttttCallBack {
                override fun feedMixHandleCreateWebWindowRequest(forrrtttRusshhhhVi: ForrrtttRusshhhhVi) {
                    fortRushhDataStore.forrrtttRusshhhhViList.add(forrrtttRusshhhhVi)
                    fortRushhDataStore.forrrtttRusshhhhView = forrrtttRusshhhhVi
                    forrrtttRusshhhhVi.eggLabelSetFileChooserHandler { callback ->
                        eggLabelHandleFileChooser(callback)
                    }
                    eggLabelAttachWebViewToContainer(forrrtttRusshhhhVi)
                }

            }, eggLabelWindow = requireActivity().window).apply {
                eggLabelSetFileChooserHandler { callback ->
                    eggLabelHandleFileChooser(callback)
                }
            }
            fortRushhDataStore.forrrtttRusshhhhView.eggLabelFLoad(
                arguments?.getString(FortuneRushLoadingFragment.FEED_MIX_D) ?: ""
            )
            fortRushhDataStore.forrrtttRusshhhhViList.add(fortRushhDataStore.forrrtttRusshhhhView)
            eggLabelAttachWebViewToContainer(fortRushhDataStore.forrrtttRusshhhhView)
        } else {
            fortRushhDataStore.forrrtttRusshhhhViList.forEach { webView ->
                webView.eggLabelSetFileChooserHandler { callback ->
                    eggLabelHandleFileChooser(callback)
                }
            }
            fortRushhDataStore.forrrtttRusshhhhView = fortRushhDataStore.forrrtttRusshhhhViList.last()

            eggLabelAttachWebViewToContainer(fortRushhDataStore.forrrtttRusshhhhView)
        }
    }

    private fun eggLabelHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        eggLabelFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    eggLabelTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }

                1 -> {
                    eggLabelPhoto = forrrttRushhhhhViFun.eggLabelSavePhoto()
                    eggLabelTakePhoto.launch(eggLabelPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                callback?.onReceiveValue(null)
                eggLabelFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun eggLabelAttachWebViewToContainer(w: ForrrtttRusshhhhVi) {
        fortRushhDataStore.feedMixContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            fortRushhDataStore.feedMixContainerView.removeAllViews()
            fortRushhDataStore.feedMixContainerView.addView(w)
        }
    }


}