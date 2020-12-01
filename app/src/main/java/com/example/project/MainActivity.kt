package com.example.project

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.mauker.materialsearchview.MaterialSearchView
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment
import com.example.project.fragments.ProfileFragment
import com.example.project.fragments.RestaurantListFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AAH_FabulousFragment.Callbacks {
    lateinit var restaurantListFragmentFragment : RestaurantListFragment
    lateinit var profileFragment : ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        restaurantListFragmentFragment = RestaurantListFragment()
        profileFragment = ProfileFragment()
        title = resources.getString(R.string.list)
        loadFragment(restaurantListFragmentFragment)

        navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_list -> {
                    loadFragment(restaurantListFragmentFragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_profile -> {
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

    override fun onResult(result: Any?) {
        Log.d("tellmehow", "onResult: " + result.toString());
        if (result.toString().equals("swiped_down", true)) {
            //do something or nothing
            Log.d("tellmehow", "equals: " + result.toString());

        } else {
            //handle result
            Log.d("tellmehow", "nope: " + result.toString());


        }
    }

}