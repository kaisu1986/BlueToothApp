package jp.co.mofumofu.bluetoothapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.BroadcastReceiver
import android.net.wifi.p2p.WifiP2pManager
import android.content.Context
import android.content.IntentFilter







class MainActivity : AppCompatActivity() {

    var mManager: WifiP2pManager? = null
    var mChannel: WifiP2pManager.Channel? = null
    var mReceiver: BroadcastReceiver? = null
    var mIntentFilter: IntentFilter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mManager!!.initialize(this, mainLooper, null)
        mReceiver = WiFiDirectBroadcastReceiver(mManager!!, mChannel!!, this)

        mIntentFilter = IntentFilter()
        mIntentFilter!!.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        mIntentFilter!!.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        mIntentFilter!!.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        mIntentFilter!!.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mReceiver, mIntentFilter)
    }

    /* unregister the broadcast receiver */
    override fun onPause() {
        super.onPause()
        unregisterReceiver(mReceiver)
    }
}
