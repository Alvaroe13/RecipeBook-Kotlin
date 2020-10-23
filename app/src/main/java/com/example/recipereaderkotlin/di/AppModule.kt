package com.example.recipereaderkotlin.di

import android.content.Context
import androidx.room.Room
import com.example.recipereaderkotlin.service.cache.RecipeDatabase
import com.example.recipereaderkotlin.service.network.Api
import com.example.recipereaderkotlin.utils.Constants.BASE_URL
import com.example.recipereaderkotlin.utils.Constants.DATABASE_NAME_RECIPE_LIST
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    //-------------------- retrofit2 (webservice) ------------------------------------------------//

    @Provides
    @Singleton
    fun provideLogInterceptor() : HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    fun provideOkHttpClient( loggingInterceptor: HttpLoggingInterceptor) : OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient) : Retrofit =
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit) : Api =
        retrofit.create(Api::class.java)

    //------------------------------ Room (cache) -----------------------------------------------//

    @Provides
    @Singleton
    fun provideRecipeDb(@ApplicationContext context: Context) =
            Room.databaseBuilder(
                context,
                RecipeDatabase::class.java,
                DATABASE_NAME_RECIPE_LIST
            )
                .fallbackToDestructiveMigration()
                .build()

    @Provides
    @Singleton
    fun provideRecipeDao(recipeDatabase: RecipeDatabase) =
            recipeDatabase.getDao()

}