package com.example.project.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.project.databinding.DetailFragmentBinding
import com.google.android.material.transition.platform.MaterialFade
import com.google.android.material.transition.platform.MaterialFadeThrough
import java.util.*
import com.glide.slider.library.animations.DescriptionAnimation

import com.glide.slider.library.SliderLayout

import com.glide.slider.library.slidertypes.TextSliderView

import com.bumptech.glide.request.RequestOptions

import android.R
import android.app.Activity
import android.app.SearchManager.QUERY
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import com.glide.slider.library.slidertypes.BaseSliderView
import com.glide.slider.library.tricks.ViewPagerEx
import java.io.File
import android.os.Build
import android.provider.DocumentsContract

import android.content.ContentUris
import android.database.Cursor

import android.os.Environment
import com.example.project.helpers.RealPathUtil
import java.lang.Exception
import android.graphics.Bitmap
import android.provider.MediaStore.Downloads.INTERNAL_CONTENT_URI
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class DetailFragment : Fragment(){

    private lateinit var imageSlider: SliderLayout
    private lateinit var binding: DetailFragmentBinding
    private val args: DetailFragmentArgs by navArgs()
    private val PROJECTION = arrayOf(MediaStore.Video.Media._ID)
    private val QUERY = MediaStore.Video.Media.DISPLAY_NAME + " = ?"
    private val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Video.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        ) else MediaStore.Video.Media.EXTERNAL_CONTENT_URI


    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = MaterialFadeThrough()
        reenterTransition = MaterialFade()
        exitTransition = MaterialFadeThrough()

        binding =  DetailFragmentBinding.inflate(inflater, container, false)
        binding.nameView.text = args.restaurant.name
        binding.addressView.text = args.restaurant.address
        binding.cityView.text = args.restaurant.city + ", " + args.restaurant.country
        binding.priceRangeView.text = args.restaurant.priceRange()
        binding.buttonFavorite.isChecked = args.restaurant.favorite
//        Glide.with(binding.root.context)
//            .load("https://www.elitetraveler.com/wp-content/uploads/2007/02/Caelis_Barcelona_alta2A0200-1-730x450.jpg")
////            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3))) // for blur
//            .into(binding.restaurantImage)

        imageSlider = binding.slider

        val listUrl: ArrayList<String> = ArrayList()
        val listName: ArrayList<String> = ArrayList()

        listUrl.add(args.restaurant.image_url)
        listName.add("Original image")

        listUrl.add("https://www.elitetraveler.com/wp-content/uploads/2007/02/Caelis_Barcelona_alta2A0200-1-730x450.jpg")
        listName.add("Caelis Barcelona")

        val requestOptions = RequestOptions()
        requestOptions.centerCrop()
        for (i in 0 until listUrl.size) {
            val sliderView = TextSliderView(binding.root.context)
            sliderView
                .image(listUrl[i])
                .description(listName[i])
                .setRequestOption(requestOptions)
                .setProgressBarVisible(true)
//                .setOnSliderClickListener(this)

            imageSlider.addSlider(sliderView)
        }

        // set Slider Transition Animation
        // imageSlider.setPresetTransformer(SliderLayout.Transformer.Default);

        // set Slider Transition Animation
        // imageSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion)

        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        imageSlider.setCustomAnimation(DescriptionAnimation())
        imageSlider.setDuration(4000)
//        imageSlider.addOnPageChangeListener(this)
        imageSlider.stopCyclingWhenTouch(false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = args.restaurant.name
        setListeners()
    }


    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // When an Image is picked
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && null != data) {
            val image: String? = data.getStringExtra("IMAGE_KEY")
            Log.e("clipdata", image.toString());
            if (data.data != null) {
                val imagePath: String? = data.data?.path
                if (imagePath != null) {
                    val requestOptions = RequestOptions()
                    requestOptions.centerCrop()
                    val sliderView = TextSliderView(binding.root.context)
                    val photoUri = Uri.fromFile(File(imagePath))

                    sliderView
                        .image(File(photoUri.path))
                        .description("New image from gallery")
                        .setRequestOption(requestOptions)
                        .setProgressBarVisible(true)

                    imageSlider.addSlider(sliderView)
                    Log.e("imagePath", imagePath)
                };
            }
//            displayImageData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setListeners() {
        binding.addImageCard.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK,INTERNAL_CONTENT_URI)
            startActivityForResult(intent,1)
        }
        binding.favoriteCard.setOnClickListener{
            binding.buttonFavorite.isChecked = !binding.buttonFavorite.isChecked
            if ( binding.buttonFavorite.isChecked ){
                Log.d("true:", binding.buttonFavorite.isChecked.toString())
                //TOOD: add to favorites
            }
            else{
                Log.d("false:", binding.buttonFavorite.isChecked.toString())
                //TOOD: remove from favorites
            }

        }
        binding.mapCard.setOnClickListener{
            val uri = "http://maps.google.com/maps?q=loc:" + args.restaurant.lat.toString() + "," + args.restaurant.lng.toString()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps");
            binding.root.context.startActivity(intent)
            Log.d("map", "Opening map!")
        }
        binding.callCard.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_DIAL // Action for what intent called for
            intent.data = Uri.parse("tel: ${args.restaurant.phone}") // Data with intent respective action on intent
            startActivity(intent)
        }

    }

    override fun onStop() {
        binding.slider.stopAutoCycle()
        super.onStop()
    }
//    override fun onSliderClick(slider: BaseSliderView?) {
//        Toast.makeText(binding.root.context, "clicked", Toast.LENGTH_SHORT).show();
//    }
}