package ru.practicum.android.diploma.ui.region

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.bundle.Bundle
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.presentation.region.SelectRegionViewModel

class SelectRegionFragment : Fragment() {

    companion object {
        fun newInstance() = SelectRegionFragment()
    }

    private val viewModel: SelectRegionViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_select_region, container, false)
    }
}
