package com.mafrilearth.pokedex.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mafrilearth.pokedex.core.domain.Resource
import com.mafrilearth.pokedex.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.mafrilearth.pokedex.R

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var badgeDrawable: com.google.android.material.badge.BadgeDrawable? = null
    private var pokemonAdapter: PokemonAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @androidx.annotation.OptIn(com.google.android.material.badge.ExperimentalBadgeUtils::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (pokemonAdapter == null) {
            pokemonAdapter = PokemonAdapter()
            pokemonAdapter?.onItemClick = { selectedData, view ->
                val bundle = Bundle().apply {
                    putInt("pokemonId", selectedData.id)
                }
                findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
            }
        }

        binding.toolbar.inflateMenu(R.menu.menu_home)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_favorite -> {
                    findNavController().navigate(R.id.action_homeFragment_to_favorite_nav_graph)
                    true
                }
                else -> false
            }
        }

        binding.rvPokemon.apply {
            layoutManager = com.google.android.material.carousel.CarouselLayoutManager(com.google.android.material.carousel.FullScreenCarouselStrategy(), androidx.recyclerview.widget.RecyclerView.VERTICAL)
            setHasFixedSize(true)
        }
        com.google.android.material.carousel.CarouselSnapHelper().attachToRecyclerView(binding.rvPokemon)

        homeViewModel.pokemon.observe(viewLifecycleOwner) { pokemon ->
            if (pokemon != null) {
                when (pokemon) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        pokemonAdapter?.setData(pokemon.data)
                        if (binding.rvPokemon.adapter == null) {
                            binding.rvPokemon.adapter = pokemonAdapter
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }

        homeViewModel.favoritePokemonCount.observe(viewLifecycleOwner) { count ->
            if (count > 0) {
                if (badgeDrawable == null) {
                    badgeDrawable = com.google.android.material.badge.BadgeDrawable.create(binding.toolbar.context).apply {
                        backgroundColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.md_theme_light_primary)
                        badgeTextColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.md_theme_light_onPrimary)
                    }
                    com.google.android.material.badge.BadgeUtils.attachBadgeDrawable(badgeDrawable!!, binding.toolbar, R.id.action_favorite)
                }
                badgeDrawable?.number = count
                badgeDrawable?.isVisible = true
            } else {
                badgeDrawable?.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        binding.rvPokemon.adapter = null
        badgeDrawable = null
        super.onDestroyView()
        _binding = null
    }
}
