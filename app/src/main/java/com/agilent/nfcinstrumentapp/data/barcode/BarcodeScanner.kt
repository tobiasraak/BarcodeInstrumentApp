package com.agilent.nfcinstrumentapp.data.barcode

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import com.agilent.nfcinstrumentapp.domain.barcode.BarcodeScanner
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import javax.inject.Inject

class BarcodeScanner @Inject constructor(
) : BarcodeScanner{

    @Composable
    override fun scanBarcode(): String? {
        var result = ""
        val scanLauncher = rememberLauncherForActivityResult(
            contract = ScanContract(),
            onResult = {
                result = it.contents
            }
        )
        val scanOptions = ScanOptions()
            .setBeepEnabled(false)
            .setOrientationLocked(false)
        scanLauncher.launch(scanOptions)

        if(result != ""){
            return result
        }
        return null
    }
}