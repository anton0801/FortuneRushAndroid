package com.appslocraapp.slotscrashapp.ui.views

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.inflate
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appslocraapp.slotscrashapp.R
import com.enastroekmozhnov.common.BaseViewHolder
import com.enastroekmozhnov.common.RVAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LineChart(
    val value: Int,
    val linesCount: Int,
//    @DrawableRes
//    val linesResource: Int
)

data class SlotItem(
    val resId: Int
)

data class SlotMachineItem(
    val res: Int,
    val coefficient: List<Int>
)

class SlotMachineItemViewHolder(itemView: View) : BaseViewHolder<SlotItem>(itemView) {
    lateinit var slotItem: SlotItem
    override fun bind(item: SlotItem) {
        slotItem = item
        (itemView as AppCompatImageView).setImageResource(item.resId)
    }
}

class SlotView : FrameLayout {

    interface SlotEvents {
        fun onStopSlots(
            slotId: Int,
            positionInRv: Int,
            slotItems: List<SlotItem>
        )
    }

    private lateinit var slotMachine: NonTouchableRecyclerView
    private val rvAdapter by lazy {
        RVAdapter { parent, _ ->
            SlotMachineItemViewHolder(
                LayoutInflater.from(context).inflate(R.layout.slot_item, parent, false)
            )
        }
    }

    private var iSlotEvent: SlotEvents? = null
    val slots = mutableListOf<SlotItem>()
    private var scrollPosition = -1
    private var prevScrollPos = -1

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        init()
    }

    fun setISlotEvent(iSlotEvent: SlotEvents) {
        this.iSlotEvent = iSlotEvent
    }

    private fun init() {
        inflate(context, R.layout.slots_wrapper, this)
        slotMachine = findViewById(R.id.slot_machine)
        slotMachine.apply {
            setHasFixedSize(true)
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            val lm = layoutManager as LinearLayoutManager
                            val firstVisible: Int = lm.findFirstVisibleItemPosition()
                            val lastVisible: Int = lm.findLastVisibleItemPosition()
                            val itemsCount = lastVisible - firstVisible + 1

                            val screenCenter = context.resources.displayMetrics.widthPixels / 2

                            var minCenterOffset = Int.MAX_VALUE

                            var middleItemIndex = 0

                            for (index in 0 until itemsCount) {
                                val listItem: View = lm.getChildAt(index) ?: return@postDelayed
                                val leftOffset: Int = listItem.left
                                val rightOffset: Int = listItem.right
                                val centerOffset =
                                    Math.abs(leftOffset - screenCenter) + Math.abs(rightOffset - screenCenter)
                                if (minCenterOffset > centerOffset) {
                                    minCenterOffset = centerOffset
                                    middleItemIndex = index + firstVisible
                                }
                            }
                            val item1 =
                                (findViewHolderForAdapterPosition(middleItemIndex) as SlotMachineItemViewHolder).slotItem
                            val item2 =
                                (findViewHolderForAdapterPosition(middleItemIndex + 1) as SlotMachineItemViewHolder).slotItem
                            val item3 =
                                (findViewHolderForAdapterPosition(middleItemIndex + 2) as SlotMachineItemViewHolder).slotItem
                            iSlotEvent?.onStopSlots(
                                this@SlotView.tag.toString().toInt(),
                                (middleItemIndex + 1),
                                listOf(item1, item2, item3)
                            )
                        }, 500)
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                }
            })
        }
    }

    fun setSlots(s: List<SlotItem>) {
        slots.clear()
        slots.addAll(s.shuffled())
        rvAdapter.setItems(slots)
        slotMachine.layoutManager?.scrollToPosition(50)
    }

    fun getScrollPosition() = scrollPosition

    fun scaleAnimationSlotItem(itemPosition: Int) {
        val holder = slotMachine.findViewHolderForAdapterPosition(itemPosition)
        if (holder != null) {
            scaleAnimation(holder.itemView)
        }
    }

    private fun scaleAnimation(view: View) {
        var needScale = true
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0 until 7) {
                if (needScale) {
                    view.animate().scaleX(1.3f).scaleY(1.3f).duration = 100
                    needScale = false
                } else {
                    view.animate().scaleX(1f).scaleY(1f).duration = 100
                    needScale = true
                }
                if (i == 6) {
                    view.animate().scaleX(1f).scaleY(1f).duration = 100
                }
                delay(100)
            }
        }
    }

    fun spin(s: Int) {
        scrollPosition = s
        prevScrollPos = s
        slotMachine.smoothScrollToPosition(s)
    }

}

class NonTouchableRecyclerView : RecyclerView {

    override fun computeVerticalScrollRange(): Int {
        return 0
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return false
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}