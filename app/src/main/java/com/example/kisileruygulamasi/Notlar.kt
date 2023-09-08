package com.example.kisileruygulamasi

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Notlar(var not_id:String? ="",
                  var not_ad:String? ="",
                  var not_aciklama:String? ="") {
}