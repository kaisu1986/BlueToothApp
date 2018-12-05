package jp.co.mofumofu.pictureChainGame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_title.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mWifiDirectContext : WifiDirectContext

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
    fun onClickPlayButton(view : View) {
        if (userNameEditText.text.toString() == "") {
            val toast = Toast.makeText(this, "名前を入力してください", Toast.LENGTH_LONG)
            toast.show()
        }
        else if (mWifiDirectContext.mWifiDirectState == WifiDirectContext.WifiDirectState.EnableDiscoverPlayers) {
            val context = this

            GlobalScope.launch(Dispatchers.Unconfined) {
                val exception = mWifiDirectContext.prepareDiscoverPlayers(roomNameEditText.text.toString(), userNameEditText.text.toString())
                if (exception == null) {
                    setContentView(R.layout.activity_connecting)
                }
                else {
                    Toast.makeText(context, "失敗です $exception.message", Toast.LENGTH_LONG).show()
                }
            }
        }
        else {
            val toast = Toast.makeText(this, mWifiDirectContext.getStateText(), Toast.LENGTH_LONG)
            toast.show()
        }
    }
}
