package com.unistmo.tygersys.ui.gallery

import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.unistmo.tygersys.R
import java.io.IOException
import java.util.*
import cn.pedant.SweetAlert.SweetAlertDialog;
import android.graphics.Color;

class GalleryFragment : Fragment(),TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {

    private var root: View?=null
    private var txtValue: EditText? = null
    private var btn_send: Button? = null
    var m_bluetoothSocket: BluetoothSocket? = null
    private lateinit var m_bluetoothAdapter: BluetoothAdapter
    private var connectSuccess: Boolean = true
    lateinit var spinner: Spinner
    private var alarm:String=""
    companion object {
        const val EXTRA_ADDRESS: String = "FC:A8:9A:00:08:82"
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_progress: ProgressDialog? = null
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_gallery, container, false)
        txtValue = root?.findViewById(R.id.txtValueA1) as EditText
        btn_send = root?.findViewById(R.id.btnSend) as Button
        spinner= root?.findViewById(R.id.spiner) as Spinner
        spinner!!.onItemSelectedListener = this
        txtValue?.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()

            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(root?.context, this, hour, minute,
                    DateFormat.is24HourFormat(root?.context))

            timePickerDialog.show()
        }
        //ConnectToDevice(root!!.context).execute()
        connectBT()
        btn_send?.setOnClickListener {
            //val alarm= if (spinner.selectedItem())
            sendCommand("$alarm:"+txtValue?.text.toString().trim())
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            if(m_bluetoothSocket!=null){
                m_bluetoothSocket?.close()
                m_bluetoothSocket=null
                m_isConnected=false;
            }
        }catch (e: IOException){
            e.printStackTrace()
        }

    }

    private fun connectBT() {
        m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        Toast.makeText(context, "Conectando...", Toast.LENGTH_LONG).show()

        try {
            if (m_bluetoothSocket == null || !m_isConnected) {
                m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(
                    EXTRA_ADDRESS
                )
                m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(
                    m_myUUID
                )
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                m_bluetoothSocket!!.connect()


            }
        } catch (e: IOException) {
            connectSuccess = false
            e.printStackTrace()
        }finally {
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                m_isConnected = true
            }
            Toast.makeText(context, "Conectado", Toast.LENGTH_LONG).show()
            m_progress?.dismiss()
        }
    }

    private fun sendCommand(input: String) {
        println("Gallery fragment send command")
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                SweetAlertDialog(root?.context)
                        .setTitleText("Configuración enviada")
                        .show()
            } catch (e: IOException) {
                e.printStackTrace()
                SweetAlertDialog(root?.context, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Verifique la conexión bluetooth")
                        .show();
            }
        }
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        //values is sended like "A2:02:44" and arduino it´s who will get subtring that only has alarm type, hour, minute
        txtValue?.setText(String.format("%02d",hourOfDay)+":"+String.format("%02d",minute))
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        println(position)
        if(position == 0) alarm="A1" else if (position == 1) alarm="A2"
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}

