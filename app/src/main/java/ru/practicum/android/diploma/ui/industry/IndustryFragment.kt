package ru.practicum.android.diploma.ui.industry

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RadioGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding
import ru.practicum.android.diploma.domain.models.entity.IndustryNested
import ru.practicum.android.diploma.presentation.industry.IndustryViewModel
import ru.practicum.android.diploma.ui.custom.CustomRadioLayout
import ru.practicum.android.diploma.ui.industry.models.IndustryFragmentState

class IndustryFragment : Fragment() {
    private val viewModel: IndustryViewModel by viewModel()
    private var _binding: FragmentIndustryBinding? = null
    private val binding get() = _binding!!
    private var editTExtListener: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        setupSearchFilter()
        prepareSaveButton()
    }

    private fun prepareSaveButton() {
        binding.selectButton.setOnClickListener {
            viewModel.saveFilter()
        }
    }

    private fun setupUI() {
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clearSearchButton.setOnClickListener {
            binding.filterEditText.text.clear()
        }
    }

    private fun setupObservers() {
        viewModel.industries.observe(viewLifecycleOwner) { state ->
            when (state) {
                is IndustryFragmentState.Content -> {
                    showContent(state.listIndastries, state.checkedIndustry)
                }

                IndustryFragmentState.Exit -> {
                    findNavController().popBackStack()
                }

                IndustryFragmentState.Empty -> showEmptyPlaceholder()
                IndustryFragmentState.Error -> showErrorPlaceholder()
                IndustryFragmentState.Loading -> showLoading()
                IndustryFragmentState.NoInternet -> showNoInternet()

            }
        }
    }

    private fun showNoInternet() {
        binding.emptyLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
        binding.loadingLayout.visibility = View.GONE
        binding.contentLayout.visibility = View.GONE
        binding.noInternetLay.visibility = View.VISIBLE
        keyBoardVisibility(false)
        binding.filterEditText.removeTextChangedListener(editTExtListener)
    }

    private fun showEmptyPlaceholder() {
        binding.emptyLayout.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
        binding.loadingLayout.visibility = View.GONE
        binding.contentLayout.visibility = View.GONE
        binding.noInternetLay.visibility = View.GONE
        keyBoardVisibility(false)
    }

    private fun showErrorPlaceholder() {
        binding.errorLayout.visibility = View.VISIBLE
        binding.emptyLayout.visibility = View.GONE
        binding.loadingLayout.visibility = View.GONE
        binding.contentLayout.visibility = View.GONE
        binding.noInternetLay.visibility = View.GONE
        keyBoardVisibility(false)
    }

    private fun showLoading() {
        binding.loadingLayout.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
        binding.emptyLayout.visibility = View.GONE
        binding.contentLayout.visibility = View.GONE
        binding.noInternetLay.visibility = View.GONE
        keyBoardVisibility(false)
    }

    private fun showContent(industries: List<IndustryNested>, checkedIndustry: IndustryNested?) {
        binding.contentLayout.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
        binding.emptyLayout.visibility = View.GONE
        binding.loadingLayout.visibility = View.GONE
        binding.noInternetLay.visibility = View.GONE
        updateRadioGroup(
            industries,
            checkedIndustry
        )
        keyBoardVisibility(false)
    }

    private fun setupSearchFilter() {
        editTExtListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не нужно
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterDebounce(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
                updateSearchIcon(s.isNullOrBlank())
            }
        }
        binding.filterEditText.addTextChangedListener(editTExtListener)
        binding.clearSearchButton.setOnClickListener {
            binding.filterEditText.text?.clear()
        }
    }

    private fun keyBoardVisibility(visibile: Boolean) {
        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        when (visibile) {
            true -> inputMethodManager?.showSoftInput(binding.filterEditText, 0)
            else -> inputMethodManager?.hideSoftInputFromWindow(binding.filterEditText.windowToken, 0)
        }
    }

    private fun updateSearchIcon(isEmpty: Boolean) {
        binding.clearSearchButton.setImageResource(
            if (isEmpty) R.drawable.search_24px else R.drawable.close
        )
    }

    private fun updateRadioGroup(industries: List<IndustryNested>, checkedIndustry: IndustryNested?) {
        binding.radioGroup.removeAllViews()
        Log.d("updateRadioGroup", checkedIndustry.toString())
        // Список для хранения всех CustomRadioLayout
        val radioLayouts = mutableListOf<CustomRadioLayout>()
        industries.forEach { industry ->
            val customRadioLayout = CustomRadioLayout(requireContext()).apply {
                bind(industry)
                layoutParams = RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
                )
                // Обработчик изменения состояния для каждого CustomRadioLayout
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        // Отключаем все остальные RadioButton
                        radioLayouts.forEach { layout ->
                            if (layout != this) {
                                layout.setChecked(false)
                            }
                        }
                    }
                    binding.selectButton.isVisible = isChecked
                    viewModel.setSelectedIndustry(industry, isChecked)
                }
                this.setChecked(industry.id == checkedIndustry?.id)
            }
            radioLayouts.add(customRadioLayout)
            binding.radioGroup.addView(customRadioLayout)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
