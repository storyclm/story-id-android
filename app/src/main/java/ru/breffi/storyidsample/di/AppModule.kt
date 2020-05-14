package ru.breffi.storyidsample.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.breffi.storyidsample.Application
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
internal class AppModule {

    @Singleton
    @Provides
    fun provideContext(context: Application): Context {
        return context
    }


}