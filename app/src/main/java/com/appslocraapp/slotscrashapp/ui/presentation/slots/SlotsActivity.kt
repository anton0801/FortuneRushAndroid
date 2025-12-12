package com.appslocraapp.slotscrashapp.ui.presentation.slots

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.appslocraapp.slotscrashapp.R
import com.appslocraapp.slotscrashapp.data.manager.CoefficientsRep
import com.appslocraapp.slotscrashapp.data.manager.SharedManager
import com.appslocraapp.slotscrashapp.data.manager.SlotData
import com.appslocraapp.slotscrashapp.databinding.ActivitySlotsBinding
import com.appslocraapp.slotscrashapp.ui.views.LineChart
import com.appslocraapp.slotscrashapp.ui.views.SlotItem
import com.appslocraapp.slotscrashapp.ui.views.SlotView
import kotlin.random.Random
import kotlin.text.compareTo
import kotlin.times
import kotlin.toString

class SlotsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySlotsBinding

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val sharedManager by lazy {
        SharedManager(this)
    }

    private var slotType = 1

    private var currentLinesCount = 1
    private var lineChart: LineChart = LineChart(5, 5)
    private var currentBet = 100
    private var currentWin = 0

    private var slotsValues = hashMapOf<Int, List<SlotItem>>()
    private var slotItemPositions = hashMapOf<Int, Map<Int, Int>>()
    private var slotsStopped = -1
    private var coincidencesStartIndex = -1

    private var slotScrollPositions = hashMapOf<Int, Int>()
    private var slotPrevScrollPositions = hashMapOf<Int, Int>()
    private var originalSlots = listOf<SlotItem>()
    private var slots = listOf<SlotItem>()
    private var maxScrollPos = 0
    private var invertScrollPos = false

    private var slotsEvent = object : SlotView.SlotEvents {
        override fun onStopSlots(
            slotId: Int,
            positionInRv: Int,
            slotItems: List<SlotItem>
        ) {
            checkLine5(slotId, positionInRv, slotItems)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val windowInsetsCompat = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsCompat.hide(WindowInsetsCompat.Type.navigationBars())
        windowInsetsCompat.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        binding = ActivitySlotsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        slotType = intent.getIntExtra("slotType", 1)
        val slotsData = SlotData.getSlotsData(slotType)

        binding.balance.text = sharedManager.getPoints().toString()

        with(slotsData) {
            setSlotsImages(get("slots") as List<SlotItem>)
            binding.root.setBackgroundResource(get("back") as Int)
            binding.slotBackField.setImageResource(get("machines_bg") as Int)
            binding.appBarBg.setImageResource(get("app_bar") as Int)
            binding.bottomBarBg.setImageResource(get("app_bar") as Int)
            binding.homeBtn.setImageResource(get("home_btn") as Int)
            binding.spinBtn.setImageResource(get("spin_btn") as Int)
            binding.winForSpinBg.setImageResource(get("win_for_spin_bg") as Int)

            if (slotType == 2 || slotType == 3) {
                binding.slotBackField.isVisible = false
                binding.slotBackField2.isVisible = true
                binding.slotBackField2.setImageResource(get("machines_bg") as Int)
            }
        }

        binding.slotMachine1.setISlotEvent(slotsEvent)
        binding.slotMachine2.setISlotEvent(slotsEvent)
        binding.slotMachine3.setISlotEvent(slotsEvent)
        binding.slotMachine4.setISlotEvent(slotsEvent)
        binding.slotMachine5.setISlotEvent(slotsEvent)

        binding.homeBtn.setOnClickListener {
            finish()
        }

        binding.spinBtn.setOnClickListener {
            spinSlots()
        }

        binding.totalBet.text = currentBet.toString()

        binding.betPlus.setOnClickListener {
            if (currentBet < 1000) {
                currentBet += 100
                //binding.totalBet.text = (lineChart.linesCount * currentBet).toString()
                binding.totalBet.text = currentBet.toString()
            }
        }
        binding.betMinus.setOnClickListener {
            if (currentBet > 100) {
                currentBet -= 100
                //binding.totalBet.text = (lineChart.linesCount * currentBet).toString()
                binding.totalBet.text = currentBet.toString()
            }
        }
        binding.maxBet.setOnClickListener {
            currentBet = 1000
            //binding.totalBet.text = (lineChart.linesCount * currentBet).toString()
            binding.totalBet.text = currentBet.toString()
        }
    }

    private fun spinSlots() {
        slotsStopped = 0
        slotsValues.clear()
        slotItemPositions.clear()
        val credits = sharedManager.getPoints()
        val totalBet = currentBet
        if (credits >= totalBet) {
            binding.winForSpin.text = "0"
            if (slotPrevScrollPositions.isEmpty()) {
                slotPrevScrollPositions = hashMapOf(
                    0 to 50,
                    1 to 50,
                    2 to 50,
                    3 to 50,
                    4 to 50,
                )
            }
            if (!invertScrollPos) {
                slotScrollPositions = hashMapOf(
                    0 to slotPrevScrollPositions[0]!! + Random.nextInt(10, 40),
                    1 to slotPrevScrollPositions[1]!! + Random.nextInt(10, 40),
                    2 to slotPrevScrollPositions[2]!! + Random.nextInt(10, 40),
                    3 to slotPrevScrollPositions[3]!! + Random.nextInt(10, 40),
                    4 to slotPrevScrollPositions[4]!! + Random.nextInt(10, 40)
                )
            } else {
                slotScrollPositions = hashMapOf(
                    0 to slotPrevScrollPositions[0]!! - Random.nextInt(10, 40),
                    1 to slotPrevScrollPositions[1]!! - Random.nextInt(10, 40),
                    2 to slotPrevScrollPositions[2]!! - Random.nextInt(10, 40),
                    3 to slotPrevScrollPositions[3]!! - Random.nextInt(10, 40),
                    4 to slotPrevScrollPositions[4]!! - Random.nextInt(10, 40)
                )
            }
            slotPrevScrollPositions = slotScrollPositions

            maxScrollPos = slotScrollPositions.maxOf { it.value }

            if (maxScrollPos >= 1450) {
                invertScrollPos = true
            } else if (maxScrollPos <= 50) {
                invertScrollPos = false
            }

            sharedManager.addSpin()
            binding.winForSpin.text = "0"
            binding.slotMachine1.spin(slotScrollPositions[0]!!)
            handler.postDelayed({
                binding.slotMachine2.spin(slotScrollPositions[1]!!)
            }, 100)
            handler.postDelayed({
                binding.slotMachine3.spin(slotScrollPositions[2]!!)
            }, 200)
            handler.postDelayed({
                binding.slotMachine4.spin(slotScrollPositions[3]!!)
            }, 300)
            handler.postDelayed({
                binding.slotMachine5.spin(slotScrollPositions[4]!!)
            }, 400)
            binding.balance.text = (credits - totalBet).toString()
            sharedManager.minusPoints(totalBet)
            disableBtns()
        } else {
            Toast.makeText(this, "Insufficient balance!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun disableBtns() {
        binding.spinBtn.isEnabled = false
        binding.betPlus.isEnabled = false
        binding.betMinus.isEnabled = false
        binding.maxBet.isEnabled = false
    }

    private fun enableBtns() {
        binding.spinBtn.isEnabled = true
        binding.betPlus.isEnabled = true
        binding.betMinus.isEnabled = true
        binding.maxBet.isEnabled = true
    }

    private fun setSlotsImages(slotImages: List<SlotItem>) {
        originalSlots = slotImages
        slots = originalSlots.shuffled()

        binding.slotMachine1.setSlots(slots)
        binding.slotMachine2.setSlots(slots)
        binding.slotMachine3.setSlots(slots)
        binding.slotMachine4.setSlots(slots)
        binding.slotMachine5.setSlots(slots)
    }

    private fun checkLine5(
        slotId: Int,
        positionInRv: Int,
        slotItems: List<SlotItem>
    ) {
        slotsValues[slotId] = slotItems
        slotsStopped += 1
        slotItemPositions[slotId] = mapOf(
            0 to positionInRv - 1,
            1 to positionInRv,
            2 to positionInRv + 1
        )
        if (slotsStopped >= 5) {
            enableBtns()
            var addPoints = 0
            val lines = hashMapOf<Int, List<Int>>()
            lines[0] = listOf(0, 0, 0, 0, 0)
            lines[1] = listOf(1, 1, 1, 1, 1)
            lines[2] = listOf(2, 2, 2, 2, 2)
            lines[3] = listOf(0, 1, 2, 1, 0)
            lines[4] = listOf(2, 1, 0, 1, 2)
            // check in first line
            lines.forEach { line ->
                if (checkAllInLine(line.value)) {
                    addPoints += calculatePoints(
                        slotItems[line.value[2]].resId,
                        currentBet,
                        0
                    )
                    binding.slotMachine1.scaleAnimationSlotItem(slotItemPositions[0]!![line.value[0]]!!)
                    handler.postDelayed({
                        binding.slotMachine2.scaleAnimationSlotItem(slotItemPositions[1]!![line.value[1]]!!)
                    }, 50)
                    handler.postDelayed({
                        binding.slotMachine3.scaleAnimationSlotItem(slotItemPositions[2]!![line.value[2]]!!)
                    }, 100)
                    handler.postDelayed({
                        binding.slotMachine4.scaleAnimationSlotItem(slotItemPositions[3]!![line.value[3]]!!)
                    }, 150)
                    handler.postDelayed({
                        binding.slotMachine5.scaleAnimationSlotItem(slotItemPositions[4]!![line.value[4]]!!)
                    }, 200)
                } else if (check4InLine(line.value)) {
                    addPoints += calculatePoints(
                        slotItems[line.value[2]].resId,
                        currentBet,
                        1
                    )


                    binding.slotMachine2.scaleAnimationSlotItem(slotItemPositions[1]!![line.value[1]]!!)
                    handler.postDelayed({
                        binding.slotMachine3.scaleAnimationSlotItem(slotItemPositions[2]!![line.value[2]]!!)
                    }, 50)
                    handler.postDelayed({
                        binding.slotMachine4.scaleAnimationSlotItem(slotItemPositions[3]!![line.value[3]]!!)
                    }, 100)
                    handler.postDelayed({
                        if (coincidencesStartIndex == 0) {
                            binding.slotMachine1.scaleAnimationSlotItem(slotItemPositions[0]!![line.value[0]]!!)
                        } else {
                            binding.slotMachine5.scaleAnimationSlotItem(slotItemPositions[4]!![line.value[4]]!!)
                        }
                    }, 150)
                } else if (check3InLine(line.value)) {
                    addPoints += calculatePoints(
                        slotItems[line.value[2]].resId,
                        currentBet,
                        2
                    )

                    binding.slotMachine3.scaleAnimationSlotItem(slotItemPositions[2]!![line.value[2]]!!)
                    when (coincidencesStartIndex) {
                        0 -> {
                            handler.postDelayed({
                                handler.postDelayed({
                                    binding.slotMachine1.scaleAnimationSlotItem(
                                        slotItemPositions[0]!![line.value[0]]!!
                                    )
                                }, 50)
                            }, 50)
                            try {
                                handler.postDelayed({
                                    handler.postDelayed({
                                        binding.slotMachine2.scaleAnimationSlotItem(
                                            slotItemPositions[1]!![line.value[1]]!!
                                        )
                                    }, 100)
                                }, 100)
                            } catch (e: NullPointerException) {
                                e.printStackTrace()
                            }
                        }

                        1 -> {
                            handler.postDelayed({
                                handler.postDelayed({
                                    binding.slotMachine2.scaleAnimationSlotItem(
                                        slotItemPositions[1]!![line.value[1]]!!
                                    )
                                }, 50)
                            }, 50)
                            handler.postDelayed({
                                handler.postDelayed({
                                    binding.slotMachine4.scaleAnimationSlotItem(
                                        slotItemPositions[3]!![line.value[3]]!!
                                    )
                                }, 100)
                            }, 100)
                        }

                        2 -> {
                            handler.postDelayed({
                                handler.postDelayed({
                                    binding.slotMachine4.scaleAnimationSlotItem(
                                        slotItemPositions[3]!![line.value[3]]!!
                                    )
                                }, 50)
                            }, 50)
                            try {
                                handler.postDelayed({
                                    handler.postDelayed({
                                        binding.slotMachine5.scaleAnimationSlotItem(
                                            slotItemPositions[4]!![line.value[4]]!!
                                        )
                                    }, 100)
                                }, 100)
                            } catch (e: NullPointerException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
            if (addPoints > 0) {
                actionWin(addPoints)
            } else {
                actionLose()
            }
        }
    }

    private fun checkAllInLine(slotItemIndexes: List<Int>): Boolean {
        return slotsValues[0]?.getOrNull(slotItemIndexes[0])?.resId == slotsValues[1]?.getOrNull(
            slotItemIndexes[1]
        )?.resId &&
                slotsValues[1]?.getOrNull(slotItemIndexes[1])?.resId == slotsValues[2]?.getOrNull(
            slotItemIndexes[2]
        )?.resId &&
                slotsValues[2]?.getOrNull(slotItemIndexes[2])?.resId == slotsValues[3]?.getOrNull(
            slotItemIndexes[3]
        )?.resId &&
                slotsValues[3]?.getOrNull(slotItemIndexes[3])?.resId == slotsValues[4]?.getOrNull(
            slotItemIndexes[4]
        )?.resId
    }

    private fun check4InLine(slotItemIndexes: List<Int>): Boolean {
        return if ((slotsValues[0]?.getOrNull(slotItemIndexes[0])?.resId == slotsValues[1]?.getOrNull(
                slotItemIndexes[1]
            )?.resId &&
                    slotsValues[1]?.getOrNull(slotItemIndexes[1])?.resId == slotsValues[2]?.getOrNull(
                slotItemIndexes[2]
            )?.resId &&
                    slotsValues[3]?.getOrNull(slotItemIndexes[3])?.resId == slotsValues[2]?.getOrNull(
                slotItemIndexes[2]
            )?.resId)
        ) {
            coincidencesStartIndex = 0
            true
        } else if ((slotsValues[1]?.getOrNull(slotItemIndexes[1])?.resId == slotsValues[2]?.getOrNull(
                slotItemIndexes[2]
            )?.resId &&
                    slotsValues[3]?.getOrNull(slotItemIndexes[3])?.resId == slotsValues[2]?.getOrNull(
                slotItemIndexes[2]
            )?.resId &&
                    slotsValues[3]?.getOrNull(slotItemIndexes[3])?.resId == slotsValues[4]?.getOrNull(
                slotItemIndexes[4]
            )?.resId)
        ) {
            coincidencesStartIndex = 1
            true
        } else {
            false
        }
    }

    private fun check3InLine(slotItemIndexes: List<Int>): Boolean {
        return if ((slotsValues[0]?.getOrNull(slotItemIndexes[0])?.resId == slotsValues[1]?.getOrNull(
                slotItemIndexes[1]
            )?.resId &&
                    slotsValues[1]?.getOrNull(slotItemIndexes[1])?.resId == slotsValues[2]?.getOrNull(
                slotItemIndexes[2]
            )?.resId)
        ) {
            coincidencesStartIndex = 0
            true
        } else if ((slotsValues[1]?.getOrNull(slotItemIndexes[1])?.resId == slotsValues[2]?.getOrNull(
                slotItemIndexes[2]
            )?.resId &&
                    slotsValues[2]?.getOrNull(slotItemIndexes[2])?.resId == slotsValues[3]?.getOrNull(
                slotItemIndexes[3]
            )?.resId)
        ) {
            coincidencesStartIndex = 1
            true
        } else if (
            (slotsValues[2]?.getOrNull(slotItemIndexes[2])?.resId == slotsValues[3]?.getOrNull(
                slotItemIndexes[3]
            )?.resId &&
                    slotsValues[3]?.getOrNull(slotItemIndexes[3])?.resId == slotsValues[4]?.getOrNull(
                slotItemIndexes[4]
            )?.resId)
        ) {
            coincidencesStartIndex = 2
            true
        } else {
            false
        }
    }

    private fun calculatePoints(res: Int?, bet: Int, coefficientForUse: Int): Int {
        val r = res ?: R.drawable.slot_1_symbol_1
        val coefficients = CoefficientsRep.coefficients.find { it.res == r }!!
        val coefficient = coefficients.coefficient[coefficientForUse]
        return bet * coefficient
    }

    private fun actionLose() {
        binding.winForSpin.text = "0"
    }

    private var sessionWinAll = 0

    private fun actionWin(winPoints: Int) {
        sessionWinAll += winPoints
        binding.win.text = sessionWinAll.toString()
        binding.winForSpin.text = winPoints.toString()
        currentWin = winPoints
        sharedManager.addPoints(winPoints)
        binding.balance.text = sharedManager.getPoints().toString()

        if (currentWin > sharedManager.getMaxWin()) {
            sharedManager.setMaxWin(currentWin)
        }
    }

}