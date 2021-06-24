package com.unistmo.tygersys.ui.bluetooth

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.unistmo.tygersys.ui.home.HomeFragment
import com.unistmo.tygersys.ui.home.HomeFragment.Companion.m_progress
import java.io.IOException

class ConnectToDevice (c: Context) : AsyncTask<Void, Void, String>() {
    private var connectSuccess: Boolean = true
    private var context: Context? = null

    init {
        this.context = c
    }

    override fun onPreExecute() {
        super.onPreExecute()
        m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        Toast.makeText(context, "Conectando...", Toast.LENGTH_LONG).show()
    }


    override fun doInBackground(vararg p0: Void?): String? {
        try {
            if (HomeFragment.m_bluetoothSocket == null || !HomeFragment.m_isConnected) {
                HomeFragment.m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val device: BluetoothDevice = HomeFragment.m_bluetoothAdapter.getRemoteDevice(
                    HomeFragment.m_address
                )
                HomeFragment.m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(
                    HomeFragment.m_myUUID
                )
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                HomeFragment.m_bluetoothSocket!!.connect()

            }
        } catch (e: IOException) {
            connectSuccess = false
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (!connectSuccess) {
            Log.i("data", "couldn't connect")
        } else {
            HomeFragment.m_isConnected = true
        }
        Toast.makeText(context, "Conectado", Toast.LENGTH_LONG).show()
        HomeFragment.m_progress?.dismiss()
    }
}