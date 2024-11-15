package ru.practicum.android.diploma.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchJobBinding
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.presentation.search.SearchJobViewModel
import ru.practicum.android.diploma.ui.search.adapters.VacancyAdapter
import ru.practicum.android.diploma.ui.search.models.VacanciesState

class SearchJobFragment : Fragment() {

    private var binding: FragmentSearchJobBinding? = null
    private val viewModel: SearchJobViewModel by viewModel()
    private val vacancyAdapter = VacancyAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchJobBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditText()
        initRecyclerView()
        observeViewModel()
    }

    private fun initEditText() {
        binding?.searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // функция не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSearchIcon(s.isNullOrEmpty())

                if (!s.isNullOrBlank()) {
                    updateRecyclerView(emptyList())
                    viewModel.searchDebounce(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {

                if (s?.isEmpty() == true) {
                    viewModel.clearVacancies()
                }
            }
        })

        binding?.clearSearchButton?.setOnClickListener {
            binding?.searchEditText?.text?.clear()
            viewModel.clearVacancies()
        }
    }

    private fun initRecyclerView() {
        binding?.vacanciesRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vacancyAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.vacanciesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is VacanciesState.Loading -> showLoading()
                is VacanciesState.Error -> {
                    hideLoading()
                    showError(state.message)
                }

                is VacanciesState.Success -> {
                    hideLoading()
                    updateRecyclerView(state.vacancies)
                }

                is VacanciesState.Empty -> {
                    hideLoading()
                    showEmptyState()
                }

                is VacanciesState.Hidden -> clearRecyclerView()
            }
        }
    }

    private fun clearRecyclerView() {
        updateRecyclerView(emptyList())
        showHiddenState()
    }

    private fun updateSearchIcon(isEmpty: Boolean) {
        binding?.clearSearchButton?.setImageResource(
            if (isEmpty) R.drawable.search_24px else R.drawable.close_24px
        )
    }

    private fun showHiddenState() {
        binding?.searchLayout?.visibility = View.VISIBLE
        binding?.noInternetLayout?.visibility = View.GONE
        binding?.noJobsLayout?.visibility = View.GONE
    }

    private fun showLoading() {
        binding?.progressBar?.visibility = View.VISIBLE
        binding?.searchLayout?.visibility = View.GONE
        binding?.noInternetLayout?.visibility = View.GONE
        binding?.noJobsLayout?.visibility = View.GONE
        binding?.vacanciesRecyclerView?.visibility = View.GONE
    }

    private fun hideLoading() {
        binding?.progressBar?.visibility = View.GONE
    }

    private fun showError(errorMessage: Int) {
        binding?.vacanciesRecyclerView?.visibility = View.GONE
        binding?.searchLayout?.visibility = View.GONE
        binding?.noJobsLayout?.visibility = View.GONE
        binding?.noInternetLayout?.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding?.vacanciesRecyclerView?.visibility = View.GONE
        binding?.searchLayout?.visibility = View.GONE
        binding?.noInternetLayout?.visibility = View.GONE
        binding?.noJobsLayout?.visibility = View.VISIBLE

    }

    private fun updateRecyclerView(vacancies: List<Vacancy>) {
        binding?.vacanciesRecyclerView?.visibility = View.VISIBLE
        binding?.noJobsLayout?.visibility = View.GONE
        binding?.searchLayout?.visibility = View.GONE
        binding?.noInternetLayout?.visibility = View.GONE
        vacancyAdapter.submitList(vacancies)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
