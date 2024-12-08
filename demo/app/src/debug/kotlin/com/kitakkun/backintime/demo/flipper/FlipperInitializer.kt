package com.kitakkun.backintime.demo.flipper

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.soloader.SoLoader
import com.kitakkun.backintime.core.runtime.flipper.BackInTimeFlipperPlugin
import com.kitakkun.backintime.evaluation.BuildConfig

class FlipperInitializer {
    fun init(context: Context) {
        SoLoader.init(context, false)
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(context)) {
            val client = AndroidFlipperClient.getInstance(context)
            client.addPlugin(BackInTimeFlipperPlugin())
            client.start()
        }
    }
}
