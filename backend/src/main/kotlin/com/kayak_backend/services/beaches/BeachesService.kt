package com.kayak_backend.services.slipways

import com.kayak_backend.models.BeachInfo
import com.kayak_backend.services.beaches.BeachesGetter
import com.kayak_backend.services.isleOfWightLocation1
import com.kayak_backend.services.isleOfWightLocation2
import okhttp3.OkHttpClient

class BeachesService(private val client: OkHttpClient = OkHttpClient()) {
    var beaches: List<BeachInfo> = listOf()
    val beachGetter = BeachesGetter(client, isleOfWightLocation1, isleOfWightLocation2)

    fun getAllBeaches(): List<BeachInfo> {
        if (beaches.isEmpty()) {
            beaches = beachGetter.getBeaches()
        }
        return beaches
    }
}
