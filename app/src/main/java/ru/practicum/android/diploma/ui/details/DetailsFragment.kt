package ru.practicum.android.diploma.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.bundle.Bundle
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentDetailsBinding
import ru.practicum.android.diploma.domain.models.entity.Experience
import ru.practicum.android.diploma.domain.models.entity.KeySkill
import ru.practicum.android.diploma.domain.models.entity.Salary
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.presentation.details.DetailsFragmentViewModel
import ru.practicum.android.diploma.ui.details.models.DetailsFragmentState
import ru.practicum.android.diploma.ui.root.RootActivity
import ru.practicum.android.diploma.ui.root.RootActivity.Companion.VACANCY_TRANSFER_KEY
import ru.practicum.android.diploma.util.ResponseStatusCode
import ru.practicum.android.diploma.util.fillBy
import ru.practicum.android.diploma.util.format

class DetailsFragment : Fragment() {
    private val viewModel: DetailsFragmentViewModel by viewModel()
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailsBinding.inflate(layoutInflater)
        return this.binding.root
    }

    override fun onDetach() {
        super.onDetach()
        navBarVisible(true)
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navBarVisible(false)
        prepareViewModel()
        initViewModel()
        prepareBackButton()
        prepareLikeButton()
        prepareShareButton()

        viewModel.isFavoriteLiveData.observe(viewLifecycleOwner) { isFavorite ->
            this.binding.toolbarLikeButton.isSelected = isFavorite
        }
    }

    private fun prepareShareButton() {
        this.binding.toolbarShareButton.setOnClickListener {
            viewModel.shareVacancy()
        }
    }

    private fun prepareLikeButton() {
        this.binding.toolbarLikeButton.setOnClickListener {
            viewModel.likeButton()
        }
    }

    private fun initViewModel() {
        viewModel.getVacancy(arguments?.getString(VACANCY_TRANSFER_KEY))
        progressBarVisible(true)
    }

    private fun prepareBackButton() {
        this.binding.preview.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun render(state: DetailsFragmentState) {
        when (state) {
            is DetailsFragmentState.Content -> {
                showContent(state.vacancy)
                this.binding.errorServer.isVisible = false
                this.binding.errorLayoutEmpty.isVisible = false
            }

            is DetailsFragmentState.Error -> {
                progressBarVisible(false)
                renderError(state.errState)
            }

            DetailsFragmentState.Empty -> {
                this.binding.errorLayoutEmpty.isVisible = true
                this.binding.content.isVisible = false
            }
        }
    }

    private fun renderError(errState: ResponseStatusCode?) {
        when (errState) {
            ResponseStatusCode.Error -> {
                this.binding.errorServer.isVisible = true
                this.binding.content.isVisible = false
            }

            ResponseStatusCode.NoInternet -> {
                this.binding.connectionErrorLayout.isVisible = true
                this.binding.content.isVisible = false
            }

            ResponseStatusCode.Ok -> {
                this.binding.errorLayoutEmpty.isVisible = true
                this.binding.content.isVisible = false
            }

            else -> {
                this.binding.errorServer.isVisible = true
                this.binding.content.isVisible = false
            }
        }
    }

    private fun showContent(vacancy: Vacancy) {
        this.binding.content.isVisible = true
        fillSalary(vacancy.salary)
        fillDescription(vacancy.description)
        fillTitle(vacancy.name)
        fillExperience(vacancy.experience)
        fillEmployment(vacancy)
        fillEmployer(vacancy)
        fillKeySkills(vacancy.keySkills)
        progressBarVisible(false)
    }

    private fun fillKeySkills(keySkills: List<KeySkill>?) {
        keySkills?.let {
            this.binding.keySkillsTitle.isVisible = keySkills.isNotEmpty()
            var str = ""
            keySkills.map { str += getString(R.string.key_skill_mask, it.name) }
            this.binding.keySkillsText.text = HtmlCompat.fromHtml(
                str,
                HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
            )
        }
    }

    private fun fillEmployer(vacancy: Vacancy) {
        this.binding.cardTitleText.text = vacancy.employer?.name
        this.binding.cardCityText.text = vacancy.adress?.full ?: vacancy.area?.name

//        этот блок для отображения заглушки без интернета
        context?.let { context ->
            this.binding.cardImage.fillBy(vacancy.employer?.logoUrls?.original, context)
        }
    }

    private fun fillEmployment(vacancy: Vacancy?) {
        val str = "${vacancy?.employment?.name}. ${vacancy?.schedule?.name}"
        this.binding.workShiftText.text = str
    }

    private fun fillExperience(experience: Experience?) {
        this.binding.experienceTitleText.text = experience?.name
    }

    private fun fillTitle(tittle: String?) {
        this.binding.titleName.text = tittle
    }

    private fun fillDescription(description: String?) {
        description?.let {
            this.binding.descriptionHtmlText.text = HtmlCompat.fromHtml(
                it,
                HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
            )
        }
    }

    private fun fillSalary(salary: Salary?) {
        this.binding.titleSalary.text = salary.format()
    }

    private fun prepareViewModel() {
        viewModel.observeState().observe(getViewLifecycleOwner()) {
            render(it)
        }
    }

    private fun navBarVisible(isVisible: Boolean) {
        (activity as RootActivity).bottomNavigationVisibility(isVisible)
    }

    private fun progressBarVisible(isVisible: Boolean) {
        this@DetailsFragment.binding.progressBar.isVisible = isVisible
        this@DetailsFragment.binding.content.isVisible = !isVisible
    }

}
