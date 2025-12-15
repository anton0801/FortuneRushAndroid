package com.appslocraapp.slotscrashapp.ui.presentation

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appslocraapp.slotscrashapp.R
import com.appslocraapp.slotscrashapp.data.manager.DailyRewardManager
import com.appslocraapp.slotscrashapp.data.manager.SharedManager
import com.appslocraapp.slotscrashapp.data.models.DailyReward
import com.appslocraapp.slotscrashapp.databinding.DailyRewardItemListBinding
import com.appslocraapp.slotscrashapp.databinding.FragmentMenuBinding
import com.appslocraapp.slotscrashapp.ui.presentation.crash_game.CrashGame
import com.appslocraapp.slotscrashapp.ui.presentation.plinko.PlinkoGameView
import com.appslocraapp.slotscrashapp.ui.presentation.slots.SlotsActivity
import com.bumptech.glide.Glide
import com.enastroekmozhnov.common.BaseViewHolder
import com.enastroekmozhnov.common.RVAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.random.Random

class MenuFragment : Fragment() {

    private lateinit var binding: FragmentMenuBinding

    private lateinit var dailyRewardManager: DailyRewardManager
    private val dailyRewardsAdapter by lazy {
        RVAdapter { parent, _ ->
            DailyRewardVH(
                LayoutInflater.from(context).inflate(R.layout.daily_reward_item_list, parent, false)
            )
        }.apply {
            size = 7
        }
    }

    private val sharedManager by lazy {
        SharedManager(requireActivity())
    }
    private val prefs by lazy { requireContext().getSharedPreferences("roulette", Context.MODE_PRIVATE) }
    private val KEY_LAST_SPIN by lazy { "last_spin_time" }

    private val TARGET_SECTOR = 6

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMenuBinding.inflate(inflater).let {
        binding = it
        binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dailyRewardManager = DailyRewardManager(view.context)

        loadAvatar()

        binding.dailyRewards.adapter = dailyRewardsAdapter
        binding.dailyRewards.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)

        binding.slot1.setOnClickListener {
            startActivity(Intent(requireActivity(), SlotsActivity::class.java).apply {
                putExtra("slotType", 1)
            })
        }
        binding.slot2.setOnClickListener {
            startActivity(Intent(requireActivity(), SlotsActivity::class.java).apply {
                putExtra("slotType", 2)
            })
        }
        binding.slot3.setOnClickListener {
            startActivity(Intent(requireActivity(), SlotsActivity::class.java).apply {
                putExtra("slotType", 3)
            })
        }
        binding.crashGame.setOnClickListener {
            startActivity(Intent(requireActivity(), CrashGame::class.java))
        }
        binding.crashPlinko.setOnClickListener {
            startActivity(Intent(requireActivity(), PlinkoGameView::class.java))
        }

        binding.spinItRoulette.setOnClickListener {
            if (canSpinToday()) {
                binding.fortuneRouletteLl.isVisible = true
            } else {
                Toast.makeText(
                    requireActivity(),
                    "The wheel of fortune is not available; it becomes available every 24 hours after the last spin! Please try again later!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.closeFortune.setOnClickListener {
            binding.fortuneRouletteLl.isVisible = false
        }

        binding.spinWheel.setOnClickListener {
            binding.spinItRoulettePreview.isVisible = false
            binding.spinItWheel.isVisible = true
            binding.spinWheelIndicator.isVisible = true
            binding.spinWheel.isVisible = false
            binding.spinningLabel.isVisible = true
            binding.freeSpinLabel.isVisible = false
            spinToFixedSector {
                binding.claimButton.isVisible = true
                binding.spinningLabel.isVisible = false
            }
        }
        binding.claimButton.setOnClickListener {
            sharedManager.addPoints(4000)
            prefs.edit().putLong(KEY_LAST_SPIN, System.currentTimeMillis()).apply()
            binding.fortuneRouletteLl.isVisible = false
        }

        binding.totalCoins.text = sharedManager.getPoints().toString()
        binding.totalSpins.text = sharedManager.allSpins().toString()
        binding.maxWin.text = sharedManager.getMaxWin().toString()

        binding.profile.setOnClickListener {
            binding.profileLl.isVisible = true
        }

        binding.closeProfile.setOnClickListener {
            binding.profileLl.isVisible = false
        }

        binding.profileImageEdit.setOnClickListener {
            openPhotoPicker()
        }

        binding.settingsBtn.setOnClickListener {
            binding.settingsLl.isVisible = true
        }
        binding.closeSettings.setOnClickListener {
            binding.settingsLl.isVisible = false
        }

        binding.privacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://fortunerushcasino.com/privacy-policy.html")))
        }

        binding.soundsToggle.setOnClickListener {
            if (prefs.getBoolean("sounds", false)) {
                prefs.edit().putBoolean("sounds", false).apply()
                binding.soundsToggle.setImageResource(R.drawable.switch_off)
            } else {
                prefs.edit().putBoolean("sounds", true).apply()
                binding.soundsToggle.setImageResource(R.drawable.switch_on)
            }
        }

        binding.animationsToggle.setOnClickListener {
            if (prefs.getBoolean("animations", false)) {
                prefs.edit().putBoolean("animations", false).apply()
                binding.animationsToggle.setImageResource(R.drawable.switch_off)
            } else {
                prefs.edit().putBoolean("animations", true).apply()
                binding.animationsToggle.setImageResource(R.drawable.switch_on)
            }
        }

        binding.autoPlayToggle.setOnClickListener {
            if (prefs.getBoolean("auto_play", false)) {
                prefs.edit().putBoolean("auto_play", false).apply()
                binding.autoPlayToggle.setImageResource(R.drawable.switch_off)
            } else {
                prefs.edit().putBoolean("auto_play", true).apply()
                binding.autoPlayToggle.setImageResource(R.drawable.switch_on)
            }
        }
    }

