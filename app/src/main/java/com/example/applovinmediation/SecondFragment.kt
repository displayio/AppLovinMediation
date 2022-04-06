package com.example.applovinmediation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.brandio.ads.Controller
import com.brandio.ads.InterscrollerPlacement
import com.brandio.ads.exceptions.DioSdkException
import com.example.applovinmediation.databinding.FragmentSecondBinding


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    val placementID = "7022"
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //important for Interscroller
        try {
            val placement =
                Controller.getInstance().getPlacement(placementID) as InterscrollerPlacement
            placement.parentRecyclerView = binding.recyclerView
        } catch (e: DioSdkException) {
            e.printStackTrace()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = (activity as MainActivity).adView?.let { RVAdapter(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}