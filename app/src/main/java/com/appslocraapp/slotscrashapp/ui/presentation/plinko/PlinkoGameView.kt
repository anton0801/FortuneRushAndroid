package com.appslocraapp.slotscrashapp.ui.presentation.plinko

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.appslocraapp.slotscrashapp.R
import com.appslocraapp.slotscrashapp.data.manager.SharedManager
import com.appslocraapp.slotscrashapp.databinding.ActivityPlinkoGameViewBinding
import com.appslocraapp.slotscrashapp.ui.views.IPlinkoEventListener
import com.appslocraapp.slotscrashapp.ui.views.Od
import com.enastroekmozhnov.common.RVAdapter


class PlinkoGameView : AppCompatActivity() {

    private lateinit var binding: ActivityPlinkoGameViewBinding

    private val sharedManager by lazy {
        SharedManager(this)
    }

    private var currentBet = 100
    private var currentWin = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val windowInsetsCompat = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsCompat.hide(WindowInsetsCompat.Type.navigationBars())
        windowInsetsCompat.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        binding = ActivityPlinkoGameViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.balance.text = sharedManager.getPoints().toString()

        binding.homeBtn.setOnClickListener {
            finish()
        }

        binding.spinBtn.setOnClickListener {
            dropBall()
        }

        binding.totalBet.text = currentBet.toString()

        binding.betPlus.setOnClickListener {
            if (currentBet < 1000) {
                currentBet += 100
                binding.totalBet.text = currentBet.toString()
            }
        }
        binding.betMinus.setOnClickListener {
            if (currentBet > 100) {
                currentBet -= 100
                binding.totalBet.text = currentBet.toString()
            }
        }
        binding.maxBet.setOnClickListener {
            currentBet = 1000
            binding.totalBet.text = currentBet.toString()
        }

        binding.plinkoBoard.iPlinkoEventListener = object : IPlinkoEventListener {
            override fun onOdRiched(od: Od) {
                sharedManager.addPoints((currentBet * od.cef).toInt())
                binding.balance.text = sharedManager.getPoints().toString()
            }
        }
    }

    private fun dropBall() {
        if (sharedManager.getPoints() > currentBet) {
            binding.plinkoBoard.dropBall()
            sharedManager.minusPoints(currentBet)
            binding.balance.text = sharedManager.getPoints().toString()
        } else {
            Toast.makeText(this, "Insufficient balance!", Toast.LENGTH_SHORT).show()
        }
    }

}