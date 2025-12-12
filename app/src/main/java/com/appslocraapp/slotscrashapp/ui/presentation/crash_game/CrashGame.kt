package com.appslocraapp.slotscrashapp.ui.presentation.crash_game

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appslocraapp.slotscrashapp.R
import com.appslocraapp.slotscrashapp.data.manager.SharedManager
import com.appslocraapp.slotscrashapp.databinding.ActivityCrashGameBinding
import com.appslocraapp.slotscrashapp.databinding.LatestWinItemBinding
import com.enastroekmozhnov.common.BaseViewHolder
import com.enastroekmozhnov.common.RVAdapter
import kotlin.collections.minusAssign
import kotlin.collections.plusAssign
import kotlin.random.Random
import kotlin.text.toInt

class CrashGame : AppCompatActivity() {

    private lateinit var binding: ActivityCrashGameBinding

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private val sharedManager by lazy {
        SharedManager(this)
    }

    private var isGameStarted = false
    private var generatedTime = -1
    private var passedTime = 0
    private var currentX = 1.0
    private var count = 0

    private var latestWinnings = mutableListOf<Double>()

    private val latestWinningsAdapter by lazy {
        RVAdapter { parent, _ ->
            LatestWinVH(
                LayoutInflater.from(parent.context).inflate(R.layout.latest_win_item, parent, false)
            )
        }.apply {
            isFull = false
        }
    }

    private val updateAviator = object : Runnable {
        override fun run() {
            passedTime += 50
            val addX = 1.0 * (passedTime / (20 * 10000f))
            currentX += addX
            binding.coefficient.text = "${currentX.format(2)}x"
            binding.plane.translationY -= 1f
            binding.plane.translationX += 1f
            if (passedTime >= (generatedTime * 1000)) {
                binding.flewaway.isVisible = true
                handler.post(object : Runnable {
                    override fun run() {
                        count += 1
                        binding.plane.translationY -= 4f
                        binding.plane.translationX += 5f
                        if (count <= 100) {
                            handler.postDelayed(this, 20)
                        } else {
                            count = 0
                            handler.removeCallbacks(this)
                        }
                    }
                })
                handler.removeCallbacks(this)
                handler.postDelayed({
                    resetGame()
                }, 1000)
            } else {
                handler.postDelayed(this, 50L)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val windowInsetsCompat = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsCompat.hide(WindowInsetsCompat.Type.navigationBars())
        windowInsetsCompat.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        binding = ActivityCrashGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinBtn.setOnClickListener {
            if (isGameStarted) {
                // claim
                if (!claimed) {
                    claimed = true
                    sharedManager.addPoints((placedBid * currentX).toInt())
                    binding.win.text = (placedBid * currentX).toInt().toString()
                    binding.balance.text = sharedManager.getPoints().toString()
                }
            } else {
                // start
                if (sharedManager.getPoints() >= placedBid) {
                    startNewRoundGame()
                } else {
                    Toast.makeText(this, "Insufficient balance!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.latestWinningsRv.adapter = latestWinningsAdapter
        binding.latestWinningsRv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        binding.balance.text = sharedManager.getPoints().toString()
        binding.homeBtn.setOnClickListener {
            finish()
        }

        binding.totalBet.text = placedBid.toString()

        binding.betPlus.setOnClickListener {
            if (placedBid < 1000) {
                placedBid += 100
                binding.totalBet.text = placedBid.toString()
            }
        }
        binding.betMinus.setOnClickListener {
            if (placedBid > 100) {
                placedBid -= 100
                binding.totalBet.text = placedBid.toString()
            }
        }
        binding.maxBet.setOnClickListener {
            placedBid = 1000
            binding.totalBet.text = placedBid.toString()
        }

    }

    private fun startNewRoundGame() {
        binding.win.text = "0"
        isGameStarted = true
        binding.coefficient.isVisible = true
        generatedTime = generateTime()
        handler.post(updateAviator)
        binding.spinBtn.setImageResource(R.drawable.claim_btn)
    }

    private fun generateTime(): Int {
        var d = Math.random() * 100
        return if (d <= 30) Random.nextInt(2, 5)
        else if (d <= 42) Random.nextInt(5, 10)
        else if (d <= 87) Random.nextInt(10, 15)
        else Random.nextInt(15, 30)
    }

    private fun resetGame() {
        claimed = false
        latestWinnings.add(currentX)
        latestWinningsAdapter.setItems(latestWinnings)
        isGameStarted = false
        passedTime = 0
        currentX = 1.0
        binding.coefficient.isVisible = false
        binding.flewaway.isVisible = false
        binding.plane.translationY = pxToDp(285f)
        binding.plane.translationX = pxToDp(-120f)
        binding.coefficient.text = "1.0x"
        binding.spinBtn.setImageResource(R.drawable.start_btn)
    }

    private var placedBid = 100
    private var claimed = false

}

fun AppCompatActivity.pxToDp(px: Float): Float {
    val r = resources
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        px,
        r.displayMetrics
    )
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

class LatestWinVH(itemView: View) : BaseViewHolder<Double>(itemView) {
    override fun bind(item: Double) {
        val view = LatestWinItemBinding.bind(itemView)
        view.root.text = "${item.format(2)}X"
    }
}