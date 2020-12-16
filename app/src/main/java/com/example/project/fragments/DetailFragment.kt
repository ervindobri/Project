package com.example.project.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.request.RequestOptions
import com.example.project.adapters.RestaurantAdapter
import com.example.project.databinding.DetailFragmentBinding
import com.example.project.models.RestaurantData
import com.example.project.models.RestaurantUpdate
import com.example.project.vmodels.RestaurantListViewModel
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.animations.DescriptionAnimation
import com.glide.slider.library.slidertypes.TextSliderView
import com.google.android.material.transition.platform.MaterialFade
import com.google.android.material.transition.platform.MaterialFadeThrough
import java.io.File
import java.util.*


class DetailFragment : Fragment(){

    private lateinit var viewModel: RestaurantListViewModel
    private lateinit var imageSlider: SliderLayout
    private lateinit var binding: DetailFragmentBinding
    private val args: DetailFragmentArgs by navArgs()

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(RestaurantListViewModel::class.java)

        enterTransition = MaterialFadeThrough()
        reenterTransition = MaterialFade()
        exitTransition = MaterialFadeThrough()

        binding =  DetailFragmentBinding.inflate(inflater, container, false)
        binding.nameView.text = args.restaurant.name
        binding.addressView.text = args.restaurant.address
        binding.cityView.text = args.restaurant.city + ", " + args.restaurant.country
        binding.priceRangeView.text = args.restaurant.priceRange()
        binding.buttonFavorite.isChecked = args.restaurant.favorite

        viewModel.findRestaurant(args.restaurant.id).observe(viewLifecycleOwner, {
            if ( it != null ) {
                Log.e("boom", it.name)
                args.restaurant.images = it.images
                args.restaurant.images.distinct()
            }
        })


        imageSlider = binding.slider

        val listUrl: ArrayList<String> = ArrayList()
        val listName: ArrayList<String> = ArrayList()
        args.restaurant.images.forEach{
            listUrl.add(it);
            listName.add(it.lastIndex.toString())
        }

        val requestOptions = RequestOptions()
        requestOptions.centerCrop()
        for (i in 0 until listUrl.size) {
            val sliderView = TextSliderView(binding.root.context)
            sliderView
                .image(listUrl[i])
                .description(listName[i])
                .setRequestOption(requestOptions)
                .setProgressBarVisible(true)

            imageSlider.addSlider(sliderView)
        }

        imageSlider.setPresetTransformer(SliderLayout.Transformer.Stack)

        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        imageSlider.setCustomAnimation(DescriptionAnimation())
        imageSlider.setDuration(4000)
        imageSlider.stopCyclingWhenTouch(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = args.restaurant.name
        setListeners()
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }



    //Handle result from opening Gallery and selecting a picture
    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // When an Image is picked
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && null != data) {
            if (data.data != null) {
                    val file: File = FileUtil.from(requireActivity(), data.data!!)
                    val requestOptions = RequestOptions()
                    requestOptions.centerCrop()
                    val sliderView = TextSliderView(binding.root.context)
                    args.restaurant.images.add(file.absolutePath)
                    viewModel.addToFavorites(args.restaurant)
                    sliderView
                        .image(file)
                        .description(file.nameWithoutExtension)
                        .setRequestOption(requestOptions)
                        .setProgressBarVisible(true)

                    imageSlider.addSlider(sliderView)
            }
        }
    }

    private fun setListeners() {
        //Add image with intent from gallery
        binding.addImageCard.setOnClickListener{
            val intent = Intent()
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }
        //Delete image button
        binding.deleteImageButton.setOnClickListener{
            if(imageSlider.sliderImageCount > 1){
//                Log.e("current url:",imageSlider.currentSlider.url)
                args.restaurant.images.remove(imageSlider.currentSlider.url)
                imageSlider.removeSliderAt(imageSlider.currentPosition)
            Log.e("new size:", args.restaurant.images.size.toString())
                viewModel.addToFavorites(args.restaurant)
            }

        }
        //Toggle favorite with button/card
        binding.favoriteCard.setOnClickListener{
            binding.buttonFavorite.isChecked = !binding.buttonFavorite.isChecked
            args.restaurant.favorite = binding.buttonFavorite.isChecked
            viewModel.addToFavorites(args.restaurant)
            Log.e("fav", args.restaurant.favorite.toString())

        }
        //Open Google MAPS intent with bubble
        binding.mapCard.setOnClickListener{
            val uri = "http://maps.google.com/maps?q=loc:" + args.restaurant.lat.toString() + "," + args.restaurant.lng.toString()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps");
            binding.root.context.startActivity(intent)
        }
        //Copy restaurant reserve number into dialer
        binding.callCard.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_DIAL // Action for what intent called for
            intent.data = Uri.parse("tel: ${args.restaurant.phone}") // Data with intent respective action on intent
            startActivity(intent)
        }

    }

    //Stop auto-play
    override fun onStop() {
        binding.slider.stopAutoCycle()
        super.onStop()
    }

}