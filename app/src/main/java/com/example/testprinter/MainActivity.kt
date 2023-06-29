package com.example.testprinter

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bolt.consumersdk.CCConsumer
import com.bolt.consumersdk.domain.CCConsumerAccount
import com.bolt.consumersdk.domain.CCConsumerError
import com.bolt.consumersdk.swiper.CCSwiperController
import com.bolt.consumersdk.swiper.SwiperControllerListener
import com.bolt.consumersdk.swiper.enums.BatteryState
import com.bolt.consumersdk.swiper.enums.SwiperCaptureMode
import com.bolt.consumersdk.swiper.enums.SwiperError
import com.bolt.consumersdk.swiper.enums.SwiperType


class MainActivity : AppCompatActivity() {
    private var listView: ListView? = null
    private val mDeviceList = ArrayList<String>()
    private var mBluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.btnStart).setOnClickListener {
            mDeviceList.clear()
            listMac.clear()
            listView!!.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1, mDeviceList
            )
            requestPermission()
        }
        listView = findViewById(R.id.listView)
        listView?.setOnItemClickListener { _, _, position, _ ->
            val device = mBluetoothAdapter?.getRemoteDevice(listMac[position])
            Log.e("MainActivity",if (device == null) "Device null" else "Device not null")
            device?.createBond()
//            CCSwiperControllerFactory().create(this, SwiperType.IDTech, swiperListener, listMac[position], false)
            mSwiperControllerManager.setMACAddress(listMac[position])
            mSwiperControllerManager.setContext(this@MainActivity)
            mSwiperControllerManager.setSwiperCaptureMode(SwiperCaptureMode.SWIPE_INSERT)
            mSwiperControllerManager.setSwiperControllerListener(swiperListener)
            mSwiperControllerManager.setSwiperType(SwiperType.IDTech)
            mSwiperControllerManager.connectToDevice()
//            swiperListener.onSwiperConnected()
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                getBluetooth()
            } else {
                Log.e("MainActivity","MainActivity not per")
            }
        }

    private fun getBluetooth() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)
        val mBluetoothManager = getSystemService(BluetoothManager::class.java)
        mBluetoothAdapter = mBluetoothManager.adapter
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.e("MainActivity","MainActivity requestPermission")
            requestPermission()
            return
        }
        mBluetoothAdapter?.startDiscovery()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.e("MainActivity","MainActivity >= Build.VERSION_CODES.S")
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT))
        }
        else{
            Log.e("MainActivity","MainActivity < Build.VERSION_CODES.S")
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        }


    }

    private val mSwiperControllerManager = SwiperControllerManager().getInstance()

    private val swiperListener = object : SwiperControllerListener {
        override fun onTokenGenerated(p0: CCConsumerAccount?, p1: CCConsumerError?) {
            Log.e("Truong","onTokenGenerated ${p0?.name}")
        }

        override fun onError(p0: SwiperError?) {
            Log.e("Truong","onError ${p0?.name}")
        }

        override fun onSwiperReadyForCard() {
            Log.e("Truong","onSwiperReadyForCard")
        }

        override fun onSwiperConnected() {
            Log.e("Truong","onSwiperConnected")
            resetSwiper()
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

    private fun resetSwiper() {
        (mSwiperControllerManager.getSwiperController() as CCSwiperController).startReaders(
            mSwiperControllerManager.getSwiperCaptureMode()
        )
    }
    private val listMac = arrayListOf<String>()
    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = if (Build.VERSION.SDK_INT >= 33) {
                    intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE,BluetoothDevice::class.java)
                } else
                    intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    requestPermission()
                    return
                }
                if (device?.name != null) {
                    mDeviceList.add("""${device.name}${device.address}""".trimIndent())
                    listMac.add(device.address)
                    Log.i("MainActivity", """${device.name}--${device.address}""".trimIndent())
                }
                listView!!.adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_list_item_1, mDeviceList
                )
            }
        }
    }



    private val mOnItemClickListener =
        OnItemClickListener { parent, view, position, id ->
            val api = CCConsumer.getInstance().api

        }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

}