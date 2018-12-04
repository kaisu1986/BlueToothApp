package jp.co.mofumofu.pictureChainGame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_title.*

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

    fun onClickPlayButton(view : View) {
        if (userNameEditText.text.toString() == "") {
            val toast = Toast.makeText(this, "名前を入力してください", Toast.LENGTH_LONG)
            toast.show()
        }
        else if (mWifiDirectContext.mWifiDirectState == WifiDirectContext.WifiDirectState.EnableDiscoverPlayers) {
            val successCallback = { message: String-> Toast.makeText(this, "成功です $message", Toast.LENGTH_LONG).show() }
            val failureCallback =  { exception: Exception ->Toast.makeText(this, "失敗です $exception.message", Toast.LENGTH_LONG).show() }
            mWifiDirectContext.discoverPlayers(roomNameEditText.text.toString(), userNameEditText.text.toString(), successCallback, failureCallback)
        }
        else {
            val toast = Toast.makeText(this, mWifiDirectContext.getStateText(), Toast.LENGTH_LONG)
            toast.show()
        }
    }
}
