# SamTap SDK Sample App

This repository contains a sample Android application demonstrating the usage of the **SamTap SDK**.

## What is SamTap SDK?

**SamTap SDK** is a powerful, developer-friendly Android library designed to simplify interactions with NFC (Near Field Communication) technology. It bridges the gap between complex low-level Android NFC APIs and high-level business logic, enabling developers to build robust "Tap to X" experiences with minimal effort.

### Key Capabilities

*   **EMV Card Reading (SoftPOS)**: extract public data from contactless EMV credit and debit cards (Visa, Mastercard, etc.) including PAN (Primary Account Number), Expiry Date, and Application Labels. *Note: PCI-CPoC compliance logic must be handled by your backend.*
*   **Universal Tag Support**: Read and write to a wide variety of standard NFC tags, including:
    *   **ISO 14443-4 (IsoDep)** for passports and identity cards.
    *   **Mifare Family** (Classic, Ultralight, DESFire) for transit and access control.
    *   **NFC-V (ISO 15693)** for inventory and industrial tags.
    *   **FeliCa** for Japanese transit and payment systems.
*   **Coroutines-First API**: All operations are suspended functions, ensuring your UI remains buttery smooth while blocking I/O operations happen on background threads.
*   **Smart Polling**: Automatically handles reader mode flags, timeouts, and technology filtering so you don't have to manage specific `NfcAdapter` flags manually.

### Common Use Cases

1.  **Mobile Point of Sale (SoftPOS)**: Turn any Android device into a payment terminal for reading cards.
2.  **Loyalty & Rewards**: Allow customers to tap their membership cards or phones to earn points.
3.  **Access Control**: Read employee badges or secure entry cards.
4.  **Identity Verification**: Scan ePassports or NFC-enabled national ID cards.
5.  **Inventory Management**: Quickly scan items tagged with NFC stickers in a warehouse.

## Getting Started

### Prerequisites

- Android SDK API Level 24+ (Android 7.0 Nougat) or higher.
- A valid API Key (See Signup section below).
- An NFC-enabled Android device (Emulators do not support NFC).

### Installation

The SDK is available via Maven.

1.  **Add the repository**:
    Ensure `mavenCentral()` (or your private registry URL) is defined in your `settings.gradle.kts` or root `build.gradle.kts`.

2.  **Add the dependency**:
    In your module-level `build.gradle.kts` (usually `app/build.gradle.kts`), add:

    ```kotlin
    dependencies {
        implementation("com.samapps.emvnfc:samtap-sdk:1.0.0") // Replace with latest version
    }
    ```

3.  **Sync Gradle**: Click "Sync Now" in Android Studio.

## Usage

### 1. Initialization

Initialize the SDK, typically in your Activity, ViewModel, or via Dependency Injection.

```kotlin
val sdk = SamNfcSdk(context)
```

### 2. Reading an EMV Payment Card

Use `readEmvOnce` to scan for a financial card. This automatically configures the NFC reader for payment technologies.

```kotlin
scope.launch {
    // 'activity' is required to enable Reader Mode
    val result = sdk.readEmvOnce(activity)
    
    when (result) {
        is EmvCardResult.Success -> {
            val card = result.data
            println("Card Number: ${card.pan}")
            println("Expires: ${card.expiry}")
            println("Scheme: ${card.scheme}") // e.g. Visa, Mastercard
        }
        is EmvCardResult.Failure -> {
            // Handle specific errors like Timeout, TagLost, or ParseFailure
            println("Reading failed: ${result.error}")
        }
    }
}
```

### 3. Reading Generic NFC Tags

For non-payment tags (like scanning a product tag or access badge), use `readSingleTag`.

```kotlin
scope.launch {
    // Reads any supported tag type
    val result = sdk.readSingleTag(activity)
    
    when (result) {
        is TagReadResult.Success -> {
            when (val data = result.data) {
                is TagData.Ndef -> println("NDEF Records: ${data.records}")
                is TagData.MifareUltralightInfo -> println("Mifare Data: ${data.type}")
                is TagData.IsoDepInfo -> println("ISO-DEP Response: ${data.hiLayerResponse}")
                else -> println("Tag ID: ${data.tagIdHex}")
            }
        }
        is TagReadResult.Failure -> println("Tag read failed: ${result.error}")
    }
}
```

## API Key & Licensing

To use the full capabilities of the SamTap SDK in a production environment, you must obtain an API Key.

👉 **[Sign up for your Developer API Key](https://samtap-587af.web.app)**

Without a valid key, the SDK operates in a limited trial mode (limited scans per day or obfuscated data).

## Support

For documentation, FAQs, and support, visit [here](https://samtap-587af.web.app/docs.html).

## License

Copyright 2024 SamApps. All rights reserved.
