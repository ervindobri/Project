package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.project.fragments.ProfileFragment
import com.example.project.fragments.RestaurantListFragment
import com.example.project.fragments.SplashFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var restaurantListFragmentFragment : RestaurantListFragment
    lateinit var profileFragment : ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        restaurantListFragmentFragment  = RestaurantListFragment()
        profileFragment  = ProfileFragment()
        title=resources.getString(R.string.list)
        loadFragment(restaurantListFragmentFragment)

        navigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_list-> {
                    loadFragment(restaurantListFragmentFragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_profile-> {
                    loadFragment(profileFragment)
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false

        }
    }
    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}