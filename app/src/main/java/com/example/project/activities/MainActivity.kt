package com.example.project.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.project.R
import com.example.project.database.AppDatabase
import com.example.project.database.User
import com.example.project.fragments.DetailFragment
import com.example.project.fragments.ProfileFragment
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

        initPersistency()
    }

    private fun initPersistency() {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "users"
        ).build()
        //Insert Case
        val thread = Thread {
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
//        thread.start()
    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}