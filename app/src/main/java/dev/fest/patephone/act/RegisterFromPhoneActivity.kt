package dev.fest.patephone.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.fest.patephone.R
import dev.fest.patephone.databinding.ActivityRegisterFromPhoneBinding

class RegisterFromPhoneActivity : AppCompatActivity() {

    private lateinit var registerFromPhoneBinding: ActivityRegisterFromPhoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerFromPhoneBinding = ActivityRegisterFromPhoneBinding.inflate(layoutInflater)
        setContentView(registerFromPhoneBinding.root)
    }
}