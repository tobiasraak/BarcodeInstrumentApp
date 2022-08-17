package com.agilent.nfcinstrumentapp.domain.barcode

import androidx.compose.runtime.Composable

interface BarcodeScanner {
    @Composable
    fun scanBarcode(): String?
}