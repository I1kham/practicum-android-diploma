package ru.practicum.android.diploma.ui.city

import android.content.Context.INPUT_METHOD_SERVICE
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.bundle.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentCitySelectBinding
import ru.practicum.android.diploma.domain.models.entity.Area
import ru.practicum.android.diploma.presentation.card.text.TextCardAdapter
import ru.practicum.android.diploma.presentation.city.CitySelectViewModel
import ru.practicum.android.diploma.ui.city.model.CitySelectState
import ru.practicum.android.diploma.ui.root.RootActivity

class CitySelectFragment : Fragment() {
    private val viewModel: CitySelectViewModel by viewModel()
    private var _binding: FragmentCitySelectBinding? = null
    private val binding get() = _binding!!
    private val cardAdapter = TextCardAdapter()
    private var onItemClick: ((Area) -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCitySelectBinding.inflate(layoutInflater)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navBarVisible(false)
        getAreas()
        initEditText()
        observeVieModel()
        onItemClick = viewModel.chooseArea()
        initRecyclerView()
        prepareBackButton()
    }

    private fun prepareBackButton() {
        binding.preview.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.arrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getAreas() {
        viewModel.getAreas()
    }

    private fun observeVieModel() {
        viewModel.citySelectState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CitySelectState.Content -> {
                    updateRecyclerView(state.cities)
                    binding.noCityLayout.isVisible = false
                    binding.errorLayout.isVisible = false
                    binding.noInternetLay.isVisible = false
                    keyBoardVisibility(false)
                    binding.progressBar.isVisible = false
                }

                CitySelectState.Empty -> {
                    updateRecyclerView(emptyList())
                    binding.noCityLayout.isVisible = true
                    keyBoardVisibility(false)
                    binding.progressBar.isVisible = false
                }

                CitySelectState.Error -> {
                    binding.errorLayout.isVisible = true
                    keyBoardVisibility(false)
                    binding.progressBar.isVisible = false
                }

                CitySelectState.Exit -> {
                    findNavController().popBackStack()
                }

                CitySelectState.NoInternet -> {
                    binding.noInternetLay.isVisible = true
                    keyBoardVisibility(false)
                    binding.progressBar.isVisible = false
                }

                CitySelectState.Loading -> {
                    binding.noCityLayout.isVisible = false
                    binding.errorLayout.isVisible = false
                    binding.noInternetLay.isVisible = false
                    keyBoardVisibility(false)
                    binding.progressBar.isVisible = true
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
            adapter = cardAdapter
        }
    }

    private fun updateRecyclerView(vacancies: List<Area>) {
        binding.recycleView.isVisible = true
        cardAdapter.submitList(vacancies)
        onItemClick?.let {
            cardAdapter.onItemClick = it
        }
    }

    private fun initEditText() {
        binding.etSearchRegionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // функция не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.debounceFilter(s.toString())
                updateSearchIcon(s.isNullOrBlank())
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    binding.recycleView.isVisible = true
                    binding.noCityLayout.isVisible = false
                    binding.errorLayout.isVisible = false
                }
            }
        })

        binding.clearSearchButton.setOnClickListener {
            binding.etSearchRegionEditText.text?.clear()
        }
    }

    private fun updateSearchIcon(isEmpty: Boolean) {
        binding.clearSearchButton.setImageResource(
            if (isEmpty) R.drawable.search_24px else R.drawable.close_24px
        )
    }

    private fun navBarVisible(isVisible: Boolean) {
        (activity as RootActivity).bottomNavigationVisibility(isVisible)
    }

    private fun keyBoardVisibility(visibile: Boolean) {
        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        when (visibile) {
            true -> inputMethodManager?.showSoftInput(binding.etSearchRegionEditText, 0)
            else -> inputMethodManager?.hideSoftInputFromWindow(binding.etSearchRegionEditText.windowToken, 0)
        }
    }
}
