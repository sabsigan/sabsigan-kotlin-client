package com.android.sabsigan.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.android.sabsigan.ViewModel.WiFiViewModel

class WifiConnectReceiver(private val viewModel: WiFiViewModel) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

            if (wifiInfo != null && wifiInfo.isConnected) {
                // 와이파이 연결됐을 때 처리
                viewModel.updateWiFiData("wifiInfo.isConnected")
//                Toast.makeText(context, "와이파이가 연결되었습니다.", Toast.LENGTH_SHORT).show()
            } else if (mobileInfo != null && mobileInfo.isConnected) {
                // 데이터 연결됐을 때 처리
                viewModel.updateWiFiData("mobileInfo.isConnected")
//                Toast.makeText(context, "데이터가 연결되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                // 와이파이 연결이 끊겼을 때 처리
                viewModel.updateWiFiData("wifiInfo.null")
//                Toast.makeText(context, "인터넷이 끊겼습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}