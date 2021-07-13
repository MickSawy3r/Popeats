package de.sixbits.popeat.di

import android.app.Application
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import de.sixbits.popeat.BuildConfig
import de.sixbits.popeat.network.VenueRepository
import de.sixbits.popeat.service.FakeLocationService
import de.sixbits.popeat.service.ILocationService
import de.sixbits.popeat.utils.LocationHelper
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
@Module(subcomponents = [])
class TestAppModule {
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
    fun provideLocationService(
    ): ILocationService {
        return FakeLocationService()
    }
}