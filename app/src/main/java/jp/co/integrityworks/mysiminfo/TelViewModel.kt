package jp.co.integrityworks.mysiminfo

import androidx.databinding.ObservableField

class TelData(telData: List<String>) {
    var telData = ObservableField<List<String>>()
    var aaaa = "edelleeee"

    init {
        this.telData.set(telData)
    }
}