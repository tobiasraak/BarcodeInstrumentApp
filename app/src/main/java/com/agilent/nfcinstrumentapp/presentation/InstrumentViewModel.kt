package com.agilent.nfcinstrumentapp.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.agilent.nfcinstrumentapp.data.BarcodeScannerResult
import com.agilent.nfcinstrumentapp.domain.barcode.BarcodeScanner
import com.agilent.nfcinstrumentapp.domain.repository.InstrumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InstrumentViewModel @Inject constructor(
    private val instrumentRepository: InstrumentRepository,
    private val barcodeScanner: BarcodeScanner
): ViewModel() {

    var scanResult by mutableStateOf(BarcodeScannerResult())
        private set


}