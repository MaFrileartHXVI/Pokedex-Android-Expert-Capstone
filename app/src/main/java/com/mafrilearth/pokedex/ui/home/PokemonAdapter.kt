package com.mafrilearth.pokedex.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mafrilearth.pokedex.model.PokemonUI
import com.mafrilearth.pokedex.databinding.ItemCarouselPokemonBinding
import java.util.ArrayList
import android.widget.ImageView
import android.widget.TextView
import com.mafrilearth.pokedex.R

fun ImageView.loadImage(url: String) {
    val circularProgressDrawable = androidx.swiperefreshlayout.widget.CircularProgressDrawable(this.context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()

    Glide.with(this.context)
        .load(url)
        .placeholder(circularProgressDrawable)
        .into(this)
}
class PokemonAdapter(private val isSearch: Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listData = ArrayList<PokemonUI>()
    private var originalList = ArrayList<PokemonUI>()
    var onItemClick: ((PokemonUI, View) -> Unit)? = null
    var onDeleteClick: ((PokemonUI) -> Unit)? = null


    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListData: List<PokemonUI>?) {
        if (newListData == null) return
        if (originalList == newListData) return
        originalList.clear()
        originalList.addAll(newListData)
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isSearch) {
            SearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_pokemon, parent, false))
        } else {
            CarouselViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_carousel_pokemon, parent, false))
        }
    }

    override fun getItemCount() = listData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = listData[position]
        if (holder is CarouselViewHolder) {
            holder.bind(data)
        } else if (holder is SearchViewHolder) {
            holder.bind(data)
        }
    }

    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemCarouselPokemonBinding.bind(itemView)
        fun bind(data: PokemonUI) {
            with(binding) {
                ivPokemonImage.loadImage(data.imageUrl)
                tvPokemonName.text = data.name.replaceFirstChar { it.uppercase() }

            }
            itemView.setOnClickListener {
                onItemClick?.invoke(data, binding.ivPokemonImage)
            }
        }
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardForeground: View = itemView.findViewById(R.id.card_foreground)
        private val layoutDeleteBg: View = itemView.findViewById(R.id.layout_delete_bg)
        private val ivDeleteIcon: View = itemView.findViewById(R.id.iv_delete_icon)
        private val btnMore: View = itemView.findViewById(R.id.btn_more)
        private val ivPokemonImage: ImageView = itemView.findViewById(R.id.iv_pokemon_image)
        private val tvPokemonName: TextView = itemView.findViewById(R.id.tv_pokemon_name)
        
        private var springAnim: androidx.dynamicanimation.animation.SpringAnimation? = null
        private val density = itemView.context.resources.displayMetrics.density
        private val gapPx = density * 8f
        private val minPillWidthPx = density * 56f
        private val marginEndPx = 0f
        private val snapDistancePx = gapPx + minPillWidthPx + marginEndPx

        fun bind(data: PokemonUI) {
            ivPokemonImage.loadImage(data.imageUrl)
            tvPokemonName.text = data.name.replaceFirstChar { it.uppercase() }
            if (springAnim == null) {
                springAnim = androidx.dynamicanimation.animation.SpringAnimation(cardForeground, androidx.dynamicanimation.animation.DynamicAnimation.TRANSLATION_X, 0f).apply {
                    spring = androidx.dynamicanimation.animation.SpringForce(0f).apply {
                        stiffness = androidx.dynamicanimation.animation.SpringForce.STIFFNESS_LOW
                        dampingRatio = androidx.dynamicanimation.animation.SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
                    }
                    addUpdateListener { _, value, _ ->
                        if (value < 0) {
                            if (value > -snapDistancePx) {
                                layoutDeleteBg.layoutParams = layoutDeleteBg.layoutParams.apply { width = minPillWidthPx.toInt() }
                                layoutDeleteBg.translationX = value + gapPx + minPillWidthPx
                            } else {
                                layoutDeleteBg.layoutParams = layoutDeleteBg.layoutParams.apply { width = (-value - gapPx - marginEndPx).toInt() }
                                layoutDeleteBg.translationX = -marginEndPx
                            }
                        } else {
                            layoutDeleteBg.layoutParams = layoutDeleteBg.layoutParams.apply { width = minPillWidthPx.toInt() }
                            layoutDeleteBg.translationX = gapPx + minPillWidthPx
                        }
                    }
                }
            }

            var initialX = 0f
            var initialTouchX = 0f

            cardForeground.setOnTouchListener { _, event ->
                when (event.actionMasked) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        springAnim?.cancel()
                        initialX = cardForeground.translationX
                        initialTouchX = event.rawX
                        true
                    }
                    android.view.MotionEvent.ACTION_MOVE -> {
                        val dx = event.rawX - initialTouchX
                        var targetX = initialX + dx
                        if (targetX < -snapDistancePx) {
                            val overscroll = targetX + snapDistancePx
                            targetX = -snapDistancePx + (overscroll * 0.3f)
                        } else if (targetX > 0) {
                            targetX *= 0.3f // Friction when pulling right
                        }
                        
                        cardForeground.translationX = targetX
                        
                        if (targetX < 0) {
                            if (targetX > -snapDistancePx) {
                                layoutDeleteBg.layoutParams = layoutDeleteBg.layoutParams.apply { width = minPillWidthPx.toInt() }
                                layoutDeleteBg.translationX = targetX + gapPx + minPillWidthPx
                            } else {
                                layoutDeleteBg.layoutParams = layoutDeleteBg.layoutParams.apply { width = (-targetX - gapPx - marginEndPx).toInt() }
                                layoutDeleteBg.translationX = -marginEndPx
                            }
                        }
                        kotlin.math.abs(dx) > 10f
                    }
                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        val finalX = cardForeground.translationX
                        if (finalX < -snapDistancePx / 2) {
                            springAnim?.animateToFinalPosition(-snapDistancePx)
                        } else {
                            springAnim?.animateToFinalPosition(0f)
                        }
                        if (kotlin.math.abs(event.rawX - initialTouchX) < 10f) {
                            cardForeground.performClick()
                        }
                        true
                    }
                    else -> false
                }
            }

            cardForeground.setOnClickListener {
                if (cardForeground.translationX < -10f) {
                    springAnim?.animateToFinalPosition(0f)
                } else {
                    onItemClick?.invoke(data, ivPokemonImage)
                }
            }

            btnMore.setOnClickListener {
                if (cardForeground.translationX == 0f) {
                    springAnim?.animateToFinalPosition(-snapDistancePx)
                } else {
                    springAnim?.animateToFinalPosition(0f)
                }
            }

            val deleteClickListener = View.OnClickListener {
                springAnim?.animateToFinalPosition(0f)
                onDeleteClick?.invoke(data)
            }
            layoutDeleteBg.setOnClickListener(deleteClickListener)
            ivDeleteIcon.setOnClickListener(deleteClickListener)
        }
    }
}
