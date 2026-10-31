package com.roni.library.security.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.roni.library.contracts.api.IApiCallback;
import com.roni.library.contracts.api.NativeApiError;
import com.roni.library.contracts.api.NativeApiRequest;
import com.roni.library.contracts.api.NativeApiResponse;
import com.roni.library.security.SecurityEngine;
import com.roni.library.security.SecurityEngineConfig;
import com.roni.library.security.biometric.AuthenticatorType;
import com.roni.library.security.biometric.BiometricAuthCallback;
import com.roni.library.security.biometric.BiometricCapability;
import com.roni.library.security.biometric.BiometricPromptConfig;
import com.roni.library.security.crypto.CryptoHash;
import com.roni.library.security.keystore.KeyAlgorithm;
import com.roni.library.security.permission.PermissionStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Cipher;

/**
 * Interactive Companion Demonstration Activity for NativeSecurityEngine.
 * Provides controls for Hardware KeyStore management, AES-GCM encryption,
 * Biometric authentication, Cryptographic hashing, and RPC dispatching.
 *
 * @author infoadmstudio2020-cyber
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private TextView tvConsole;
    private ScrollView scrollConsole;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());

    private SecurityEngine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupSecurityEngine();
        setupListeners();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvConsole = findViewById(R.id.tv_console_output);
        scrollConsole = findViewById(R.id.scroll_console);
    }

    private void setupSecurityEngine() {
        appendLog("[INIT] Initializing NativeSecurityEngine subsystem...");

        SecurityEngineConfig config = new SecurityEngineConfig.Builder()
                .debugEnabled(true)
                .tag("SecurityDemo")
                .requireHardwareBackedKeys(false)
                .biometricFallbackEnabled(true)
                .build();

        engine = SecurityEngine.getInstance();
        engine.init(this, config);

        updateStatusBanner();
        appendLog("[READY] SecurityEngine initialized with state: " + engine.getState());
    }

    private void updateStatusBanner() {
        if (engine != null) {
            boolean hsm = engine.getKeyStoreManager().isHardwareSecurityModuleAvailable();
            BiometricCapability bioCap = engine.getBiometricService().canAuthenticate(AuthenticatorType.BIOMETRIC_STRONG);
            int keysCount = engine.getKeyStoreManager().listAliases().size();

            String status = "State: " + engine.getState() +
                    " | HSM: " + (hsm ? "Available" : "Emulated") +
                    " | Biometric: " + bioCap.name() +
                    " | Keys: " + keysCount;
            tvStatus.setText(status);
        }
    }

    private void setupListeners() {
        Button btnGenKey = findViewById(R.id.btn_gen_key);
        Button btnEncryptDecrypt = findViewById(R.id.btn_encrypt_decrypt);
        Button btnListKeys = findViewById(R.id.btn_list_keys);
        Button btnBioCheck = findViewById(R.id.btn_biometric_check);
        Button btnBioAuth = findViewById(R.id.btn_biometric_auth);
        Button btnHash = findViewById(R.id.btn_hash);
        Button btnHmac = findViewById(R.id.btn_hmac);
        Button btnPermissions = findViewById(R.id.btn_permissions);
        Button btnRpc = findViewById(R.id.btn_rpc);
        Button btnClear = findViewById(R.id.btn_clear);

        btnGenKey.setOnClickListener(v -> generateAesKey());
        btnEncryptDecrypt.setOnClickListener(v -> testEncryptDecrypt());
        btnListKeys.setOnClickListener(v -> listKeyStoreAliases());
        btnBioCheck.setOnClickListener(v -> checkBiometrics());
        btnBioAuth.setOnClickListener(v -> authenticateBiometrics());
        btnHash.setOnClickListener(v -> testHash());
        btnHmac.setOnClickListener(v -> testHmac());
        btnPermissions.setOnClickListener(v -> testPermissions());
        btnRpc.setOnClickListener(v -> testRpcCommand());
        btnClear.setOnClickListener(v -> tvConsole.setText("Console cleared.\n"));
    }

    private void generateAesKey() {
        String alias = "demo_key_" + System.currentTimeMillis() % 1000;
        boolean success = engine.getKeyStoreManager().generateKey(alias, KeyAlgorithm.AES_GCM_256, false);
        appendLog("[KEYSTORE] Generated AES-256-GCM key alias='" + alias + "' -> " + (success ? "SUCCESS" : "FAILED"));
        updateStatusBanner();
    }

    private void testEncryptDecrypt() {
        String alias = "vault_master_key";
        if (!engine.getKeyStoreManager().hasKey(alias)) {
            engine.getKeyStoreManager().generateKey(alias, KeyAlgorithm.AES_GCM_256, false);
        }

        String secretData = "CONFIDENTIAL_PAYLOAD_" + UUID.randomUUID();
        appendLog("[CRYPTO] Plaintext: " + secretData);

        String encryptedPayload = engine.getKeyStoreManager().encryptString(alias, secretData);
        appendLog("[CRYPTO] Ciphertext: " + encryptedPayload);

        String decryptedData = engine.getKeyStoreManager().decryptString(alias, encryptedPayload);
        appendLog("[CRYPTO] Decrypted:  " + decryptedData);
        appendLog("[CRYPTO] Integrity match: " + secretData.equals(decryptedData));
    }

    private void listKeyStoreAliases() {
        List<String> aliases = engine.getKeyStoreManager().listAliases();
        appendLog("[KEYSTORE ALIASES] Total=" + aliases.size() + " -> " + aliases);
    }

    private void checkBiometrics() {
        BiometricCapability strong = engine.getBiometricService().canAuthenticate(AuthenticatorType.BIOMETRIC_STRONG);
        BiometricCapability cred = engine.getBiometricService().canAuthenticate(AuthenticatorType.DEVICE_CREDENTIAL);
        appendLog("[BIOMETRIC CAPABILITY]\n" +
                "  BIOMETRIC_STRONG: " + strong.name() + " (" + strong.getDescription() + ")\n" +
                "  DEVICE_CREDENTIAL: " + cred.name() + " (" + cred.getDescription() + ")");
    }

    private void authenticateBiometrics() {
        BiometricPromptConfig config = BiometricPromptConfig.builder()
                .setTitle("Verify Identity")
                .setSubtitle("Confirm action via biometrics")
                .build();

        appendLog("[BIOMETRIC] Initiating authentication prompt...");
        engine.getBiometricService().authenticate(config, new BiometricAuthCallback() {
            @Override
            public void onAuthenticationSucceeded(String authMethod, Cipher cipher) {
                mainHandler.post(() -> appendLog("[BIOMETRIC SUCCESS] Authenticated via: " + authMethod));
            }

            @Override
            public void onAuthenticationFailed(String reason) {
                mainHandler.post(() -> appendLog("[BIOMETRIC FAILED] Reason: " + reason));
            }

            @Override
            public void onAuthenticationError(int errorCode, String errString) {
                mainHandler.post(() -> appendLog("[BIOMETRIC ERROR] Code=" + errorCode + ", Msg=" + errString));
            }
        });
    }

    private void testHash() {
        String testInput = "NativeRuntime Ecosystem Security Standard";
        String sha256 = CryptoHash.sha256Hex(testInput);
        String sha512 = CryptoHash.sha512Hex(testInput);
        appendLog("[HASHING] Input: \"" + testInput + "\"\n" +
                "  SHA-256: " + sha256 + "\n" +
                "  SHA-512: " + sha512.substring(0, 32) + "...");
    }

    private void testHmac() {
        String key = "secure_signing_secret_99";
        String payload = "transaction_id=89234&amount=500";
        String signature = CryptoHash.hmacSha256Base64(payload, key);
        appendLog("[HMAC-SHA256] Signature=" + signature);
    }

    private void testPermissions() {
        String perm = "android.permission.USE_BIOMETRIC";
        PermissionStatus status = engine.getPermissionService().checkPermission(perm);
        appendLog("[PERMISSION CHECK] " + perm + " -> " + status.getValue());
    }

    private void testRpcCommand() {
        String reqId = "sec_rpc_" + UUID.randomUUID().toString().substring(0, 8);
        Map<String, Object> params = new HashMap<>();
        params.put("input", "TestRPCInputPayload");

        NativeApiRequest request = new NativeApiRequest(reqId, "/security", "hash.sha256", params, System.currentTimeMillis());
        appendLog("--> RPC DISPATCH: [/security] Action=hash.sha256");

        engine.getCommandHandler().handle(request, new IApiCallback() {
            @Override
            public void onResponse(NativeApiResponse response) {
                mainHandler.post(() -> appendLog("<-- RPC SUCCESS: [" + response.getRequestId() + "] Data=" + response.getData()));
            }

            @Override
            public void onError(NativeApiError error) {
                mainHandler.post(() -> appendLog("<-- RPC ERROR: [" + reqId + "] Msg=" + error.getMessage()));
            }
        });
    }

    private void appendLog(String message) {
        String timestamp = dateFormat.format(new Date());
        String entry = "[" + timestamp + "] " + message + "\n";
        tvConsole.append(entry);
        scrollConsole.post(() -> scrollConsole.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (engine != null) {
            engine.release();
        }
    }
}
