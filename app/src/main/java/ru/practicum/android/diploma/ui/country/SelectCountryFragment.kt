package ru.practicum.android.diploma.ui.country

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentSelectCountryBinding
import ru.practicum.android.diploma.domain.models.entity.Country
import ru.practicum.android.diploma.presentation.card.country.CountryAdapter
import ru.practicum.android.diploma.presentation.country.SelectCountryViewModel
import ru.practicum.android.diploma.ui.country.model.CountrySelectState
import ru.practicum.android.diploma.ui.root.RootActivity

class SelectCountryFragment : Fragment() {
    private val viewModel: SelectCountryViewModel by viewModel()
    private var _binding: FragmentSelectCountryBinding? = null
    private val binding get() = _binding!!
    private val countryAdapter = CountryAdapter()
    private var onItemClick: ((Country) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: android.os.Bundle?,
    ): View {
        _binding = FragmentSelectCountryBinding.inflate(layoutInflater)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navBarVisible(false)
        observeViewModel()
        onItemClick = viewModel.chooseCountry()
        initRecyclerView()
        binding.preview.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.countrySelectState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CountrySelectState.Success -> {
                    updateRecyclerView(state.countries)
                    binding.errorLayout.isVisible = false
                }

                CountrySelectState.Empty -> {
                    updateRecyclerView(emptyList())
                    binding.errorLayout.isVisible = true
                }

                is CountrySelectState.Error -> {
                    binding.errorLayout.isVisible = true
                }

                CountrySelectState.Exit -> {
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        navBarVisible(false)
        _binding = null
    }

    private fun initRecyclerView() {
        binding.recycleView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = countryAdapter
        }
    }

    private fun updateRecyclerView(countries: List<Country>) {
        binding.recycleView.isVisible = true
        countryAdapter.submitList(countries)
        onItemClick?.let { countryAdapter.onItemClick = it }
    }

    private fun navBarVisible(isVisible: Boolean) {
        (activity as RootActivity).bottomNavigationVisibility(isVisible)
    }
}

