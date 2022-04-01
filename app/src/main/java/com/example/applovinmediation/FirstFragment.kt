package com.example.applovinmediation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.applovinmediation.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBanner.setOnClickListener {
            (activity as MainActivity).createAd(MainActivity.AdUnitType.BANNER)
        }
        binding.buttonMediumRect.setOnClickListener {
            (activity as MainActivity).createAd(MainActivity.AdUnitType.MEDIUMRECT)
        }
        binding.buttonInfeed.setOnClickListener {
            (activity as MainActivity).createAd(MainActivity.AdUnitType.INFEED)
        }
        binding.buttonLoadInterstitial.setOnClickListener {
            (activity as MainActivity).createAd(MainActivity.AdUnitType.INTERSTITIAL)
        }
        binding.buttonShowInterstitial.setOnClickListener {
            (activity as MainActivity).showIntersitial()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}