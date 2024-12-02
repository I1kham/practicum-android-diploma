package ru.practicum.android.diploma.ui.filtration

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.bundle.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFiltrationBinding
import ru.practicum.android.diploma.presentation.filtration.FiltrationViewModel
import ru.practicum.android.diploma.ui.root.RootActivity

class FiltrationFragment : Fragment() {
    private val viewModel: FiltrationViewModel by viewModel()
    private var _binding: FragmentFiltrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFiltrationBinding.inflate(layoutInflater)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navBarVisible(false)
        // Загрузка сохраненного фильтра
        setupObservers()
        setupListeners()
        prepareButtons()
        viewModel.loadSavedFilter()
    }

    private fun setupObservers() {
        viewModel.filterState.observe(viewLifecycleOwner) { filter ->
            // Заполнение полей сохраненными значениями
            val regionName = filter?.regionName?.let { regionName ->
                ", $regionName"
            }.orEmpty()
            val countryName = filter?.countryName.orEmpty()
            binding.workPlace.text = "$countryName$regionName"

            binding.industry.text = filter?.industryName.orEmpty()
            if (binding.etInputSalary.text.toString() != filter?.salary.orEmpty()) {
                binding.etInputSalary.setText(filter?.salary.orEmpty())
            }
            binding.checkBox.isChecked = filter?.onlySalaryFlag ?: false

            val isAnyFieldFilled: Boolean =
                filter?.countryName?.isNotEmpty() == true ||
                    filter?.regionName?.isNotEmpty() == true ||
                    filter?.industryName?.isNotEmpty() == true ||
                    filter?.salary?.isNotEmpty() == true ||
                    filter?.onlySalaryFlag == true

            setApplyResetButtonsVis(isAnyFieldFilled)
            changeArrowBtnToCrossBtn()
        }
    }

    private fun setApplyResetButtonsVis(isAnyFieldFilled: Boolean) {
        binding.submitButton.isVisible = isAnyFieldFilled
        binding.resetButton.isVisible = isAnyFieldFilled
    }

    private fun setupListeners() {
        // Слушатель изменения зарплаты
        binding.etInputSalary.doAfterTextChanged { text ->
            viewModel.salaryDebounce(text.toString())
            binding.clearSalaryButton.isVisible = text?.isBlank() != true
            binding.hintTitle.isActivated = text?.isBlank() != true
        }

        // Слушатель чекбокса
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkingOnlySalaryFlag(isChecked)
        }
        // Кнопка очистить
        binding.clearSalaryButton.setOnClickListener {
            binding.etInputSalary.setText(null.orEmpty())
        }

        // Кнопка назад
        binding.headerLayout.setOnClickListener {
            findNavController().popBackStack()
        }

        // Картинка нопки назад
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // Кнопка применить
        binding.submitButton.setOnClickListener {
            viewModel.saveFilter()
            findNavController().popBackStack()
        }

        // Кнопка сбросить
        binding.resetButton.setOnClickListener {
            viewModel.resetFilter()
        }
    }

    private fun prepareButtons() {
        // Кнопки выбора региона и отрасли
        binding.workPlaceLayout.setOnClickListener {
            findNavController().navigate(R.id.action_filtrationFragment_to_selectRegionFragment)
        }

        binding.industryLayout.setOnClickListener {
            findNavController().navigate(R.id.action_filtrationFragment_to_industryFragment)
        }
    }

    private fun navBarVisible(isVisible: Boolean) {
        (activity as RootActivity).bottomNavigationVisibility(isVisible)
    }

    private fun changeArrowBtnToCrossBtn() {
        if (binding.workPlace.text.isNotEmpty()) {
            binding.workPlaceTitle.isVisible = true
            binding.workPlaceBtn.setImageResource(R.drawable.close_24px)
            binding.workPlaceBtn.setOnClickListener {
                viewModel.resetWorkPlace()
            }
        } else {
            binding.workPlaceTitle.isVisible = false
            binding.workPlaceBtn.setImageResource(R.drawable.arrow_forward_24px)
            binding.workPlaceBtn.setOnClickListener {
                findNavController().navigate(R.id.action_filtrationFragment_to_selectRegionFragment)
            }
        }
        if (binding.industry.text.isNotEmpty()) {
            binding.industryTitle.isVisible = true
            binding.industryBtn.setImageResource(R.drawable.close_24px)
            binding.industryBtn.setOnClickListener {
                viewModel.resetIndustry()
            }
        } else {
            binding.industryTitle.isVisible = false
            binding.industryBtn.setImageResource(R.drawable.arrow_forward_24px)
            binding.industryBtn.setOnClickListener {
                findNavController().navigate(R.id.action_filtrationFragment_to_industryFragment)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        navBarVisible(true)
        _binding = null
    }
}
