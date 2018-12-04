package jp.co.mofumofu.pictureChainGame

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.*

const val SERVER_PORT = 8888

class WifiDirectContext(activity : Activity) : BroadcastReceiver() {

    enum class WifiDirectState {
        Unknown,
        Disable,
        EnableDiscoverPlayers,
        DiscoveringPlayers,
    }

    private var mManager = activity.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private var mChannel = mManager.initialize(activity, activity.mainLooper, null)
    private var mIntentFilter = IntentFilter()
    var mWifiDirectState = WifiDirectState.Unknown

    init {
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    fun onResume(activity: Activity) {
        activity.registerReceiver(this, mIntentFilter)
    }

    fun onPause(activity: Activity)  {
        activity.unregisterReceiver(this)
    }

    override fun onReceive(context: Context, intent: Intent)  {
        val action = intent.action

        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                mWifiDirectState = if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)  {
                    WifiDirectState.EnableDiscoverPlayers // Wifi P2P is enabled
                } else  {
                    WifiDirectState.Disable // Wi-Fi P2P is not enabled
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {} // Call WifiP2pManager.requestPeers() to get a list of current peers
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {} // Respond to new connection or disconnections
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {} // Respond to this device's wifi state changing
            else -> {}
        }
    }

    fun getStateText(): String  {
        val stateText = when (mWifiDirectState) {
            WifiDirectState.Unknown -> "Unknown 状態です"
            WifiDirectState.Disable -> "Wifi P2P が有効ではありません。"
            WifiDirectState.EnableDiscoverPlayers -> "DiscoverPlayers() が呼べる状態です。"
            WifiDirectState.DiscoveringPlayers -> "DiscoverPlayers() 中です。"
        }

        return stateText + " (" + mWifiDirectState.toString() + ")"
    }

    fun discoverPlayers(roomName: String, userName: String, successCallback: (String) -> Unit, failureCallback: (Exception) -> Unit)  {
        assert(mWifiDirectState != WifiDirectState.EnableDiscoverPlayers)

        val instanceName = "mofumofuPictureChainGame_$roomName"
        val serviceType = "_presence._tcp"
        val record: Map<String, String> = mapOf(
                "listenPort" to SERVER_PORT.toString(),
                "userName" to userName
        )

        // Player 情報取得するときの Listener
        val txtListener = WifiP2pManager.DnsSdTxtRecordListener { fullDomainName, txtRecordMap, srcDevice -> Log.v("main", "hogehoge"); successCallback("test ${txtRecordMap[userName]}") }
        val serviceListener = WifiP2pManager.DnsSdServiceResponseListener { _, _, _ -> }

        try  {
            GlobalScope.launch {
                // ローカルサービスに登録
                Log.v("main", "ローカルサービスに登録")
                suspendCoroutine<Unit> { cont ->
                    val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(instanceName, serviceType, record)
                    mManager.addLocalService(mChannel, serviceInfo, object : WifiP2pManager.ActionListener {
                        override fun onSuccess() {
                            cont.resume(Unit)
                        }

                        override fun onFailure(reason: Int) {
                            cont.resumeWithException(Exception("failed addLocalService() $reason"))
                        }
                    })
                }

                // Listener 登録
                mManager.setDnsSdResponseListeners(mChannel, serviceListener, txtListener)

                Log.v("main", "Discover用 ServiceRequest を追加")
                // Discover用 ServiceRequest を追加
                suspendCoroutine<Unit> { cont ->
                    val serviceRequest = WifiP2pDnsSdServiceRequest.newInstance()
                    mManager.addServiceRequest(mChannel, serviceRequest, object : WifiP2pManager.ActionListener {
                        override fun onSuccess() {
                            cont.resume(Unit)
                        }

                        override fun onFailure(reason: Int) {
                            cont.resumeWithException(Exception("failed addServiceRequest() $reason"))
                        }
                    })
                }

                Log.v("main", "DiscoverServices を行う")
                // DiscoverServices を行う
                suspendCoroutine<Unit> { cont ->
                    mManager.discoverServices(mChannel, object : WifiP2pManager.ActionListener {
                        override fun onSuccess() {
                            Log.v("main", "DiscoverServices を行う onSuccess")
                            cont.resume(Unit)
                        }

                        override fun onFailure(reason: Int) {
                            cont.resumeWithException(Exception("failed addServiceRequest() $reason"))
                        }
                    })
                }
            }
        }
        catch (e: Exception) {
            failureCallback(e)
        }
    }

    /*
    fun sendMessage()
    {
        var serverSocket = ServerSocket(8888)
        var client = serverSocket.accept();
        var outputStream = client.getOutputStream()
        outputStream.write("test".toByteArray())
    }

    fun recvMessage()
    {
        var socket = Socket()
        socket.bind(null)
        socket.connect(InetSocketAddress(host, port))
    }
    */
}