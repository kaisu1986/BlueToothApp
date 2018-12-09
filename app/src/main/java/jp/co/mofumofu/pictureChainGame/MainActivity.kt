package jp.co.mofumofu.pictureChainGame

import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_connecting.*
import kotlinx.android.synthetic.main.activity_title.*
import kotlinx.android.synthetic.main.include_loading.view.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var mWifiDirectContext: WifiDirectContext
    private var mLoadingView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWifiDirectContext = WifiDirectContext(this)
        setContentView(R.layout.activity_title)
    }

    override fun onResume() {
        super.onResume()
        mWifiDirectContext.onResume(this)
    }

    /* unregister the broadcast receiver */
    override fun onPause() {
        super.onPause()
        mWifiDirectContext.onPause(this)
    }

    @Suppress("UNUSED_PARAMETER")
    @ExperimentalCoroutinesApi
    fun onClickPlayButton(view: View) {
        if (userNameEditText.text.toString() == "") {
            val toast = Toast.makeText(this, "名前を入力してください", Toast.LENGTH_LONG)
            toast.show()
        } else if (mWifiDirectContext.mWifiDirectState == WifiDirectContext.WifiDirectState.EnableDiscoverPlayers) {
            showLoadingLayout()

            GlobalScope.launch(Dispatchers.IO) {
                val exception = mWifiDirectContext.prepareDiscoverPlayers(roomNameEditText.text.toString(), userNameEditText.text.toString())
                withContext(Dispatchers.Main) {
                    hideLoadingLayout()
                    if (exception == null) {
                        setContentView(R.layout.activity_connecting)
                        connectPlayerInfoRecyclerView.setHasFixedSize(true)
                        connectPlayerInfoRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                        connectPlayerInfoRecyclerView.adapter = ConnectPlayerInfoRecycleViewAdapter()
                        mWifiDirectContext.setDiscoverPlayerListener { list ->
                            val adapter = connectPlayerInfoRecyclerView.adapter as ConnectPlayerInfoRecycleViewAdapter
                            adapter.update(list)
                        }
                        mWifiDirectContext.startDiscoverPlayersDaemon()
                    } else {
                        Toast.makeText(this@MainActivity, "失敗です $exception.message", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            val toast = Toast.makeText(this, mWifiDirectContext.getStateText(), Toast.LENGTH_LONG)
            toast.show()
        }
    }

    private fun showLoadingLayout() {
        if (mLoadingView != null) {
            return
        }

        val rootViewGroup = this.findViewById<View>(android.R.id.content) as ViewGroup
        mLoadingView = layoutInflater.inflate(R.layout.include_loading, rootViewGroup)

        val anim = ObjectAnimator.ofFloat(mLoadingView!!.fader, "alpha", 0f, 0.2f)
        anim.duration = 30
        anim.start()
    }

    private fun hideLoadingLayout() {
        val anim = ObjectAnimator.ofFloat(mLoadingView!!.fader, "alpha", 0.2f, 0f)
        anim.duration = 30
        anim.start()
    }
}
