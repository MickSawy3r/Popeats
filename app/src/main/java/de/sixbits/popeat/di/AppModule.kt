package de.sixbits.popeat.di

import android.app.Application
import android.content.Context
import android.location.LocationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.sixbits.popeat.BuildConfig
import de.sixbits.popeat.network.VenueRepository
import de.sixbits.popeat.service.ILocationService
import de.sixbits.popeat.service.LocationService
import de.sixbits.popeat.utils.LocationHelper
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@InstallIn(SingletonComponent::class)
@Module(
    subcomponents = [
    ]
)
open class AppModule {
    @Provides
    fun provideRetrofitInstance(client: OkHttpClient.Builder): Retrofit {

        return Retrofit
            .Builder()
            .client(client.build())
            .baseUrl(BuildConfig.FOURSQUARE_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    fun provideHttpClient(): OkHttpClient.Builder {
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        clientBuilder.addInterceptor(loggingInterceptor)
        return clientBuilder
    }

    @Provides
    fun provideGlideInstance(
        application: Application,
        requestOptions: RequestOptions
    ): RequestManager {
        return Glide.with(application)
            .setDefaultRequestOptions(requestOptions)
    }

    @Provides
    fun provideVenueRepository(
        retrofit: Retrofit
    ): VenueRepository {
        return retrofit.create(VenueRepository::class.java)
    }

    @Provides
    fun provideLocationHelper(application: Application): LocationHelper {
        return LocationHelper(application)
    }

    @Provides
    fun provideLocationManager(application: Application): LocationManager {
        return application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @Provides
    fun provideSettingsClient(application: Application): SettingsClient {
        return LocationServices.getSettingsClient(application)
    }

    @Provides
    fun provideLocationService(
        fusedLocationProviderClient: FusedLocationProviderClient,
        settingsClient: SettingsClient
    ): ILocationService {
        return LocationService(fusedLocationProviderClient, settingsClient)
    }

    @Provides
    fun provideFusedLocationProviderClient(application: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(application)
    }

    @Provides
    fun provideFirebase(application: Application): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(application)
    }
}
