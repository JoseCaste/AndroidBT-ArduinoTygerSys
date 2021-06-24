package com.unistmo.tygersys.ui.home

import android.app.Activity
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.unistmo.tygersys.R
import com.unistmo.tygersys.ui.home.HomeFragment.Companion.m_address
import com.unistmo.tygersys.ui.home.HomeFragment.Companion.m_bluetoothAdapter
import com.unistmo.tygersys.ui.home.HomeFragment.Companion.m_bluetoothSocket
import com.unistmo.tygersys.ui.home.HomeFragment.Companion.m_isConnected
import com.unistmo.tygersys.ui.home.HomeFragment.Companion.m_myUUID
import com.unistmo.tygersys.ui.home.HomeFragment.Companion.m_progress
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import com.unistmo.tygersys.ui.bluetooth.ConnectToDevice
import com.unistmo.tygersys.ui.gallery.GalleryFragment


class HomeFragment : Fragment() {

    private var root: View? = null

    private var m_bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1
    private var select_device_refresh: Button? = null
    private var select_device_list: ListView? = null


    companion object {
        const val EXTRA_ADDRESS: String = "FC:A8:9A:00:08:82"
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_progress: ProgressDialog? = null
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }//FC:A8:9A:00:08:82


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_connect_bt, container, false)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!m_bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }
        select_device_refresh = root?.findViewById(R.id.select_device_refresh) as Button
        select_device_list = root?.findViewById(R.id.select_device_list) as ListView
        select_device_refresh?.setOnClickListener {

            pairedDeviceList()

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            if (m_bluetoothSocket != null) {
                m_bluetoothSocket?.close()
                m_bluetoothSocket = null
                GalleryFragment.m_isConnected = false;
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun pairedDeviceList() {
        println("buscando...")
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if (!m_pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                list.add(device)
                Log.i("device", "" + device)
            }
        } else {
            Toast.makeText(root?.context, "no paired bluetooth devices found", Toast.LENGTH_LONG)
                .show()

        }

        val adapter = ArrayAdapter(root?.context!!, android.R.layout.simple_list_item_1, list)
        select_device_list?.adapter = adapter
        select_device_list?.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val device: BluetoothDevice = list[position]
                val address: String = device.name
                Toast.makeText(root?.context, "Names´s bluetooth: $address", Toast.LENGTH_LONG)
                    .show()
                print(address)
                connectDevice()
            }

    }

    private fun connectDevice() {
        m_address = HomeFragment.EXTRA_ADDRESS

        ConnectToDevice(root!!.context).execute()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (m_bluetoothAdapter!!.isEnabled) {
                    Toast.makeText(root?.context, "Bluetooth has been enabled", Toast.LENGTH_LONG)
                        .show()

                } else {
                    Toast.makeText(root?.context, "Bluetooth has been disabled", Toast.LENGTH_LONG)
                        .show()

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(
                    root?.context,
                    "Bluetooth enabling has been canceled",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

class ConnectToDevice_(c: Context) : AsyncTask<Void, Void, String>() {

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
            if (m_bluetoothSocket == null || !m_isConnected) {
                m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                m_bluetoothSocket!!.connect()

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
            m_isConnected = true
        }
        Toast.makeText(context, "Conectado", Toast.LENGTH_LONG).show()
        m_progress?.dismiss()
    }


}
