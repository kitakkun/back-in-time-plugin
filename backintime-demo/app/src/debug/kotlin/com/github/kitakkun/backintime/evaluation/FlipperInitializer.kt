package com.github.kitakkun.backintime.evaluation

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.soloader.SoLoader
import com.github.kitakkun.back_in_time_flux.BuildConfig
import com.github.kitakkun.backintime.runtime.flipper.BackInTimeFlipperPlugin

object FlipperInitializer {
    fun initFlipper(context: Context) {
        SoLoader.init(context, false)
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(context)) {
            val client = AndroidFlipperClient.getInstance(context)
            client.addPlugin(BackInTimeFlipperPlugin())
            client.start()
        }
    }
}
