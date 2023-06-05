package com.example.connect.di

import android.content.Context
import com.example.connect.ui.authentication.repo.AuthRepo
import com.example.connect.ui.authentication.repo.AuthRepoImpl
import com.example.connect.ui.main.repo.MainRepo
import com.example.connect.ui.main.repo.MainRepoImpl
import com.example.connect.utils.AppSharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepo(authRepoImpl: AuthRepoImpl): AuthRepo = authRepoImpl

    @Provides
    @Singleton
    fun provideMainRepo(mainRepoImpl: MainRepoImpl): MainRepo = mainRepoImpl

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideSharedPref(context: Context) = AppSharedPref(context)
}