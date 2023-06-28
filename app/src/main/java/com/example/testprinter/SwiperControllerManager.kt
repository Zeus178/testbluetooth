package com.example.testprinter

import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import com.bolt.consumersdk.CCConsumer
import com.bolt.consumersdk.domain.CCConsumerAccount
import com.bolt.consumersdk.domain.CCConsumerError
import com.bolt.consumersdk.swiper.CCSwiperControllerFactory
import com.bolt.consumersdk.swiper.SwiperController
import com.bolt.consumersdk.swiper.SwiperControllerListener
import com.bolt.consumersdk.swiper.enums.BatteryState
import com.bolt.consumersdk.swiper.enums.SwiperCaptureMode
import com.bolt.consumersdk.swiper.enums.SwiperError
import com.bolt.consumersdk.swiper.enums.SwiperType

class SwiperControllerManager {
    var TAG = SwiperControllerManager::class.java.simpleName
    private val mInstance = this
    private var mDeviceMACAddress: String? = null
    private var mSwiperController: SwiperController? = null
    private var mSwiperControllerListener: SwiperControllerListener? = null
    private var mSwiperCaptureMode = SwiperCaptureMode.SWIPE_INSERT
    private var mSwiperType = SwiperType.BBPosDevice
    private var mContext: Context? = null
    private var bConnected = false

    fun getInstance(): SwiperControllerManager {
        return mInstance
    }


    fun setContext(context: Context?) {
        mContext = context
    }

    /***
     * Set bluetooth MAC Address of IDTECH Device
     * @param strMAC
     */
    fun setMACAddress(strMAC: String?) {
        var bReset = false
        if (strMAC == null || strMAC != mDeviceMACAddress) {
            bReset = true
        }
        mDeviceMACAddress = strMAC
    }

    fun getMACAddr(): String? {
        return mDeviceMACAddress
    }

    /***
     *
     * @param swiperCaptureMode
     */
    fun setSwiperCaptureMode(swiperCaptureMode: SwiperCaptureMode) {
        mSwiperCaptureMode = swiperCaptureMode
    }

    /***
     *
     * @return
     */
    fun getSwiperCaptureMode(): SwiperCaptureMode? {
        return mSwiperCaptureMode
    }

    /***
     *
     */
    fun connectToDevice() {
        if (mSwiperType == SwiperType.IDTech && TextUtils.isEmpty(mDeviceMACAddress)) {
            return
        }
        if (mContext == null || mDeviceMACAddress == null) {
            return
        }
        if (mSwiperController != null) {
            disconnectFromDevice()
            Handler().postDelayed({ createSwiperController() }, 5000)
        } else {
            createSwiperController()
        }
    }

    /***
     * Create a swiper controller based on the defined swiper type
     */
    private fun createSwiperController() {
        var swiperController: SwiperController? = null
        swiperController = CCSwiperControllerFactory().create(
            mContext,
            mSwiperType,
            object : SwiperControllerListener {
                override fun onTokenGenerated(account: CCConsumerAccount, error: CCConsumerError) {
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onTokenGenerated(account, error)
                    }
                }

                override fun onError(swipeError: SwiperError) {
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onError(swipeError)
                    }
                }

                override fun onSwiperReadyForCard() {
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onSwiperReadyForCard()
                    }
                }

                override fun onSwiperConnected() {
                    bConnected = true
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onSwiperConnected()
                    }
                }

                override fun onSwiperDisconnected() {
                    bConnected = false
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onSwiperDisconnected()
                    }
                }

                override fun onBatteryState(batteryState: BatteryState) {
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onBatteryState(batteryState)
                    }
                }

                override fun onStartTokenGeneration() {
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onStartTokenGeneration()
                    }
                }

                override fun onLogUpdate(strLogUpdate: String) {
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onLogUpdate(strLogUpdate)
                    }
                }

                override fun onDeviceConfigurationUpdate(s: String) {
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onDeviceConfigurationUpdate(s)
                    }
                    Log.d(TAG, "onDeviceConfigurationUpdate: $s")
                }

                override fun onConfigurationProgressUpdate(v: Double) {}
                override fun onConfigurationComplete(b: Boolean) {}
                override fun onTimeout() {
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onTimeout()
                    }
                }

                override fun onLCDDisplayUpdate(str: String) {
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onLogUpdate(str)
                    }
                }

                override fun onRemoveCardRequested() {
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onRemoveCardRequested()
                    }
                }

                override fun onCardRemoved() {
                    if (mSwiperControllerListener != null) {
                        mSwiperControllerListener!!.onCardRemoved()
                    }
                }

                override fun onDeviceBusy() {}
            },
            mDeviceMACAddress,
            false
        )
        if (swiperController == null) {
            //Connection to device failed.  Device may be busy, wait and try again.
            val handler = Handler()
            handler.postDelayed({ createSwiperController() }, 5000)
        } else {
            mSwiperController = swiperController
        }
        return
    }

    /***
     * Disconnect from swiper device
     */
    fun disconnectFromDevice() {
        mSwiperController!!.release()
        mSwiperController = null
    }

    /***
     *
     * @param swiperControllerListener
     */
    fun setSwiperControllerListener(swiperControllerListener: SwiperControllerListener?) {
        mSwiperControllerListener = swiperControllerListener
    }

    /***
     *
     * @return true if swiper is connected.
     */
    fun isSwiperConnected(): Boolean {
        return bConnected
    }

    /***
     *
     * @return SwiperController Object
     */
    fun getSwiperController(): SwiperController? {
        return mSwiperController
    }

    /***
     *
     * @return the type of swiper supported by the current controller
     */
    fun getSwiperType(): SwiperType? {
        return mSwiperType
    }

    /***
     * Used to set define the type of swiper to create a controller for.  ID_TECH VP3300 or BBPOS
     * @param swiperType
     */
    fun setSwiperType(swiperType: SwiperType) {
        var bReset = false
        if (mSwiperType != swiperType) {
            bReset = true
        }
        mSwiperType = swiperType
        setupConsumerApi()
        if ((bReset || mSwiperController == null) && !TextUtils.isEmpty(mDeviceMACAddress)) {
            createSwiperController()
        }
    }

    /**
     * Initial Configuration for Consumer Api
     */
    private fun setupConsumerApi() {
        when (this.getInstance().getSwiperType()) {
            SwiperType.BBPosDevice ->                 //CCConsumer.getInstance().getApi().setEndPoint("https://fts-uat.cardconnect.com");
                CCConsumer.getInstance().api.setEndPoint(mContext!!.getString(R.string.cardconnect_prod_post_url))

            SwiperType.IDTech -> CCConsumer.getInstance().api.setEndPoint(mContext!!.getString(R.string.cardconnect_prod_post_url))
            else -> {}
        }
        CCConsumer.getInstance().api.setDebugEnabled(true)
    }
}