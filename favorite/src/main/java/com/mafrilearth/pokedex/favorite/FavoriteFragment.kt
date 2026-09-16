package com.mafrilearth.pokedex.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mafrilearth.pokedex.R
import com.mafrilearth.pokedex.favorite.databinding.FragmentFavoriteBinding
import com.mafrilearth.pokedex.favorite.di.favoriteModule
import com.mafrilearth.pokedex.ui.home.PokemonAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import androidx.core.net.toUri

class FavoriteFragment : Fragment() {

    private val favoriteViewModel: FavoriteViewModel by viewModel()
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private var pokemonAdapter: PokemonAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(favoriteModule)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (pokemonAdapter == null) {
            pokemonAdapter = PokemonAdapter(isSearch = true)
            
            pokemonAdapter?.onItemClick = { selectedData, view ->
                val uri = "pokedex://detail/${selectedData.id}?transitionName=pokemon_image_${selectedData.id}".toUri()
                val extras = androidx.navigation.fragment.FragmentNavigatorExtras(
                    view to "pokemon_image_${selectedData.id}"
                )
                
                val navOptions = androidx.navigation.NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.stay_dim)
                    .setPopEnterAnim(R.anim.stay_undim)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build()
                
                findNavController().navigate(
                    uri,
                    navOptions,
                    extras
                )
            }
        }
        
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_rounded)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        favoriteViewModel.favoritePokemon.observe(viewLifecycleOwner) { dataPokemon ->
            pokemonAdapter?.setData(dataPokemon)
            binding.tvEmpty.visibility = if (dataPokemon.isNotEmpty()) View.GONE else View.VISIBLE
        }

        binding.rvPokemon.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = pokemonAdapter
        }

        pokemonAdapter?.onDeleteClick = { pokemon ->
            favoriteViewModel.setFavoritePokemon(pokemon, false)
            Snackbar.make(binding.root, getString(R.string.text_removed_from_favorites, pokemon.name), Snackbar.LENGTH_SHORT).apply {
                setAction(getString(R.string.action_undo)) { _ ->
                    favoriteViewModel.setFavoritePokemon(pokemon, true)
                }
            }.show()
        }
    }

    override fun onDestroyView() {
        binding.rvPokemon.adapter = null
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(favoriteModule)
    }
}
