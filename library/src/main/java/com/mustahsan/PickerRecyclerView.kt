package com.mustahsan

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.mustahsan.androidkit.ktx.screenWidth
import com.mustahsan.androidkit.number.dp

class PickerRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val TAG = "PickerRecyclerView"
    private var selectedView: View? = null
    var selectedPosition: Int = -1
    var stoppedPosition: Int = -1
    private val helper = LinearSnapHelper()
    private var onScrollingListener: ((position: Int) -> Unit)? = null
    private var styleCenterView: ((position: Int, centerView: View, otherView: View?) -> Unit)? =
        null
    private var onScrollStoppedListener: ((position: Int) -> Unit)? =
        null

    var itemSize: Float = 0f
        set(value) {
            field = value
            updatePadding(itemSize)
        }

    var itemSpacing: Int = 0
        set(value) {
            field = value
            addItemDecoration(HorizontalSpacingDecoration(1, itemSpacing.toInt(), true))
            updatePadding(itemSize)
        }

    init {
        if (attrs != null) {
            val attributeSet = context.obtainStyledAttributes(attrs, R.styleable.PickerRecyclerView)
            itemSize = attributeSet.getDimension(R.styleable.PickerRecyclerView_itemSize, 60.dp)
            itemSpacing =
                attributeSet.getDimension(R.styleable.PickerRecyclerView_itemSpace, 1.dp).toInt()
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var state = SCROLL_STATE_IDLE
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (SCROLL_STATE_IDLE == newState) {
                    layoutManager?.let { lm ->
                        helper.findSnapView(lm)?.let { targetView ->
                            val position = lm.getPosition(targetView)
                            onScrollStoppedListener?.invoke(position)
                        }
                    }
                }
                state = newState
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                layoutManager?.let { lm ->
                    helper.findSnapView(lm)?.let { targetView ->
                        val position = lm.getPosition(targetView)
                        if (selectedPosition != position) {
                            selectedPosition = position
                            if (state != SCROLL_STATE_IDLE) {
                                onScrollingListener?.invoke(position)
                            }
                            handleSnap(targetView)
                        }
                    }
                }
            }

        })
    }

    private fun handleSnap(targetView: View) {
        /*val distance = helper.calculateDistanceToFinalSnap(lm, targetView)
        if (selectedPosition != -1) {
            val holder =
                findViewHolderForAdapterPosition(selectedPosition) as SnappyTabAdapter.ViewHolder?
            if (holder != null) {
                holder.titleTextView.setTextColor(tabItemTextColorNormal);
            }
        }
        selectedView?.isSelected = false
        val holder = findViewHolderForAdapterPosition(position) as SnappyTabAdapter.ViewHolder?
        holder.titleTextView.setTextColor(tabItemTextColorSelected);*/
        if (styleCenterView == null) {
            targetView.isSelected = true
            selectedView?.isSelected = false
        } else {
            styleCenterView!!.invoke(selectedPosition, targetView, selectedView)
        }
        selectedView = targetView
        //                            selectedView?.isSelected = true
    }

    init {
        val orientation = LayoutManager.getProperties(context, attrs, 0, 0).orientation
        clipToPadding = false
        layoutManager = CenterLayoutManager(context, orientation, false)
        helper.attachToRecyclerView(this)
    }

    fun styleCenterView(listener: (position: Int, centerView: View, otherView: View?) -> Unit) {
        styleCenterView = listener
    }

    fun onScrolling(listener: (position: Int) -> Unit) {
        onScrollingListener = listener
    }

    fun onScrollingStopped(listener: (position: Int) -> Unit) {
        onScrollStoppedListener = listener
    }

    fun updatePadding(itemSize: Float) {
        val padding = ((context?.screenWidth?.div(2)
            ?: itemSpacing) - (itemSpacing / 2) - (itemSize / 2)).toInt()
        setPadding(padding, 0, padding, 0)
    }

}