    private val avatarFileName = "user_avatar.jpg" // имя файла в кеше
    private val avatarFile: File by lazy { File(requireActivity().cacheDir, avatarFileName) }

    private val pickPhotoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let { saveAvatarFromUri(it) }
        }
    }

    override fun onResume() {
        super.onResume()

        val rewards = dailyRewardManager.getRewards()
        dailyRewardsAdapter.setItems(rewards)

        val rewardLast = getLastReward()
        if (rewardLast == null) {
            binding.collectBtn.setImageResource(R.drawable.collected_btn_off)
        }

        binding.balance.text = sharedManager.getPoints().toString()

        binding.collectBtn.setOnClickListener {
            val rewardLast = getLastReward()
            if (rewardLast != null) {
                dailyRewardManager.claimReward(rewardLast)
                sharedManager.addPoints(rewardLast)
                dailyRewardsAdapter.setItems(dailyRewardManager.getRewards())
                binding.balance.text = sharedManager.getPoints().toString()
                binding.collectBtn.setImageResource(R.drawable.collected_btn_off)
            }
        }
    }

    private fun getLastReward(): Int? {
        val r = dailyRewardManager.getRewards()
        for (reward in r) {
            if (reward.isActive && !reward.isClaimed) {
                return reward.reward
            }
        }
        return null
    }

    private fun canSpinToday(): Boolean {
        val lastSpin = prefs.getLong(KEY_LAST_SPIN, 0)
        return System.currentTimeMillis() - lastSpin >= 24 * 60 * 60 * 1000
    }

    private fun spinToFixedSector(onEnd: () -> Unit) {
        val sectorsCount = 8 // укажи сколько у тебя секторов на картинке!
        val oneSector = 360f / sectorsCount
        val targetAngle = TARGET_SECTOR * oneSector

        // Делаем 6–8 полных оборотов + точный угол
        val spins = 6 + Random.nextInt(3) // 6, 7 или 8 оборотов
        val finalRotation = spins * 360f + (360f - targetAngle) // против часовой

        val animator = ValueAnimator.ofFloat(0f, finalRotation)
        animator.duration = 4000 // 4 секунды
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener {
            binding.spinItWheel.rotation = it.animatedValue as Float
        }
        animator.doOnEnd {
            binding.spinItWheel.rotation = 360f - targetAngle // точная подгонка
            onEnd()
        }
        animator.start()
    }

    private fun openPhotoPicker() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        intent.type = "image/*"
        pickPhotoLauncher.launch(intent)
    }

    // Сохраняем выбранное фото в локальный кеш
    private fun saveAvatarFromUri(uri: Uri) {
        try {
            requireActivity().contentResolver.openInputStream(uri)?.use { input: InputStream ->
                FileOutputStream(avatarFile).use { output ->
                    input.copyTo(output)
                }
            }
            loadAvatar()
            Toast.makeText(requireActivity(), "Avatar saved", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Загружаем аватар из кеша (если есть)
    private fun loadAvatar() {
        if (avatarFile.exists()) {
            Glide.with(requireActivity())
                .load(avatarFile)
                .into(binding.userImage)
        } else {
            binding.userImage.setImageResource(R.drawable.user_image_default)
        }
    }

    fun deleteAvatar() {
        if (avatarFile.exists()) {
            avatarFile.delete()
            binding.userImage.setImageResource(R.drawable.user_image_default)
        }
    }

}

class DailyRewardVH(itemView: View) : BaseViewHolder<DailyReward>(itemView) {
    override fun bind(item: DailyReward) {
        val view = DailyRewardItemListBinding.bind(itemView)
        view.prize.text = "${item.reward/1000}K"
        view.day.text = "DAY ${item.day}"
        view.prizeIc.setImageResource(if (item.isActive) R.drawable.daily_reward_active else R.drawable.daily_reward_inactive)
    }
}