package com.example.project.activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.project.R
import com.example.project.database.User
import com.example.project.database.UserDatabase
import com.example.project.fragments.DetailFragment
import com.example.project.fragments.ProfileFragment
import com.example.project.helpers.BottomNavigationViewHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        removeTextLabel(navigationView, R.id.restaurantListFragment)
        removeTextLabel(navigationView, R.id.profileFragment)
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

        initPersistency()
    }

    private fun initPersistency() {
        val db = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java,
            "users"
        ).build()
        //Insert Case
        val thread = Thread {
            if ( db.userDao().getAll().size == 0){
                val user = User(
                    1,
                    "Ervin",
                    "Dobri",
                    "dobriervin@yahoo.com",
                    "Strada Armoniei, nr.14",
                    null,
                    "+40754365846"
                )
                db.userDao().insertAll(user)
            }

        }
        thread.start()
    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    private fun removeTextLabel(bottomNavigationView: BottomNavigationView, @IdRes menuItemId: Int) {
        val view = bottomNavigationView.findViewById<View>(menuItemId) ?: return
        if (view is ItemView) {
            val viewGroup = view as ViewGroup
            var padding = 0
            for (i in 0 until viewGroup.childCount) {
                val v = viewGroup.getChildAt(i)
                if (v is ViewGroup) {
                    padding = v.getHeight()
                    viewGroup.removeViewAt(i)
                }
            }
            viewGroup.setPadding(
                view.getPaddingLeft(),
                (viewGroup.paddingTop + padding) / 2,
                view.getPaddingRight(),
                view.getPaddingBottom()
            )
        }
    }
}