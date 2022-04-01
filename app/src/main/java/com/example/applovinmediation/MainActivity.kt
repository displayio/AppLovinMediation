package com.example.applovinmediation

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity.CENTER
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.Toast
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.adapter.MaxAdapter
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.brandio.ads.Controller
import com.brandio.ads.exceptions.DIOError
import com.brandio.ads.listeners.SdkInitListener
import com.example.applovinmediation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var adView: MaxAdView? = null
    private lateinit var interstitialAd: MaxInterstitialAd

    private lateinit var rootAdView: ViewGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        rootAdView = findViewById(R.id.reserved_for_ad)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        initAppLovinSdk()
    }

    fun initAppLovinSdk() {
        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance(this).mediationProvider = AppLovinMediationProvider.MAX
        AppLovinSdk.getInstance(this).initializeSdk {
            showToast("AppLovin Initialized!")
        }
    }

    fun createAd(adUnitType: AdUnitType) {
        when (adUnitType) {
            AdUnitType.BANNER -> adView = MaxAdView("47dca8ad50135955", this)
            AdUnitType.MEDIUMRECT -> adView = MaxAdView("443e36ed302312ce", this)
            AdUnitType.INFEED -> adView = MaxAdView("f850d5aa98acf91b", this)
            AdUnitType.INTERSTITIAL -> {
                createInterstitialdAd("879e5078a8df075e")
                return
            }
        }
        adView?.setListener(object : MaxAdViewAdListener{
            // MAX Ad Listener for inline ads
            override fun onAdLoaded(maxAd: MaxAd) {
                showToast("Ad Loaded!")
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                showToast("Ad Load Failed!")
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                showToast("Ad Display Failed!")
            }

            override fun onAdClicked(maxAd: MaxAd) {
                showToast("Ad Clicked!")
            }

            override fun onAdExpanded(maxAd: MaxAd) {
                showToast("Ad Expanded!")
            }

            override fun onAdCollapsed(maxAd: MaxAd) {
                showToast("Ad Collapsed!")
            }

            /* DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY AND WILL BE REMOVED IN A FUTURE SDK RELEASE */
            override fun onAdDisplayed(ad: MaxAd?) {
                showToast("Ad Displayed!")
            }

            override fun onAdHidden(ad: MaxAd?) {
                showToast("Ad Hidden!")
            }

        })
        adView?.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, 800)
        adView?.gravity = CENTER
        adView?.setBackgroundColor(Color.WHITE)

        rootAdView.removeAllViews()
        rootAdView.addView(adView)

        // Load the ad
        adView?.loadAd()
    }

    fun createInterstitialdAd(adUnitId: String?) {
        interstitialAd = MaxInterstitialAd(adUnitId, this)

        // MAX Ad Listener for interstitial ads
        interstitialAd.setListener(object : MaxAdListener{
            override fun onAdLoaded(ad: MaxAd?) {
                showToast("Interstitial Ad Loaded!")
            }

            override fun onAdDisplayed(ad: MaxAd?) {
            }

            override fun onAdHidden(ad: MaxAd?) {
            }

            override fun onAdClicked(ad: MaxAd?) {
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
            }

        })

        // Load the first ad
        interstitialAd.loadAd()
    }


    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun showIntersitial() {
        interstitialAd.showAd()
    }

    enum class AdUnitType {
        BANNER,
        MEDIUMRECT,
        INFEED,
        INTERSTITIAL
    }
}