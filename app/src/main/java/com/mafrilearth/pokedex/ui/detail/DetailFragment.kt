package com.mafrilearth.pokedex.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.mafrilearth.pokedex.core.domain.Resource
import com.mafrilearth.pokedex.model.PokemonUI
import com.mafrilearth.pokedex.databinding.FragmentDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.mafrilearth.pokedex.R
import com.google.android.material.chip.Chip
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment() {

    private val detailViewModel: DetailViewModel by viewModel()
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pokemonId = arguments?.getInt("pokemonId") ?: 0

        val evolutionAdapter = EvolutionAdapter()
        binding.rvEvolutionChain.apply {
            layoutManager = com.google.android.material.carousel.CarouselLayoutManager(com.google.android.material.carousel.HeroCarouselStrategy(), androidx.recyclerview.widget.RecyclerView.HORIZONTAL)
            adapter = evolutionAdapter
        }
        com.google.android.material.carousel.CarouselSnapHelper().attachToRecyclerView(binding.rvEvolutionChain)

        if (pokemonId != 0) {
            detailViewModel.getPokemonDetail(pokemonId).observe(viewLifecycleOwner) { pokemon ->
                if (pokemon != null) {
                    when (pokemon) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            pokemon.data?.let { showDetailPokemon(it) }
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_rounded)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        
        binding.toolbar.inflateMenu(R.menu.menu_detail)
        
        binding.rvDetailImageCarousel.apply {
            layoutManager = com.google.android.material.carousel.CarouselLayoutManager(com.google.android.material.carousel.FullScreenCarouselStrategy())
            setHasFixedSize(true)
        }
        com.google.android.material.carousel.CarouselSnapHelper().attachToRecyclerView(binding.rvDetailImageCarousel)
    }

    private var currentPokemon: PokemonUI? = null

    private fun showDetailPokemon(pokemonDetail: PokemonUI) {
        currentPokemon = pokemonDetail
        val capName = pokemonDetail.name.replaceFirstChar { it.uppercase() }
        binding.apply {
            toolbar.title = capName
            
            tvDetailHeight.text = getString(R.string.text_height_format, pokemonDetail.height / 10.0)
            tvDetailWeight.text = getString(R.string.text_weight_format, pokemonDetail.weight / 10.0)
            tvDetailBaseExp.text = String.format(java.util.Locale.getDefault(), "%d", pokemonDetail.baseExperience)
            if (pokemonDetail.abilities.isNotEmpty()) {
                tvAbilities.text = pokemonDetail.abilities.joinToString(", ") { it.replaceFirstChar(Char::uppercase) }
            } else {
                tvAbilities.text = "-"
            }
            cgTypes.removeAllViews()
            pokemonDetail.types.forEach { type ->
                val chip = layoutInflater.inflate(R.layout.item_chip_assist, cgTypes, false) as Chip
                chip.text = type.replaceFirstChar { it.uppercase() }
                cgTypes.addView(chip)
            }
            if (pokemonDetail.description.isNotEmpty()) {
                tvDescription.text = pokemonDetail.description
                tvDescription.visibility = View.VISIBLE
            } else {
                tvDescription.visibility = View.GONE
            }
            tvSpecies.text = getString(R.string.text_species_format, pokemonDetail.species.replaceFirstChar(Char::uppercase))
            
            if (pokemonDetail.forms.isNotEmpty()) {
                tvForms.text = getString(R.string.text_forms_format, pokemonDetail.forms.joinToString(", ") { it.replaceFirstChar(Char::uppercase) })
            } else {
                tvForms.text = getString(R.string.text_forms_empty)
            }
            if (pokemonDetail.evolutionChain.isNotEmpty()) {
                val evolutionList = pokemonDetail.evolutionChain.map {
                    val parts = it.split("|")
                    if (parts.size == 2) {
                        EvolutionItem(parts[0], parts[1])
                    } else {
                        EvolutionItem(parts[0], "")
                    }
                }
                (rvEvolutionChain.adapter as EvolutionAdapter).setData(evolutionList)
            } else {
                (rvEvolutionChain.adapter as EvolutionAdapter).setData(emptyList())
            }
            cgMoves.removeAllViews()
            val topMoves = pokemonDetail.moves.take(10)
            if (topMoves.isNotEmpty()) {
                topMoves.forEach { move ->
                    val chip = layoutInflater.inflate(R.layout.item_chip_assist, cgMoves, false) as Chip
                    chip.text = move.replaceFirstChar { it.uppercase() }
                    cgMoves.addView(chip)
                }
            } else {
                val emptyChip = layoutInflater.inflate(R.layout.item_chip_assist, cgMoves, false) as Chip
                emptyChip.text = "-"
                cgMoves.addView(emptyChip)
            }
            llStats.removeAllViews()
            pokemonDetail.stats.forEach { stat ->
                val statLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 0, 0, 16) }
                }

                val statHeader = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                val statName = TextView(requireContext()).apply {
                    text = stat.name.uppercase()
                    textSize = 12f
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }

                val statValue = TextView(requireContext()).apply {
                    text = String.format(java.util.Locale.getDefault(), "%d", stat.baseStat)
                    textSize = 12f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                
                statHeader.addView(statName)
                statHeader.addView(statValue)

                val progressBar = layoutInflater.inflate(R.layout.item_stat_progress, statLayout, false) as com.google.android.material.progressindicator.LinearProgressIndicator
                progressBar.max = 255
                progressBar.setProgressCompat(stat.baseStat, true)
                
                statLayout.addView(statHeader)
                statLayout.addView(progressBar)
                llStats.addView(statLayout)
            }
            
            val imageAdapter = DetailImageAdapter(listOf(pokemonDetail.imageUrl))
            rvDetailImageCarousel.adapter = imageAdapter


            var statusFavorite = pokemonDetail.isFavorite
            setStatusFavorite(statusFavorite)
            
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_detail_favorite -> {
                        statusFavorite = !statusFavorite
                        detailViewModel.setFavoritePokemon(pokemonDetail, statusFavorite)
                        setStatusFavorite(statusFavorite)
                        
                        val message = if (statusFavorite) getString(R.string.text_added_to_favorites, pokemonDetail.name.replaceFirstChar(Char::uppercase)) else getString(R.string.text_removed_from_favorites, pokemonDetail.name.replaceFirstChar(Char::uppercase))
                        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setStatusFavorite(statusFavorite: Boolean) {
        val menuItem = binding.toolbar.menu.findItem(R.id.action_detail_favorite)
        if (menuItem != null) {
            if (statusFavorite) {
                menuItem.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_m3)
            } else {
                menuItem.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_outlined)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class EvolutionItem(val name: String, val imageUrl: String)

    inner class EvolutionAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<EvolutionAdapter.EvolutionViewHolder>() {
        private val listData = ArrayList<EvolutionItem>()

        fun setData(newListData: List<EvolutionItem>?) {
            if (newListData == null) return
            val oldSize = listData.size
            listData.clear()
            notifyItemRangeRemoved(0, oldSize)
            listData.addAll(newListData)
            notifyItemRangeInserted(0, listData.size)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvolutionViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel_pokemon, parent, false)
            return EvolutionViewHolder(view)
        }

        override fun getItemCount() = listData.size

        override fun onBindViewHolder(holder: EvolutionViewHolder, position: Int) {
            val data = listData[position]
            holder.bind(data)
        }

        inner class EvolutionViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
            private val tvName: TextView = itemView.findViewById(R.id.tv_pokemon_name)
            private val ivPokemon: android.widget.ImageView = itemView.findViewById(R.id.iv_pokemon_image)

            fun bind(data: EvolutionItem) {
                tvName.text = data.name.replaceFirstChar { it.uppercase() }
                if (data.imageUrl.isNotEmpty()) {
                    val circularProgressDrawable = androidx.swiperefreshlayout.widget.CircularProgressDrawable(itemView.context)
                    circularProgressDrawable.strokeWidth = 5f
                    circularProgressDrawable.centerRadius = 30f
                    circularProgressDrawable.start()

                    Glide.with(itemView.context)
                        .load(data.imageUrl)
                        .placeholder(circularProgressDrawable)
                        .into(ivPokemon)
                }
            }
        }
    }

    inner class DetailImageAdapter(private val images: List<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<DetailImageAdapter.ImageViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel_image, parent, false)
            return ImageViewHolder(view)
        }
        override fun getItemCount() = images.size
        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val circularProgressDrawable = androidx.swiperefreshlayout.widget.CircularProgressDrawable(holder.itemView.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            Glide.with(holder.itemView.context)
                .load(images[position])
                .placeholder(circularProgressDrawable)
                .into(holder.ivImage)
        }
        inner class ImageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
            val ivImage: android.widget.ImageView = itemView.findViewById(R.id.iv_image)
        }
    }
}
