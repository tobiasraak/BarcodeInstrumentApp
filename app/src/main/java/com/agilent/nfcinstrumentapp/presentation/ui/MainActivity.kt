package com.agilent.nfcinstrumentapp.presentation.ui

import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.*
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.agilent.nfcinstrumentapp.presentation.ui.theme.AppTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter?= null
    private lateinit var nfcMessage: String
    private val tag = MainActivity::class.java.simpleName
    private lateinit var nfcIntentFilter: Array<IntentFilter>
    private lateinit var pendingIntent: PendingIntent
    private lateinit var techList: Array<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        } else Toast.makeText(this, "No NFC Module found", Toast.LENGTH_SHORT).show()

        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val ndefDetected = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("*/*")
            } catch (ex: Exception) {
                Log.e("FAIL", ex.message!!)
            }
        }
        val techDetected = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        nfcIntentFilter = arrayOf(techDetected, tagDetected, ndefDetected)
        pendingIntent =
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent(this@MainActivity, javaClass)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE
                )
            }else PendingIntent.getActivity(
                this,
                0,
                Intent(applicationContext, applicationContext.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT)

        techList = arrayOf(
            arrayOf(
                NfcV::class.java.name,
                NfcF::class.java.name,
                NfcA::class.java.name,
                NfcB::class.java.name
            )
        )

        if (intent.action!! == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            handleNfcIntent(intent)
        }

        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Open qr-code scanner")
                }
            }
        }


    }

    private fun handleNfcIntent(NfcIntent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == NfcIntent.action) {
            val receivedArray = NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (receivedArray != null) {
                val receivedMessage = receivedArray[0] as NdefMessage
                val attachedRecords = receivedMessage.records
                for (record in attachedRecords) {
                    val string = String(record.payload)
                    if (string == packageName) {
                        continue
                    }
                    nfcMessage = string
                }
                Toast.makeText(
                    this, nfcMessage, Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this, "Received Blank Parcel", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(nfcAdapter!= null) {
            nfcAdapter!!.disableForegroundDispatch(this@MainActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        if(nfcAdapter !=null) {
            nfcAdapter!!.enableForegroundDispatch(this@MainActivity, pendingIntent, nfcIntentFilter, techList)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val tag = intent?.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG)
        if (intent != null) {
            Log.d(this.tag, "onNewIntent: " + intent.action)
        }
        if (tag != null) {
            Toast.makeText(this, "Tag detected", Toast.LENGTH_SHORT).show()
            val ndef = Ndef.get(tag as Tag?)
        }
    }
}

@Composable
fun Greeting(name: String) {
    val scannedText = remember {
        mutableStateOf("")   
    }
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            Log.i(TAG, "scanned code: ${result.contents}")
            scannedText.value = result.contents
        }
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = scannedText.value)
        Button(onClick = {
            val scanOptions = ScanOptions()
                .setBeepEnabled(false)
                .setOrientationLocked(false)
            scanLauncher.launch(scanOptions)
        }) {
            Text(text = "$name!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        Greeting("Android")
    }
}