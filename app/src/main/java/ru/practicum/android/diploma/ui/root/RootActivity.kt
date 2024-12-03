package ru.practicum.android.diploma.ui.root

import androidx.appcompat.app.AppCompatActivity
import androidx.core.bundle.Bundle
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {
    private var binding: ActivityRootBinding? = null
    private var navHostFragment: NavHostFragment? = null
    private var navController: NavController? = null
    private var bottomNavigationView: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareBinding()
        prepareNavHostFragment()
        prepareNavHostController()
        prepareBottomNavView()
    }

    private fun prepareBinding() {
        binding = ActivityRootBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }
    }

    private fun prepareNavHostFragment() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
    }

    private fun prepareNavHostController() {
        navHostFragment?.let {
            navController = it.navController
        }
    }

    private fun prepareBottomNavView() {
        binding?.let { bindingRoot ->
            bottomNavigationView = bindingRoot.bottomNavigation
            navController?.let { navController ->
                bottomNavigationView?.setupWithNavController(navController)
                navController.navInflater
            }
        }
    }

    fun bottomNavigationVisibility(isVisibile: Boolean) {
        binding?.bottomNavigation?.isVisible = isVisibile
    }

    companion object {
        /*ключ для передачи id вакансии между фрагментами через safeArgs*/
        const val VACANCY_TRANSFER_KEY = "vacancy_id"

        /* не желаемый регион */
        const val NOT_DESIRED_AREA_KEY = "1001"
    }
}
