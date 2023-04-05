package com.example.broadcastreceiverpro

import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.broadcastreceiverpro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(
            binding.root
        )
        // 1. 브로드캐스터 리시버를 만들어서 바로 배터리 정보를 획득함.
//        registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)).apply {
//
//        }
        // 1. 브로드캐스터 리시버를 만들어서 바로 배터리 정보를 획득함.
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intent = registerReceiver(null, intentFilter)
        //2. 배터리 충전 상태
        val extra_status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        Log.e("ddd", "${extra_status}")
        when(extra_status){
            // 충전정보 체크 usb 로 충전중이냐, AC 로 충전중
            BatteryManager.BATTERY_STATUS_CHARGING -> {
                when(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)){
                    BatteryManager.BATTERY_PLUGGED_AC -> {
                        binding.ivBattery.setImageResource(R.drawable.ac)
                        binding.tvInfo.text = "PLUGGED_AC"
                    }
                    BatteryManager.BATTERY_PLUGGED_USB -> {
                        binding.ivBattery.setImageResource(R.drawable.usbdrive)
                        binding.tvInfo.text = "PLUGGED_USB"
                    }
                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> {
                        binding.ivBattery.setImageResource(R.drawable.wirelesscharging)
                        binding.tvInfo.text = "PLUGGED_WIRELESS"
                    }
                    else ->{
                        binding.ivBattery.setImageResource(R.drawable.battery_full)
                        binding.tvInfo.text = "FULL CHARGING"
                    }
                }


            }
            //  NO 충전중
            else -> {
                binding.ivBattery.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.battery_unknown))
                binding.tvInfo.text = "NO CHARGING"
            }
        }
        // 배터리 잔여량을 계산해서 보여줌.
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE,-1)
        var percent = (level!!.toFloat() / scale!!.toFloat()) * 100
        binding.tvPercent.text = "${percent}%"

        // 이벤트 처리 (내가 만든 MyReceiver 불러서 Notification 알림 발생) : 부가적인 정보 배터리 양을 보내줌.
        binding.btnCallReceiver.setOnClickListener {
            val intent = Intent(this, MyReceiver::class.java)
            intent.putExtra("batteryPercent", "${binding.tvPercent.text}")
            sendBroadcast(intent)
        }
    }

}