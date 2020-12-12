package com.example.project.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.example.project.adapters.FavoriteAdapter
import com.example.project.database.UserUpdate
import com.example.project.databinding.ProfileFragmentBinding
import com.example.project.models.RestaurantData
import com.example.project.vmodels.ProfileViewModel
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
            Glide.with(binding!!.root.context).load(it.picture).into(binding!!.profileImage)

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
                    binding?.emailTextField?.editText?.text.toString(),
                    binding?.addressTextField?.editText?.text.toString(),
                    viewModel.currentUser.value!!.picture,
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
        binding?.profileImage?.setOnLongClickListener{
            val intent = Intent()
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            true;
        }

    }

    //TODO: ADD PIcture and edit picture
    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // When an Image is picked
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && null != data) {
            val image: String? = data.getStringExtra("IMAGE_KEY")
            if (data.data != null) {
                binding?.profileImage?.let { Glide.with(binding!!.root.context).load(data.data!!).into(it) }
                viewModel.currentUser.value!!.picture = data.data!!.toString();
                viewModel.updateUser(
                    UserUpdate(
                        viewModel.currentUser.value!!.uid,
                        viewModel.currentUser.value!!.firstName,
                        viewModel.currentUser.value!!.lastName,
                        viewModel.currentUser.value!!.emailAddress,
                        viewModel.currentUser.value!!.address,
                        viewModel.currentUser.value!!.picture,
                        viewModel.currentUser.value!!.phone,
                    )
                )
            }
        }
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun showDetails(restaurant: RestaurantData) {
        findNavController().navigate(
            ProfileFragmentDirections.actionProfileFragmentToDetailFragment(restaurant)
        )
    }

}