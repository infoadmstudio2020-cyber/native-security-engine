# NativeSecurityEngine

Hardware-backed KeyStore cryptography, AES-GCM encryption, Biometrics, and Permissions engine for Android.

[![JitPack](https://jitpack.io/v/infoadmstudio2020-cyber/native-security-engine.svg)](https://jitpack.io/#infoadmstudio2020-cyber/native-security-engine)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-Apache-2.0-blue.svg)](LICENSE)

## Architecture Overview

`NativeSecurityEngine` operates at **Layer 2: Domain Engine Subsystems (Core Domain Engines)** within the NativeRuntime ecosystem.
It provides:
- **Hardware KeyStore Manager (`KeyStoreManager`)**: Hardware Security Module (HSM/TEE/StrongBox) key generation and key lifecycle, supporting AES-256-GCM, AES-CBC-256, RSA-2048, and HMAC-SHA256 with seamless software fallback in headless test environments.
- **Enigma AES-256-GCM Cryptography**: Authenticated symmetric encryption and decryption with initialization vectors (IV) and authentication tags.
- **Biometric Authentication Service (`BiometricAuthService`)**: Biometric capability checks (`canAuthenticate()`), prompt delegation, and observer listener callbacks for fingerprint, face, and device credentials.
- **Android Runtime Permission Service (`PermissionService`)**: Permission status tracking, single/multiple permission requests, and rationale evaluation.
- **Cryptographic Hashing & Signing (`CryptoHash`)**: Deterministic SHA-256/512 hashing, HMAC message signing, cryptographically secure random salts, and constant-time equality comparisons.
- **Universal RPC Command Handler (`SecurityCommandHandler`)**: Full implementation of Layer 0 `IApiHandler` responding to `/security` commands.
- **Ecosystem Service Registration (`NativeSecurityService`)**: Automatic registration with Layer 1 `ServiceRegistry` (extending `BaseService`).

## Installation

### Step 1 — Add JitPack repository

In your root `settings.gradle`:

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2 — Add dependency

In your module's `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.infoadmstudio2020-cyber:native-security-engine:1.0.1'

    // Ecosystem dependencies
    api 'com.github.infoadmstudio2020-cyber:NativeRuntimeContracts:v1.0.1'
    implementation 'com.github.infoadmstudio2020-cyber:NativeServiceSDK:1.0.1'
    testImplementation 'com.github.infoadmstudio2020-cyber:NativeLoggerSDK:1.0.1'
}
```

## Quick Start

### 1. Initialize Engine

```java
SecurityEngineConfig config = new SecurityEngineConfig.Builder()
    .debugEnabled(BuildConfig.DEBUG)
    .tag("SecurityEngine")
    .requireHardwareBackedKeys(false)
    .biometricFallbackEnabled(true)
    .build();

SecurityEngine.getInstance().init(context, config);
```

### 2. Encrypt & Decrypt Data with AES-256-GCM

```java
KeyStoreManager ksm = SecurityEngine.getInstance().getKeyStoreManager();

// Generate key in hardware keystore
ksm.generateKey("master_vault_key", KeyAlgorithm.AES_GCM_256, false);

// Encrypt plaintext string
String encrypted = ksm.encryptString("master_vault_key", "SuperSecretData123");

// Decrypt back to original plaintext
String decrypted = ksm.decryptString("master_vault_key", encrypted);
```

### 3. Check Biometrics & Authenticate

```java
BiometricAuthService bio = SecurityEngine.getInstance().getBiometricService();

BiometricCapability capability = bio.canAuthenticate(AuthenticatorType.BIOMETRIC_STRONG);
if (capability.isAvailable()) {
    BiometricPromptConfig prompt = BiometricPromptConfig.builder()
        .setTitle("Unlock Vault")
        .build();

    bio.authenticate(prompt, new BiometricAuthCallback() {
        @Override
        public void onAuthenticationSucceeded(String authMethod, Cipher cipher) {
            // Identity confirmed
        }

        @Override
        public void onAuthenticationFailed(String reason) {
            // Authentication rejected
        }

        @Override
        public void onAuthenticationError(int errorCode, String errString) {
            // Error handling
        }
    });
}
```

## Requirements

- Android minSdk 21
- compileSdk 34+
- Java 17

## License

Apache License 2.0. See [LICENSE](LICENSE) for details.

