# NativeSecurityEngine API Reference

## NativeSecurityEngine

Main entry point for the library.

### Methods

#### `static getInstance()`
Returns the singleton instance.

#### `init(Context context, NativeSecurityEngineConfig config)`
Initializes the library. Must be called before any other method.

**Parameters:**
- `context` — application context
- `config` — configuration object

#### `isInitialized()`
Returns `true` if the library has been initialized.

#### `getConfig()`
Returns the current `NativeSecurityEngineConfig` or null.

#### `release()`
Releases all resources. After calling this, `getInstance()` returns a fresh instance.

---

## NativeSecurityEngineConfig.Builder

| Method | Default | Description |
|--------|---------|-------------|
| `debugEnabled(boolean)` | `false` | Enable debug logging |
| `tag(String)` | `"NativeSecurityEngine"` | Log tag |
| `timeout(int)` | `30000` | Timeout in milliseconds |
| `build()` | — | Create the config |

---

## NativeSecurityEngineResult<T>

| Method | Description |
|--------|-------------|
| `isSuccess()` | True if operation succeeded |
| `getData()` | Result data or null |
| `getErrorMessage()` | Error message or null |
| `getErrorCode()` | Numeric error code |
