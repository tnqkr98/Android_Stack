package com.tnqkr98.kotlinauthtestcode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Base64
import android.util.Log
import android.widget.EditText
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.tnqkr98.kotlinauthtestcode.RetrofitClient.gosleepApiService
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var verfiyEt:EditText
    }

    private var expireTime = 180
    private var mTimerHandler:Handler? = null
    private var mTimerRunnable:Runnable = object :Runnable{
        override fun run() {
            expireTime -= 1
            var min = expireTime/60
            var sec = expireTime - min*60
            if(expireTime > 0)
                mTimerHandler?.postDelayed(this,1000);
            else
                expireTime = 180;

            var remainTime = String.format("$min:%2d",sec)
            runOnUiThread { tx_Expiration.text = "만료 시간 : $remainTime" }
        }
    }

    var smsReceiver = SMSReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        verfiyEt = findViewById(R.id.et_Verify)

        var mTimerThread = HandlerThread("backgroundThread")
        mTimerThread?.start()
        mTimerHandler = Handler(mTimerThread!!.looper)

        registerReceiver(smsReceiver,smsReceiver.doFilter())

        bt_AuthMsg.setOnClickListener {
            var task = SmsRetriever.getClient(this).startSmsRetriever()

            task.addOnSuccessListener {
                val hashcode = getAppSignatures(this)
                Log.d("gosleep","hash code : $hashcode")

                gosleepApiService.requestAuthMsg("01022115987",hashcode[0]).enqueue(object : Callback<Result>{
                    override fun onResponse(call: Call<Result>, response: Response<Result>) {
                        expireTime = 180
                        mTimerHandler?.postDelayed(mTimerRunnable,1000)
                    }
                    override fun onFailure(call: Call<Result>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }
            
            task.addOnFailureListener {
                Log.e("gosleep","Fail Hash : $it")
            }
        }

        bt_Confirm.setOnClickListener {
            // ToDo - Confirm API
            gosleepApiService.requestAuthConfirm("01022115987",et_Verify.text.toString()).enqueue(object : Callback<Result>{
                override fun onResponse(call: Call<Result>, response: Response<Result>) {
                    TODO("Not yet implemented")
                }
                override fun onFailure(call: Call<Result>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    override fun onDestroy() {
        unregisterReceiver(smsReceiver)
        super.onDestroy()
    }

    // 본 개발 시, 파일 분리 필요 MsgAutoAuth

    class SMSReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action){
                val extras = intent.extras
                var status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status

                when(status?.statusCode){
                    CommonStatusCodes.SUCCESS -> {
                        // Get SMS msg contents
                        val message = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as? String
                        Log.d("gosleep","Msg : $message")
                        if(!message.isNullOrEmpty()){
                            var code = message.substring(25,29)
                            verfiyEt.setText("$code")
                        }
                    }

                    CommonStatusCodes.TIMEOUT -> {
                        // 5minutes timeout
                        Log.d("Gosleep","timeout");
                    }
                }
            }
        }

        fun doFilter():IntentFilter = IntentFilter().apply {
            addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        }
    }

    fun getAppSignatures(context: Context) : List<String> {
        val appCodes = mutableListOf<String>()

        try{
            val packageName = context.packageName
            var packageManager = context.packageManager
            val signatures = packageManager.getPackageInfo(packageName,PackageManager.GET_SIGNATURES).signatures            // ToDo - deprecate 대체 확인 요망

            for(signature in signatures){
                val hash = getHash(packageName,signature.toCharsString())
                if(hash != null)
                    appCodes.add(String.format("%s",hash))
            }
        }
        catch(e : PackageManager.NameNotFoundException){
            e.printStackTrace()
        }

        return appCodes
    }

    private fun getHash(packageName : String , signature:String):String? {
        val appInfo = "$packageName $signature"

        try{
            val messageDigest = MessageDigest.getInstance("SHA-256")            // HASH_TYPE = "SHA-256"

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            }

            val hashSignature = Arrays.copyOfRange(messageDigest.digest(),0,9)          // NUM_HASHED_BYTES = 9

            var base64Hash = Base64
                .encodeToString(hashSignature,Base64.NO_PADDING or Base64.NO_WRAP)
                .substring(0,11)        // NUM_BASE64_CHAR = 11

            Log.d("gosleep", String.format("\nPackage : %s\nHash : %s", packageName, base64Hash))

            return base64Hash
        }
        catch(e:NoSuchAlgorithmException){
            Log.d("gosleep", "hash:NoSuchAlgorithm : $e")
        }
        return null
    }

}