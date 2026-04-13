package edu.cnm.deepdive.doggoneit.service.dogapi;

import android.util.Log;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import edu.cnm.deepdive.doggoneit.BuildConfig;
import jakarta.inject.Singleton;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
/**
 * Hilt module providing configured Dog API networking dependencies.
 */
public class DogApiModule {

  private static final String BASE_URL = "https://api.thedogapi.com/";
  private static final String API_KEY_HEADER = "x-api-key";
  private static final String HTTP_LOG_TAG = "DogApiHttp";

  @Provides
  @Singleton
  OkHttpClient provideOkHttpClient() {
    Interceptor apiKeyInterceptor = (chain) -> {
      Request request = chain.request()
          .newBuilder()
          .header(API_KEY_HEADER, BuildConfig.DOG_API_KEY)
          .build();
      return chain.proceed(request);
    };
    Interceptor httpDebugInterceptor = (chain) -> {
      Request request = chain.request();
      if (BuildConfig.DEBUG) {
        Log.i(HTTP_LOG_TAG, request.method() + " " + request.url());
      }
      okhttp3.Response response = chain.proceed(request);
      if (BuildConfig.DEBUG) {
        Log.i(HTTP_LOG_TAG, "Response code: " + response.code() + " for " + request.url());
      }
      return response;
    };
    return new OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(httpDebugInterceptor)
        .build();
  }

  @Provides
  @Singleton
  Retrofit provideRetrofit(OkHttpClient okHttpClient) {
    return new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
  }

  @Provides
  @Singleton
  DogApiService provideDogApiService(Retrofit retrofit) {
    return retrofit.create(DogApiService.class);
  }

}
