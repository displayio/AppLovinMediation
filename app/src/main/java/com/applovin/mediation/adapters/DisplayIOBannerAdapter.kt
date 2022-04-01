package com.applovin.mediation.adapters

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.adapter.MaxAdViewAdapter
import com.applovin.mediation.adapter.MaxAdapter
import com.applovin.mediation.adapter.MaxAdapterError
import com.applovin.mediation.adapter.MaxInterstitialAdapter
import com.applovin.mediation.adapter.listeners.MaxAdViewAdapterListener
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkUtils.runOnUiThread
import com.brandio.ads.*
import com.brandio.ads.ads.Ad
import com.brandio.ads.containers.InfeedAdContainer
import com.brandio.ads.exceptions.DIOError
import com.brandio.ads.listeners.AdEventListener
import com.brandio.ads.listeners.AdLoadListener
import com.brandio.ads.listeners.AdRequestListener
import com.brandio.ads.listeners.SdkInitListener


class DisplayIOMediationAdapter(sdk: AppLovinSdk?) : MediationAdapterBase(sdk), MaxAdViewAdapter,
    MaxInterstitialAdapter {
    private val TAG = "DisplayIOMediation";
    private var interstitialDIOAd: Ad? = null
    companion object {
        const val APP_ID = "7729"
    }

    override fun initialize(
        params: MaxAdapterInitializationParameters?,
        activity: Activity?,
        listener: MaxAdapter.OnCompletionListener?
    ) {

        if (!Controller.getInstance().isInitialized) {
            listener?.onCompletion(
                MaxAdapter.InitializationStatus.INITIALIZING,
                null
            )
            runOnUiThread {
                Controller.getInstance().init(
                    activity!!,
                    null,
                    Companion.APP_ID,
                    object : SdkInitListener {
                        override fun onInit() {
                            listener?.onCompletion(
                                MaxAdapter.InitializationStatus.INITIALIZED_SUCCESS,
                                null
                            )
                            Toast.makeText(activity, "DIO Initialized!", Toast.LENGTH_SHORT).show()
                        }

                        override fun onInitError(p0: DIOError?) {
                            listener?.onCompletion(
                                MaxAdapter.InitializationStatus.INITIALIZED_FAILURE,
                                null
                            )
                        }
                    }
                )
            }

        } else {
            listener?.onCompletion(
                MaxAdapter.InitializationStatus.INITIALIZED_SUCCESS,
                null
            )
        }
    }


    override fun getSdkVersion(): String {
        return Controller.getInstance().ver
    }

    override fun getAdapterVersion(): String {
        return Controller.getInstance().ver
    }

    override fun onDestroy() {
        Controller.getInstance().onDestroy()
    }

    override fun loadAdViewAd(
        parameters: MaxAdapterResponseParameters?,
        format: MaxAdFormat?,
        activity: Activity?,
        listener: MaxAdViewAdapterListener?
    ) {

        if (!Controller.getInstance().isInitialized) {
            Log.e("Adapter", "DIO SDK is not initialized!")
            listener?.onAdViewAdLoadFailed(MaxAdapterError.UNSPECIFIED)
            return
        }

        val plcID = parameters?.thirdPartyAdPlacementId
        requestAndLoadDisplayIOAd(
            plcID,
            listener,
            null,
            activity
        )
    }

    private fun requestAndLoadDisplayIOAd(
        plcID: String?,
        listener: MaxAdViewAdapterListener?,
        interstitialListener: MaxInterstitialAdapterListener?,
        activity: Activity?
    ) {
        Log.e(TAG, "requestAndLoadDisplayIOAd")
        // check type of the placement before cast to BannerPlacement or any other
        var placement = try {
            Controller.getInstance().getPlacement(plcID)
        } catch (e: Exception) {
            listener?.onAdViewAdLoadFailed(MaxAdapterError.INTERNAL_ERROR)
            return
        }

        val adRequest = placement.newAdRequest()
        adRequest.setAdRequestListener(object : AdRequestListener {
            override fun onAdReceived(adProvider: AdProvider?) {
                adProvider?.setAdLoadListener(object : AdLoadListener {
                    override fun onLoaded(ad: Ad?) {
                        Log.e(TAG, "onLoaded")

                        var adView: View? = null
                        // check type of the placement before retrieve ad view
                        // can be also checked with placement IDs
                        when (placement) {
                            is BannerPlacement -> {
                                adView = placement.getBanner(
                                    activity,
                                    adRequest.id
                                )
                            }
                            is MediumRectanglePlacement -> {
                                adView = placement.getMediumRectangle(
                                    activity,
                                    adRequest.id
                                )
                            }
                            is InfeedPlacement -> {
                                adView = InfeedAdContainer.getAdView(activity)
                                val infeedContainer =
                                    placement.getInfeedContainer(activity, adRequest.id)
                                infeedContainer.bindTo(adView)
                            }
                            is InterstitialPlacement -> {
                                Log.e(TAG, "onInterstitialAdLoaded")

                                interstitialDIOAd = ad
                                interstitialListener?.onInterstitialAdLoaded()
                                Log.e(TAG, "after notify interstitialListener?.onInterstitialAdLoaded()")

                                return
                            }
                        }

                        if (adView != null) {
                            listener?.onAdViewAdLoaded(adView)
                        } else {
                            listener?.onAdViewAdLoadFailed(MaxAdapterError.NO_FILL)
                        }
                    }

                    override fun onFailedToLoad(p0: DIOError?) {
                        Log.e(TAG, "onFailedToLoad")

                        listener?.onAdViewAdLoadFailed(MaxAdapterError.UNSPECIFIED)
                        interstitialListener?.onInterstitialAdLoadFailed(MaxAdapterError.UNSPECIFIED)
                    }
                })
                adProvider!!.loadAd()
            }

            override fun onNoAds(e: DIOError?) {
                Log.e(TAG, "onNoAds")

                listener?.onAdViewAdLoadFailed(MaxAdapterError.NO_FILL)
                interstitialListener?.onInterstitialAdLoadFailed(MaxAdapterError.NO_FILL)

            }
        })
        Log.e(TAG, "before requestAd()")

        adRequest.requestAd()
    }

    override fun loadInterstitialAd(
        parameters: MaxAdapterResponseParameters?,
        activity: Activity?,
        listener: MaxInterstitialAdapterListener?
    ) {
        val plcID = parameters?.thirdPartyAdPlacementId

        requestAndLoadDisplayIOAd(
            plcID,
            null,
            listener,
            activity
        )
    }

    override fun showInterstitialAd(
        parameters: MaxAdapterResponseParameters?,
        activity: Activity?,
        listener: MaxInterstitialAdapterListener?
    ) {
        Log.e(TAG, "showInterstitialAd")

        if (interstitialDIOAd != null){
           interstitialDIOAd!!.setEventListener(object : AdEventListener() {
               override fun onShown(p0: Ad?) {
                   listener?.onInterstitialAdDisplayed()
               }

               override fun onFailedToShow(p0: Ad?) {
                   listener?.onInterstitialAdDisplayFailed(MaxAdapterError.AD_DISPLAY_FAILED)
               }

               override fun onClicked(p0: Ad?) {
                   listener?.onInterstitialAdClicked()

               }

               override fun onClosed(p0: Ad?) {
                   listener?.onInterstitialAdHidden()
               }

           })
           interstitialDIOAd!!.showAd(activity)
       }
    }
}