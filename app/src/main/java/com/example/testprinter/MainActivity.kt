package com.example.testprinter

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bolt.consumersdk.CCConsumer
import com.bolt.consumersdk.CCConsumerTokenCallback
import com.bolt.consumersdk.domain.CCConsumerAccount
import com.bolt.consumersdk.domain.CCConsumerError
import com.bolt.consumersdk.listeners.BluetoothSearchResponseListener
import com.bolt.consumersdk.swiper.SwiperControllerListener
import com.bolt.consumersdk.swiper.enums.BatteryState
import com.bolt.consumersdk.swiper.enums.SwiperError
import com.bolt.consumersdk.swiper.enums.SwiperType
import java.util.Collections


class MainActivity : AppCompatActivity(), CCConsumerTokenCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val apiBridgeImpl = ApiBridgeImpl()
//        Intent(this, PaymentAccountsActivity::class.java).also {
//            it.putExtra(API_BRIDGE_IMPL_KEY, apiBridgeImpl)
//            startActivityForResult(it, PAYMENT_ACTIVITY_REQUEST_CODE)
//        }
        findViewById<TextView>(R.id.btnStart).setOnClickListener {
            requestPermission()
        }
//        requestPermission()
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothAdapter.startDiscovery()

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent
                    .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                Log.e("Truong","Truong ${device?.name} -- ${device?.address}")
            }
        }
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT))
        }
        else{
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN))
        }


    }

    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            getBluetooth()
        }else{
            Log.e("Truong","Truong not per 111")
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                getBluetooth()
            } else {
                Log.e("Truong","Truong not per")
            }
        }
    private val mapDevices = Collections.synchronizedMap(HashMap<String, BluetoothDevice>())
    private var mBluetoothSearchResponseListener: BluetoothSearchResponseListener? = null
    @SuppressLint("HardwareIds", "MissingPermission")
    private fun getBluetooth() {
        val adp= BluetoothAdapter.getDefaultAdapter()
//        Log.e("Truong", "Truong ${adp.name}")
        val pairedDevices: Set<BluetoothDevice>? = adp?.bondedDevices
        val api = CCConsumer.getInstance().api
        Log.e("Truong", "Truong ${if (pairedDevices == null) "null" else "not null ${pairedDevices.size}"}")
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
            Log.e("Truong","Truong $deviceName -- $deviceHardwareAddress")
            api.connectToDevice(device, this)
            mSwiperControllerManager.setMACAddress(deviceHardwareAddress)
            mSwiperControllerManager.setContext(this)
            mSwiperControllerManager.setSwiperControllerListener(swiperListener)
            mSwiperControllerManager.setSwiperType(SwiperType.IDTech)
            mSwiperControllerManager.connectToDevice()
        }
        api.setEndPoint("https://fts.cardconnect.com:6443")
        mSwiperControllerManager.setContext(this)
        mSwiperControllerManager.setSwiperType(SwiperType.IDTech)
        api.startBluetoothDeviceSearch(mBluetoothSearchResponseListener, this@MainActivity, false)


    }

    override fun onResume() {
        super.onResume()
        mBluetoothSearchResponseListener =
            BluetoothSearchResponseListener { device ->
                Log.e("Truong","Truong")
                synchronized(mapDevices) {
                    mapDevices[device.address] = device
                }
            }
    }

    private val mSwiperControllerManager = SwiperControllerManager().getInstance()

    private val swiperListener = object : SwiperControllerListener {
        override fun onTokenGenerated(p0: CCConsumerAccount?, p1: CCConsumerError?) {
            Log.e("Truong","onTokenGenerated ${p0?.name}")
        }

        override fun onError(p0: SwiperError?) {
            Log.e("Truong","onError ${p0?.exceptionMessage}")
        }

        override fun onSwiperReadyForCard() {
            Log.e("Truong","onSwiperReadyForCard")
        }

        override fun onSwiperConnected() {
            Log.e("Truong","onSwiperConnected")
        }

        override fun onSwiperDisconnected() {
            Log.e("Truong","onSwiperDisconnected")
        }

        override fun onBatteryState(p0: BatteryState?) {
            Log.e("Truong","onBatteryState")
        }

        override fun onStartTokenGeneration() {
            Log.e("Truong","onStartTokenGeneration")
        }

        override fun onLogUpdate(p0: String?) {
            Log.e("Truong","onLogUpdate $p0")
        }

        override fun onDeviceConfigurationUpdate(p0: String?) {
            Log.e("Truong","onDeviceConfigurationUpdate $p0")
        }

        override fun onConfigurationProgressUpdate(p0: Double) {
            Log.e("Truong","onConfigurationProgressUpdate $p0")
        }

        override fun onConfigurationComplete(p0: Boolean) {
            Log.e("Truong","onConfigurationComplete $p0")
        }

        override fun onTimeout() {
            Log.e("Truong","onTimeout")
        }

        override fun onLCDDisplayUpdate(p0: String?) {
            Log.e("Truong","onLCDDisplayUpdate $p0")
        }

        override fun onRemoveCardRequested() {
            Log.e("Truong","onRemoveCardRequested")
        }

        override fun onCardRemoved() {
            Log.e("Truong","onCardRemoved")
        }

        override fun onDeviceBusy() {
            Log.e("Truong","onDeviceBusy")
        }

    }

    override fun onCCConsumerTokenResponseError(p0: CCConsumerError) {
        Log.e("Truong","onCCConsumerTokenResponseError ${p0.responseMessage}")
    }

    override fun onCCConsumerTokenResponse(p0: CCConsumerAccount) {
        Log.e("Truong","onCCConsumerTokenResponse ${p0.name}")
    }

}