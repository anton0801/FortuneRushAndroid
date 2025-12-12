package com.appslocraapp.slotscrashapp.ui.ie.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.appslocraapp.slotscrashapp.MainActivity
import com.appslocraapp.slotscrashapp.R
import com.appslocraapp.slotscrashapp.databinding.FragmentLoadFortuneBinding
import com.appslocraapp.slotscrashapp.ui.ie.handler.FortuneRushLocalStorageManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class FortuneRushLoadingFragment : Fragment(R.layout.fragment_load_fortune) {
    private lateinit var chickHealthLoadBinding: FragmentLoadFortuneBinding

    private val fortRushhhLoadViewModel by viewModel<FortRushhhLoadViewModel>()

    private val fortuneRushLocalStorageManager by inject<FortuneRushLocalStorageManager>()

    private var eggLabelUrl = ""

    private val chickHealthRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            eggLabelNavigateToSuccess(eggLabelUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                fortuneRushLocalStorageManager.feedMixNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                eggLabelNavigateToSuccess(eggLabelUrl)
            } else {
                eggLabelNavigateToSuccess(eggLabelUrl)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 999 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            eggLabelNavigateToSuccess(eggLabelUrl)
        } else {
            // твой код на отказ
            eggLabelNavigateToSuccess(eggLabelUrl)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chickHealthLoadBinding = FragmentLoadFortuneBinding.bind(view)

        chickHealthLoadBinding.feedMixGrandButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val eggLabelPermission = Manifest.permission.POST_NOTIFICATIONS
                chickHealthRequestNotificationPermission.launch(eggLabelPermission)
                fortuneRushLocalStorageManager.feedMixNotificationRequestedBefore = true
            } else {
                eggLabelNavigateToSuccess(eggLabelUrl)
                fortuneRushLocalStorageManager.feedMixNotificationRequestedBefore = true
            }
        }

        chickHealthLoadBinding.feedMixSkipButton.setOnClickListener {
            fortuneRushLocalStorageManager.feedMixNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            eggLabelNavigateToSuccess(eggLabelUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                fortRushhhLoadViewModel.chickHealthHomeScreenState.collect {
                    when (it) {
                        is FortRushhhLoadViewModel.FeedMixHomeScreenState.FeedMixLoading -> {
                        }

                        is FortRushhhLoadViewModel.FeedMixHomeScreenState.FeedMixError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is FortRushhhLoadViewModel.FeedMixHomeScreenState.FeedMixSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val eggLabelPermission = Manifest.permission.POST_NOTIFICATIONS
                                val eggLabelPermissionRequestedBefore =
                                    fortuneRushLocalStorageManager.feedMixNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(
                                        requireContext(),
                                        eggLabelPermission
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    eggLabelNavigateToSuccess(it.data)
                                } else if (!eggLabelPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > fortuneRushLocalStorageManager.feedMixNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    chickHealthLoadBinding.feedMixNotiGroup.visibility = View.VISIBLE
                                    chickHealthLoadBinding.feedMixLoadingGroup.visibility = View.GONE
                                    eggLabelUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(eggLabelPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > fortuneRushLocalStorageManager.feedMixNotificationRequest) {
                                        chickHealthLoadBinding.feedMixNotiGroup.visibility =
                                            View.VISIBLE
                                        chickHealthLoadBinding.feedMixLoadingGroup.visibility =
                                            View.GONE
                                        eggLabelUrl = it.data
                                    } else {
                                        eggLabelNavigateToSuccess(it.data)
                                    }
                                } else {
                                    eggLabelNavigateToSuccess(it.data)
                                }
                            } else {
                                eggLabelNavigateToSuccess(it.data)
                            }
                        }

                        FortRushhhLoadViewModel.FeedMixHomeScreenState.FeedMixNotInternet -> {
                            chickHealthLoadBinding.feedMixStateGroup.visibility = View.VISIBLE
                            chickHealthLoadBinding.feedMixLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun eggLabelNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_fortuneRushLoadingFragment_to_fortuneRushV,
            bundleOf(FEED_MIX_D to data)
        )
    }

    companion object {
        const val FEED_MIX_D = "eggLabelData"
    }
}