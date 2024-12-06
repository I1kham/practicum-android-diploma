package ru.practicum.android.diploma.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.bundle.Bundle
import androidx.core.bundle.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchJobBinding
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.domain.models.entity.isNotEmptyCheck
import ru.practicum.android.diploma.presentation.card.vacancy.VacancyAdapter
import ru.practicum.android.diploma.presentation.search.SearchJobViewModel
import ru.practicum.android.diploma.ui.root.RootActivity.Companion.VACANCY_TRANSFER_KEY
import ru.practicum.android.diploma.ui.search.models.VacanciesState
import ru.practicum.android.diploma.util.ResponseStatusCode

class SearchJobFragment : Fragment() {
    private var _binding: FragmentSearchJobBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchJobViewModel by viewModel()
    private val vacancyAdapter = VacancyAdapter()
    private var scrollListener: RecyclerView.OnScrollListener? = null
    private var onItemClick: ((Vacancy) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchJobBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getFilter()
        super.onViewCreated(view, savedInstanceState)
        initEditText()
        initRecyclerView()
        observeViewModel()
        prepareOnItemClick()
        prepareFilterButton()
    }

    private fun prepareFilterButton() {
        binding.filterImageButton.setOnClickListener {
            findNavController().navigate(R.id.action_searchJobFragment_to_filtrationFragment)
        }
    }

    private fun prepareOnItemClick() {
        onItemClick = { vacancy ->
            val bundle = bundleOf(VACANCY_TRANSFER_KEY to vacancy.id)
            findNavController().navigate(R.id.action_searchJobFragment_to_detailsFragment, bundle)
        }
    }

