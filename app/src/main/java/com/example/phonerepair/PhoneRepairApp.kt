package com.example.phonerepair

import android.app.Application
import com.example.phonerepair.data.RepairRepository

class PhoneRepairApp : Application() {
    val repository = RepairRepository()
}
