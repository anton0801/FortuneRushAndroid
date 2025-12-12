package com.appslocraapp.slotscrashapp.data.manager

import com.appslocraapp.slotscrashapp.R
import com.appslocraapp.slotscrashapp.ui.views.SlotItem


object SlotData {

    private val leprikonSlots = hashMapOf(
        "slots" to listOf(
            SlotItem(R.drawable.slot_1_symbol_1),
            SlotItem(R.drawable.slot_1_symbol_2),
            SlotItem(R.drawable.slot_1_symbol_3),
            SlotItem(R.drawable.slot_1_symbol_wild),
            SlotItem(R.drawable.slot_1_symbol_5),
            SlotItem(R.drawable.slot_1_symbol_6),
            SlotItem(R.drawable.slot_1_symbol_7),
            SlotItem(R.drawable.slot_1_symbol_8),
            SlotItem(R.drawable.slot_1_symbol_9),
        ),
        "back" to R.drawable.slot_bg_1,
        "slotsBg" to R.drawable.slot_1_machines_bg,
        "home_btn" to R.drawable.slot_1_home_btn,
        "machines_bg" to R.drawable.slot_1_machines_bg,
        "app_bar" to R.drawable.slot_1_app_bar_bg,
        "spin_btn" to R.drawable.slot_1_spin_btn,
        "win_for_spin_bg" to R.drawable.slot_1_win_for_spin_bg
    )

    private val goldSlotData = hashMapOf(
        "slots" to listOf(
            SlotItem(R.drawable.slot_2_symbol_1),
            SlotItem(R.drawable.slot_2_symbol_2),
            SlotItem(R.drawable.slot_2_symbol_3),
            SlotItem(R.drawable.slot_2_symbol_4),
            SlotItem(R.drawable.slot_2_symbol_5),
            SlotItem(R.drawable.slot_2_symbol_6),
            SlotItem(R.drawable.slot_2_symbol_7),
            SlotItem(R.drawable.slot_2_symbol_8),
            SlotItem(R.drawable.slot_2_symbol_9),
        ),
        "back" to R.drawable.slot_2_bg,
        "slotsBg" to R.drawable.slot_2_machines_bg,
        "home_btn" to R.drawable.slot_2_home_btn,
        "machines_bg" to R.drawable.slot_2_machines_bg,
        "app_bar" to R.drawable.slot_2_app_bar,
        "spin_btn" to R.drawable.slot_2_spin_btn,
        "win_for_spin_bg" to R.drawable.slot_2_win_for_spin_bg
    )

    private val jackpotSlotData = hashMapOf(
        "slots" to listOf(
            SlotItem(R.drawable.slot_3_symbol_1),
            SlotItem(R.drawable.slot_3_symbol_2),
            SlotItem(R.drawable.slot_3_symbol_3),
            SlotItem(R.drawable.slot_3_symbol_4),
            SlotItem(R.drawable.slot_3_symbol_5),
            SlotItem(R.drawable.slot_3_symbol_6)
        ),
        "back" to R.drawable.slots_3_bg,
        "slotsBg" to R.drawable.slots_3_machines_bg,
        "home_btn" to R.drawable.slots_3_home_btn,
        "machines_bg" to R.drawable.slots_3_machines_bg,
        "app_bar" to R.drawable.slots_3_app_bar,
        "spin_btn" to R.drawable.slots_3_spin_btn,
        "win_for_spin_bg" to R.drawable.slot_3_win_for_spin_bg
    )

    fun getSlotsData(slotType: Int) = when (slotType) {
        2 -> goldSlotData
        3 -> jackpotSlotData
        else -> leprikonSlots
    }

}