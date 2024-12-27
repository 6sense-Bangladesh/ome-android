package com.ome.app.di

import android.content.Context
import com.ome.app.data.ConnectionListener
import com.ome.app.data.ConnectionListenerImpl
import com.ome.app.data.local.NetworkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {

    @Singleton
    @Provides
    fun provideConnectionStatusListener(@ApplicationContext context: Context): ConnectionListener =
        ConnectionListenerImpl(context)

    @Provides
    @Singleton
    fun provideWifiManager(
        @ApplicationContext context: Context
    ): NetworkManager = NetworkManager(context)

}
