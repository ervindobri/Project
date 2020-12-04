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


class DetailFragment : Fragment() {

    private lateinit var binding: DetailFragmentBinding
    private val args: DetailFragmentArgs by navArgs()


    @SuppressLint("SetTextI18n")
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
        Glide.with(binding.root.context)
            .load("https://www.elitetraveler.com/wp-content/uploads/2007/02/Caelis_Barcelona_alta2A0200-1-730x450.jpg")
//            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3))) // for blur
            .into(binding.restaurantImage)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = args.restaurant.name
        setListeners()
    }

    private fun setListeners() {
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
}