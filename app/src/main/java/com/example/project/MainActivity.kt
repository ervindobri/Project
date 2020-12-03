package com.example.project

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import br.com.mauker.materialsearchview.MaterialSearchView
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment
import com.example.project.fragments.DetailFragment
import com.example.project.fragments.ProfileFragment
import com.example.project.fragments.RestaurantListFragment
import com.example.project.models.RestaurantData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var detailFragment: DetailFragment
    lateinit var profileFragment : ProfileFragment

    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        navController = (supportFragmentManager.findFragmentById(R.id.navigationFragment) as NavHostFragment).navController
        setupActionBarWithNavController(
            navController,
            AppBarConfiguration(setOf(R.id.restaurantListFragment, R.id.profileFragment))
        )

        profileFragment = ProfileFragment()
        detailFragment = DetailFragment()
        title = resources.getString(R.string.list)
        navigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.restaurantListFragment -> {
                    navigationView.visibility = View.VISIBLE
                }

                R.id.profileFragment -> {
                    navigationView.visibility = View.VISIBLE
                }
                else ->{
                    navigationView.visibility = View.GONE
                }
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}