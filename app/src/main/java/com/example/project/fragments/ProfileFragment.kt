package com.example.project.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.project.adapters.FavoriteAdapter
import com.example.project.vmodels.ProfileViewModel
import com.example.project.databinding.ProfileFragmentBinding
import com.example.project.database.AppDatabase
import com.google.android.material.transition.platform.MaterialSharedAxis

class ProfileFragment : Fragment() {

    private var binding: ProfileFragmentBinding? = null
    private lateinit var viewModel: ProfileViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)


        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = 80L
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = 80L
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = 80L
        }

        fetchUserInfo()

//        val rest = arrayListOf<RestaurantData>(
//            // add items to arraylist
//            RestaurantData(1,"blabla", "blabasdadas", "balasd", "bla", "bdsadsa", 124, "dsadsa"),
//            RestaurantData(2,"blabla", "blabasdadas", "balasd", "bla", "bdsadsa", 124, "dsadsa"),
//            RestaurantData(3,"blabla", "blabasdadas", "balasd", "bla", "bdsadsa", 124, "dsadsa")
//        )
        val viewPager = binding?.viewPager
        val adapter = FavoriteAdapter()
        viewPager?.adapter = adapter
        viewModel.favoritesLive.observe(this.viewLifecycleOwner, { adapter.setData(it) })

        Log.d("view pager:", viewPager?.adapter?.itemCount.toString())


        return binding!!.root
    }




    @SuppressLint("SetTextI18n")
    private fun fetchUserInfo() {
        //fetch Records
        val db = Room.databaseBuilder(
            binding!!.root.context,
            AppDatabase::class.java, "users"
        ).build()
        val thread = Thread{
            viewModel.currentUser = db.userDao().getAll().first()
            db.close()
            val user = viewModel.currentUser!!
            binding!!.contactName.text = user.firstName + " " + user.lastName
            binding!!.contactAddress.text = user.address
            binding!!.contactMail.text = user.emailAddress
            binding!!.contactPhone.text = user.phone
        }
        thread.start()
        db.close()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Profile"

    }

    //TODO: open edit profile dialog

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}