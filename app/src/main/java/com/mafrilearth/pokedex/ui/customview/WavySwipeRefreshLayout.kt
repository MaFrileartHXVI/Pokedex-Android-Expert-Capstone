package com.mafrilearth.pokedex.ui.customview

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import com.google.android.material.progressindicator.CircularProgressIndicator

class WavySwipeRefreshLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var target: View? = null
    private var loadingIndicator: CircularProgressIndicator? = null
    private var isRefreshing = false
    private var isDragging = false
    private var initialDownY = 0f
    private var activePointerId = -1
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val maxDragDistance = 250f // pixels
    
    var onRefreshListener: (() -> Unit)? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        ensureTarget()
        addLoadingIndicator()
    }

    private fun ensureTarget() {
        if (target == null) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child !is CircularProgressIndicator) {
                    target = child
                    break
                }
            }
        }
    }

    private var loadingContainer: View? = null

    private fun addLoadingIndicator() {
        loadingContainer = android.view.LayoutInflater.from(context)
            .inflate(com.mafrilearth.pokedex.R.layout.layout_pull_refresh_indicator, this, false)
        loadingContainer?.visibility = GONE
        addView(loadingContainer, 0) // Add behind target
        loadingIndicator = loadingContainer?.findViewById<CircularProgressIndicator>(com.mafrilearth.pokedex.R.id.loadingIndicator)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        ensureTarget()
        if (!isEnabled || target?.canScrollVertically(-1) == true || isRefreshing) {
            return false
        }
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = ev.getPointerId(0)
                isDragging = false
                val pointerIndex = ev.findPointerIndex(activePointerId)
                if(pointerIndex >= 0) {
                    initialDownY = ev.getY(pointerIndex)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (activePointerId == -1) return false
                val pointerIndex = ev.findPointerIndex(activePointerId)
                if (pointerIndex < 0) return false
                val y = ev.getY(pointerIndex)
                val yDiff = y - initialDownY
                if (yDiff > touchSlop && !isDragging) {
                    isDragging = true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                activePointerId = -1
            }
        }
        return isDragging
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!isDragging) return super.onTouchEvent(ev)
        
        when (ev.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = ev.findPointerIndex(activePointerId)
                if (pointerIndex < 0) return false
                val y = ev.getY(pointerIndex)
                val dragDistance = (y - initialDownY) * 0.5f // friction
                
                if (dragDistance > 0) {
                    loadingContainer?.visibility = VISIBLE
                    target?.translationY = minOf(dragDistance, maxDragDistance)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (ev.actionMasked == MotionEvent.ACTION_UP) {
                    performClick()
                }
                isDragging = false
                val targetTranslation = target?.translationY ?: 0f
                if (targetTranslation >= maxDragDistance * 0.8f) {
                    setRefreshing(true, notify = true)
                } else {
                    setRefreshing(false, notify = false)
                }
            }
        }
        return true
    }

    private fun animateTargetOffset(targetY: Float) {
        val currentY = target?.translationY ?: 0f
        ValueAnimator.ofFloat(currentY, targetY).apply {
            duration = 300
            addUpdateListener {
                target?.translationY = it.animatedValue as Float
            }
            start()
        }
    }



    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (isRefreshing == refreshing && !notify) return
        isRefreshing = refreshing
        if (isRefreshing) {
            loadingContainer?.visibility = VISIBLE
            animateTargetOffset(maxDragDistance * 0.6f)
            if (notify) {
                onRefreshListener?.invoke()
            }
        } else {
            loadingContainer?.visibility = GONE
            animateTargetOffset(0f)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