    private fun initEditText() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // функция не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSearchIcon(s.isNullOrEmpty())
                    viewModel.searchDebounced(s.toString().trim())
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.isEmpty() == true) {
                    viewModel.clearVacancies()
                }
            }
        })

        binding.clearSearchButton.setOnClickListener {
            binding.searchEditText.text?.clear()
            binding.searchEditText.clearFocus()
            binding.progressBar.isVisible = false
            binding.bottomProgressBar.isVisible = false
            binding.vacanciesRecyclerView.scrollToPosition(0)
            viewModel.clearVacancies()
        }
    }

    private fun initRecyclerView() {
        binding.vacanciesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vacancyAdapter
            scrollListener?.let { removeOnScrollListener(it) }
            scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val pos =
                            (binding.vacanciesRecyclerView.layoutManager as LinearLayoutManager)
                                .findLastVisibleItemPosition()
                        val itemsCount = vacancyAdapter.itemCount
                        if (pos >= itemsCount - 1) {
                            viewModel.loadNextPage()
                        }
                    }
                }
            }.also {
                addOnScrollListener(it)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.vacanciesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                VacanciesState.Loading -> {
                    binding.searchLayout.isVisible = false
                    val itemsCount = binding.vacanciesRecyclerView.childCount
                    if (itemsCount > 0) {
                        binding.bottomProgressBar.isVisible = true
                    } else {
                        binding.vacanciesRecyclerView.visibility = View.GONE
                        vacancyAdapter.run {
                            submitList(null)
                            notifyDataSetChanged()
                        }
                        showTopProgressBar()
                        binding.messageChip.isVisible = false
                    }
                    keyBoardVisibility(false)
                }

                is VacanciesState.Error -> {
                    hideProgressBar()
                    showError(state.responseState)
                    keyBoardVisibility(false)
                }

                is VacanciesState.Success -> {
                    hideProgressBar()
                    updateRecyclerView(state.vacancies)
                    keyBoardVisibility(false)
                    binding.messageChip.isVisible = true
                    state.totalCount?.let { count ->
                        binding.messageChip.text =
                            requireContext().resources.getQuantityString(
                                R.plurals.plurals_vacancies,
                                count,
                                count
                            )
                    }
                }

                VacanciesState.Empty -> {
                    hideProgressBar()
                    showEmptyState()
                    keyBoardVisibility(false)
                    binding.messageChip.isVisible = true
                    binding.messageChip.text = context?.getString(R.string.no_such_vacancies)
                }

                VacanciesState.Start -> {
                    clearRecyclerView()
                    binding.messageChip.isVisible = false
                }
            }
        }

        viewModel.savedFilter.observe(viewLifecycleOwner) { filter ->
            binding.filterImageButton.setImageResource(
                if (filter.isNotEmptyCheck()) R.drawable.filter_on__24px else R.drawable.filter_off__24px
            )
        }
    }

    private fun clearRecyclerView() {
        binding.vacanciesRecyclerView.isVisible = false
        showHiddenState()
    }

    private fun updateSearchIcon(isEmpty: Boolean) {
        binding.clearSearchButton.setImageResource(
            if (isEmpty) R.drawable.search_24px else R.drawable.close
        )
        binding.searchLayout.isVisible = isEmpty
    }

    private fun showHiddenState() {
        binding.errorLayout.visibility = View.GONE
        binding.noJobsLayout.visibility = View.GONE
        binding.vacanciesRecyclerView.isVisible = false
        binding.searchLayout.visibility = View.VISIBLE
    }

    private fun showTopProgressBar() {
        binding.searchLayout.isVisible = false
        binding.errorLayout.visibility = View.GONE
        binding.noJobsLayout.visibility = View.GONE
        binding.vacanciesRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.bottomProgressBar.isVisible = false

    }

    private fun showError(responseState: ResponseStatusCode?) {
        when (responseState) {
            ResponseStatusCode.Error -> {
                val recycleVieVisible = binding.vacanciesRecyclerView.isVisible
                if (recycleVieVisible) {
                    showResponseErrToast()
                } else {
                    binding.messageChip.isVisible = false
                    binding.searchLayout.visibility = View.GONE
                    binding.noJobsLayout.visibility = View.GONE
                    binding.vacanciesRecyclerView.visibility = View.GONE
                    binding.errorTv.setText(R.string.server_error)
                    binding.errorImage.setImageResource(R.drawable.server_error_on_search_screen)
                    binding.errorLayout.visibility = View.VISIBLE
                }
            }

            ResponseStatusCode.NoInternet -> {
                val recycleVieVisible = binding.vacanciesRecyclerView.isVisible
                if (recycleVieVisible) {
                    showNoInternetToast()
                } else {
                    binding.messageChip.isVisible = false
                    binding.searchLayout.visibility = View.GONE
                    binding.noJobsLayout.visibility = View.GONE
                    binding.vacanciesRecyclerView.visibility = View.GONE
                    binding.errorTv.setText(R.string.no_internet)
                    binding.errorImage.setImageResource(R.drawable.no_internet_placeholder)
                    binding.errorLayout.visibility = View.VISIBLE
                }
            }

            else -> {}
        }
    }

    private fun showEmptyState() {
        binding.vacanciesRecyclerView.visibility = View.GONE
        binding.searchLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
        binding.noJobsLayout.visibility = View.VISIBLE

    }

    private fun updateRecyclerView(vacancies: List<Vacancy>) {
        binding.vacanciesRecyclerView.visibility = View.VISIBLE
        binding.noJobsLayout.visibility = View.GONE
        binding.searchLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
        vacancyAdapter.submitList(vacancies)
        onItemClick?.let {
            vacancyAdapter.onItemClick = it
        }
    }

    private fun showNoInternetToast() {
        Toast.makeText(context, getString(R.string.no_internet_toast), Toast.LENGTH_LONG)
            .show()
        binding.bottomProgressBar.isVisible = false
    }

    private fun showResponseErrToast() {
        Toast.makeText(context, getString(R.string.response_error_toast), Toast.LENGTH_LONG)
            .show()
        binding.bottomProgressBar.isVisible = false
    }

    private fun keyBoardVisibility(visibile: Boolean) {
        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        when (visibile) {
            true -> inputMethodManager?.showSoftInput(binding.searchEditText, 0)
            else -> inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrollListener = null
    }
}
