package com.android.sabsigan.wifidirectsample

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import com.android.sabsigan.wifidirectsample.event.ConnectionInfoEvent
import com.android.sabsigan.wifidirectsample.event.MyDeviceInfoEvent
import com.android.sabsigan.wifidirectsample.event.PeerListEvent
import com.android.sabsigan.wifidirectsample.event.ResetDataEvent
import com.android.sabsigan.wifidirectsample.event.StatusChangedEvent
import com.android.sabsigan.wifidirectsample.event.WifiEnable
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.os.Build
import androidx.core.app.ActivityCompat

class WifiDirectReceiver(private val manager : WifiP2pManager?, private val channel : Channel) : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
        // 와이파이 활성화 / 비활성화
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                sendStatusChange(state)
            }
        // 사용 가능한 피어 목록이 변경되었음을 나타내는 브로드 캐스트 인텐트, PEERS_CHANGED
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {

                // The peer list has changed! We should probably do something about
                // that.
                if (manager != null) {
                    val peerListListener =
                        WifiP2pManager.PeerListListener { peerList -> sendPeerListEvent(peerList) }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.NEARBY_WIFI_DEVICES
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return
                        }
                    }
                    manager.requestPeers(channel, peerListListener)
                    Log.d(TAG, "P2P peers changed")
                }

            }
        // Wifi p2p 연결 상태가 변경되었음을 나타내는 브로드캐스트 의도, CONNECTION_STATE_CHANGE
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {

                // Connection state changed! We should probably do something about
                // that.
                // Request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()

                // Connection state changed! We should probably do something about
                // that.
                if (manager != null) {
                    setConnectionInfo(intent, manager)
                }

            }
        // 해당 장치의 세부 정보가 변경되었음을 나타내는 브로드 캐스트 의도, THIS_DEVICE_CHANGED
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
//                (context.supportFragmentManager.findFragmentById(R.id.frag_list) as DeviceListFragment)
//                    .apply {
//                        updateThisDevice(
//                            intent.getParcelableExtra(
//                                WifiP2pManager.EXTRA_WIFI_P2P_DEVICE
//                            ) as WifiP2pDevice
//                        )
//                    }
                sendMyDeviceInfo(intent)
            }
        }


    }
    // 상태변경 보내기
    private fun sendStatusChange(state: Int) {
        //옵저버에게 값 전달
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            StatusChangedEvent.send(WifiEnable.ENABLE)
        } else {
            StatusChangedEvent.send(WifiEnable.DISABLE)
        }
    }

    // 접속 가능 와이파이 다이렉트 정보 보내기
    private fun sendPeerListEvent(peerList: WifiP2pDeviceList) {
        PeerListEvent.send(peerList)
    }

    // 내 정보 보내기
    private fun sendMyDeviceInfo(intent: Intent) {
        val myDeviceInfo = intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)

        myDeviceInfo?.let { MyDeviceInfoEvent.send(it) }

        println("WiFiDirectBroadcastReceiver myDeviceInfo : $myDeviceInfo")
    }
    // 접속정보 보내기
    private fun setConnectionInfo(intent: Intent, manager: WifiP2pManager) {
        val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
        if (networkInfo != null) {
            if (networkInfo.isConnected) {
                manager.requestConnectionInfo(channel) { info ->
                    Log.d(TAG, "WiFiDirectBroadcastReceiver setConnectionInfo : $info")
                    ConnectionInfoEvent.send(info)
                }
            }
//            else {
//                ResetDataEvent.send(true)
//            }
        }
    }
}
