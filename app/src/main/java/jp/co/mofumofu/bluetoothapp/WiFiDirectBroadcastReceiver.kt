package jp.co.mofumofu.bluetoothapp

import android.net.wifi.p2p.WifiP2pManager
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.widget.TextView


/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
class WiFiDirectBroadcastReceiver(private val mManager: WifiP2pManager, private val mChannel: WifiP2pManager.Channel,
                                  activity: MainActivity) : BroadcastReceiver() {
    private val mActivity: MainActivity

    init {
        this.mActivity = activity
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                val textView = mActivity.findViewById<TextView>(R.id.text_view1)
                textView.setText("Wifi P2P is enabled")
            } else {
                // Wi-Fi P2P is not enabled
                val textView = mActivity.findViewById<TextView>(R.id.text_view1)
                textView.setText("Wifi P2P is not enabled")
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION == action) {
            // Respond to this device's wifi state changing
        }
    }
}