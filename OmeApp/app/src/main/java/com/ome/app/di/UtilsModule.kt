package com.ome.app.di

import android.content.Context
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.data.ConnectionStatusListenerImpl
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
    fun provideConnectionStatusListener(@ApplicationContext context: Context): ConnectionStatusListener =
        ConnectionStatusListenerImpl(context)

}
