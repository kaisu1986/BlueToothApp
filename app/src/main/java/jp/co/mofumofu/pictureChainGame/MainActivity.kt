package jp.co.mofumofu.pictureChainGame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.BroadcastReceiver
import android.net.wifi.p2p.WifiP2pManager
import android.content.Context
import android.content.IntentFilter

class MainActivity : AppCompatActivity() {

    private var mWifiDirectContext : WifiDirectContext? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)
    }

    override fun onResume() {
        super.onResume()
        mWifiDirectContext?.onResume(this)
    }

    /* unregister the broadcast receiver */
    override fun onPause() {
        super.onPause()
        mWifiDirectContext?.onPause(this)
    }
}
