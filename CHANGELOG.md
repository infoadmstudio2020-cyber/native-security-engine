# Changelog

All notable changes to NativeSecurityEngine are documented here.
Format based on [Keep a Changelog](https://keepachangelog.com).

## [1.0.0] — 2026-10-31

### Added
- Complete Layer 2 Domain Engine implementation for cryptography, biometrics, and security.
- `SecurityEngine` coordinator with thread-safe singleton state machine and `IDisposable` lifecycle.
- `KeyStoreManager` supporting hardware key generation, alias cataloging, and Enigma AES-256-GCM authenticated encryption/decryption with software fallback in JVM environments.
- `KeyAlgorithm` enum supporting `AES_GCM_256`, `AES_CBC_256`, `RSA_2048`, `RSA_4096`, and `HMAC_SHA256`.
- `BiometricAuthService` with `canAuthenticate()`, prompt delegation, and lifecycle callbacks (`BiometricAuthCallback`).
- `BiometricCapability` and `AuthenticatorType` supporting strong biometrics, weak biometrics, and device credentials.
- `PermissionService` managing Android runtime permission status, requests, rationale, and listener notifications.
- `CryptoHash` providing SHA-256, SHA-512, HMAC-SHA256, secure random salts, and constant-time equality checks.
- `SecurityCommandHandler` implementing Layer 0 `IApiHandler` for `/security` command dispatching.
- `NativeSecurityService` integrating `SecurityEngine` with Layer 1 `ServiceRegistry` (extending `BaseService`).
- Companion demonstration app in `app` module with live UI controls and visual cryptographic feedback.
- Comprehensive automated JVM unit test suites (`SecurityEngineTest`, `KeyStoreManagerTest`, `BiometricAuthServiceTest`, `PermissionServiceTest`, `CryptoHashTest`, `SecurityCommandHandlerTest`).
- Updated GitHub Actions CI/CD workflows with write permissions and `softprops/action-gh-release@v2`.
- JitPack publication configuration.

