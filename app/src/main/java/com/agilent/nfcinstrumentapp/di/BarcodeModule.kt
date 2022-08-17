package com.agilent.nfcinstrumentapp.di

import com.agilent.nfcinstrumentapp.data.barcode.BarcodeScanner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import javax.inject.Singleton

@Module
@InstallIn(Singleton::class)
abstract class BarcodeModule {

    @Binds
    @Singleton
    abstract fun bindBarcodeScanner(barcodeScanner: BarcodeScanner): com.agilent.nfcinstrumentapp.domain.barcode.BarcodeScanner
}