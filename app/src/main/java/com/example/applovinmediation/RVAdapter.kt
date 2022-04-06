package com.example.applovinmediation

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.applovin.adview.AppLovinAdView

class RVAdapter(private val adView: View) : RecyclerView.Adapter<RVAdapter.ViewHolder>() {
    private val list: MutableList<Int?>

    init {
        list = (0..50).toMutableList()
        list[10] = null
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 0)  {
//            val innerAdView: View = (adView as ViewGroup).getChildAt(1)!!.apply {
//                layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT) }
            return ViewHolder(adView)
        }
        return ViewHolder(RelativeLayout(parent.context).apply {
            layoutParams =
                RelativeLayout.LayoutParams(MATCH_PARENT, 200).apply { setMargins(10, 10, 10, 10) }
            setPadding(3, 3, 3, 3)
            setBackgroundColor(Color.BLUE)
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}