package com.samapps.samtapsample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.samapps.emvnfc.api.EmvCardResult
import com.samapps.emvnfc.api.SamNfcSdk
import com.samapps.emvnfc.api.TagReadResult
import com.samapps.samtapsample.ui.theme.SamTapSampleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SamTapSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SamTapDemo(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SamTapDemo(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Initialize the SDK. In a real app, you might want to do this in a DI module or ViewModel.
    // For this sample, we re-create it or remember it.
    // Assuming Logger.NONE is the default as per the SDK code provided.
    val samSdk = remember { SamNfcSdk(context) }

    var statusText by remember { mutableStateOf("Ready to scan.") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SamTap SDK Sample",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                scope.launch {
                    statusText = "Waiting for EMV card..."
                    try {
                        // Pass the activity if required by the SDK method signature provided:
                        // suspend fun readEmvOnce(activity: Activity, timeoutMs: Long = 5000): EmvCardResult
                        // We need to cast context to Activity.
                        val activity = context as? android.app.Activity
                        if (activity != null) {
                            val result = samSdk.readEmvOnce(activity)
                            statusText = when (result) {
                                is EmvCardResult.Success -> {
                                    val card = result.data
                                    """
                                    Success!
                                    PAN: ${card.pan}
                                    Expiry: ${card.expiry}
                                    Scheme: ${card.scheme}
                                    """.trimIndent()
                                }
                                is EmvCardResult.Failure -> "Failed: ${result.error}"
                            }
                        } else {
                            statusText = "Error: Context is not an Activity"
                        }
                    } catch (e: Exception) {
                        statusText = "Exception: ${e.message}"
                        Log.e("SamTapSample", "Error reading EMV", e)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Read EMV Card")
        }

        Button(
            onClick = {
                scope.launch {
                    statusText = "Waiting for a Tag..."
                    try {
                        val activity = context as? android.app.Activity
                        if (activity != null) {
                            val result = samSdk.readSingleTag(activity)
                            statusText = when (result) {
                                is TagReadResult.Success -> "Tag Read: ${result.data}"
                                is TagReadResult.Failure -> "Tag Read Failed: ${result.error}"
                            }
                        } else {
                            statusText = "Error: Context is not an Activity"
                        }
                    } catch (e: Exception) {
                        statusText = "Exception: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Read Any Tag")
        }

        HorizontalDivider()

        Text(
            text = "Status / Result:",
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}