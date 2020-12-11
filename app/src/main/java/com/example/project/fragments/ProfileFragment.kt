package com.example.project.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.project.adapters.FavoriteAdapter
import com.example.project.vmodels.ProfileViewModel
import com.example.project.databinding.ProfileFragmentBinding
import com.example.project.database.UserDatabase
import com.example.project.database.UserUpdate
import com.example.project.models.RestaurantData
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialSharedAxis

class ProfileFragment : Fragment(), FavoriteAdapter.SelectedRestaurant {

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

        viewModel.currentUser.observe(viewLifecycleOwner, {
            binding?.contactName?.text = (it.firstName) + " " + it.lastName
            binding?.contactAddress?.text = (it.address)
            binding?.contactMail?.text = (it.emailAddress)
            binding?.contactPhone?.text = (it.phone)
        })

        val viewPager = binding?.viewPager
        val adapter = FavoriteAdapter(this)
        viewPager?.adapter = adapter
        viewModel.favoritesLive.observe(this.viewLifecycleOwner, { adapter.setData(it) })

        Log.d("view pager:", viewPager?.adapter?.itemCount.toString())
        return binding!!.root
    }





//    @SuppressLint("SetTextI18n")
//    private fun fetchUserInfo() {
//        //fetch Records
//        val db = Room.databaseBuilder(
//            binding!!.root.context,
//            UserDatabase::class.java, "users"
//        ).build()
//        val thread = Thread{
//            if ( db.userDao().getAll().size > 0){
//                viewModel.currentUser = db.userDao().getAll().first()
//                db.close()
//                val user = viewModel.currentUser!!
//                binding!!.contactName.text = user.firstName + " " + user.lastName
//                binding!!.contactAddress.text = user.address
//                binding!!.contactMail.text = user.emailAddress
//                binding!!.contactPhone.text = user.phone
//            }
//        }
//        thread.start()
//        db.close()
//    }

    private fun buildContainerTransformation() =
        MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            duration = 300
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Profile"

        binding?.editButton?.setOnClickListener{
            //bind data to layout elements
            viewModel.currentUser.observe(viewLifecycleOwner, {
                binding?.firstNameTextField?.editText?.setText(it.firstName)
                binding?.lastNameTextField?.editText?.setText(it.lastName)
                binding?.addressTextField?.editText?.setText(it.address)
                binding?.phoneTextfield?.editText?.setText(it.phone)
                binding?.emailTextField?.editText?.setText(it.emailAddress)
            })

            //todo: select image from gallery

            //openlayout
            val transition = buildContainerTransformation()
            transition.startView =  binding!!.editButton
            transition.endView =  binding!!.editLayout
            transition.addTarget( binding!!.editLayout)

            TransitionManager.beginDelayedTransition( binding!!.root, transition)
            binding!!.editLayout.visibility = View.VISIBLE
        }
        binding?.closeEditProfile?.setOnClickListener{
            //close container
            val transition = buildContainerTransformation()
            transition.startView =  binding!!.editLayout
            transition.endView =  binding!!.editButton
            transition.addTarget( binding!!.editButton)

            TransitionManager.beginDelayedTransition( binding!!.root, transition)
            binding!!.editLayout.visibility = View.GONE
        }
        binding?.editProfile?.setOnClickListener{
            //save profile info
            viewModel.updateUser(
                UserUpdate(
                    viewModel.currentUser.value?.uid ?: 1,
                    binding?.firstNameTextField?.editText?.text.toString(),
                    binding?.lastNameTextField?.editText?.text.toString(),
                    binding?.addressTextField?.editText?.text.toString(),
                    binding?.emailTextField?.editText?.text.toString(),
                    null,
                    binding?.phoneTextfield?.editText?.text.toString(),
                )
            )
            //close container
            val transition = buildContainerTransformation()
            transition.startView =  binding!!.editLayout
            transition.endView =  binding!!.editButton
            transition.addTarget( binding!!.editButton)

            TransitionManager.beginDelayedTransition( binding!!.root, transition)
            binding!!.editLayout.visibility = View.GONE
        }

    }

    //TODO: ADD PIcture and edit picture

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun showDetails(restaurant: RestaurantData) {
        findNavController().navigate(
            ProfileFragmentDirections.actionProfileFragmentToDetailFragment(restaurant)
        )
    }

    override fun onDestroy() {
        viewModel.closeDB()
        super.onDestroy()
    }

}