package com.ome.app.di

import android.content.Context
import com.ome.Ome.BuildConfig
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.local.PreferencesProviderImpl
import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.StoveService
import com.ome.app.data.remote.UserService
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.stove.StoveRepositoryImpl
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.data.remote.user.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideRetrofit(preferencesProvider: PreferencesProvider): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient =
            OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .addHeader("x-inirv-auth", preferencesProvider.getAccessToken() ?: "")
                    .addHeader("x-inirv-vsn", "6")
                    .addHeader("x-inirv-uid", preferencesProvider.getUserId() ?: "").build()
                chain.proceed(request)
            }).build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .build()

    }



    @Provides
    @Singleton
    fun provideStoveService(retrofit: Retrofit): StoveService =
        retrofit.create(StoveService::class.java)

    @Provides
    @Singleton
    fun provideStoveRepository(userService: StoveService): StoveRepository =
        StoveRepositoryImpl(userService)


    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideUserRepository(userService: UserService): UserRepository =
        UserRepositoryImpl(userService)

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): PreferencesProvider =
        PreferencesProviderImpl(context)

    @Provides
    @Singleton
    fun provideAmplifyManager(): AmplifyManager =
        AmplifyManager()

    @Provides
    @Singleton
    fun provideResourceProvider(@ApplicationContext context: Context): ResourceProvider =
        ResourceProvider(context)


}
