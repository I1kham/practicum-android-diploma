package ru.practicum.android.diploma.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.bundle.Bundle
import androidx.core.bundle.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFavoriteJobBinding
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.presentation.card.vacancy.VacancyAdapter
import ru.practicum.android.diploma.presentation.favorites.FavoriteJobViewModel
import ru.practicum.android.diploma.ui.favorites.models.FavoritesState
import ru.practicum.android.diploma.ui.root.RootActivity.Companion.VACANCY_TRANSFER_KEY

class FavoriteJobFragment : Fragment() {
    private var binding: FragmentFavoriteJobBinding? = null
    private val viewModel: FavoriteJobViewModel by viewModel()
    private var vacancyAdapter: VacancyAdapter? = null
    private var onItemClick: ((Vacancy) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFavoriteJobBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        prepareOnItemClick()

        viewModel.getVacancies()
        viewModel.favoritesState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vacancyAdapter = null
        binding?.favoritesRv?.adapter = null
    }

    private fun init() {
        vacancyAdapter = VacancyAdapter()
        binding?.favoritesRv?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding?.favoritesRv?.adapter = vacancyAdapter
    }

    private fun prepareOnItemClick() {
        onItemClick = { vacancy ->
            val bundle = bundleOf(VACANCY_TRANSFER_KEY to vacancy.id)
            findNavController().navigate(R.id.action_favoriteJobFragment_to_detailsFragment, bundle)
        }

    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> showFavorites(state.favoritesVacancies)
            is FavoritesState.Empty -> showPlaceholder(state.message)
            is FavoritesState.Loading -> showLoading()
            is FavoritesState.Error -> showPlaceholder(state.message)
        }
    }

    private fun showFavorites(favoriteList: List<Vacancy>) {
        binding?.progressBar?.visibility = View.GONE
        binding?.favoritesListPlaceholder?.visibility = View.GONE
        binding?.favoritesRv?.visibility = View.VISIBLE
        vacancyAdapter?.submitList(favoriteList)
        onItemClick?.let {
            vacancyAdapter?.onItemClick = it
        }
    }

    private fun showLoading() {
        binding?.progressBar?.visibility = View.VISIBLE
        binding?.favoritesRv?.visibility = View.GONE
        binding?.favoritesListPlaceholder?.visibility = View.GONE
    }

    private fun showPlaceholder(@StringRes message: Int) {
        binding?.progressBar?.visibility = View.GONE
        binding?.favoritesRv?.visibility = View.GONE

        val drawableRes = when (message) {
            R.string.empty_list -> R.drawable.empty_favorites_list_placeholder
            else -> R.drawable.nothing_found_placeholder
        }

        binding?.favoritesListPlaceholderImage?.setImageResource(drawableRes)
        binding?.favoritesListPlaceholderText?.text = getString(message)
        binding?.favoritesListPlaceholder?.visibility = View.VISIBLE
    }
}
