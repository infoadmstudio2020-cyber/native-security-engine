package com.roni.library.security.handler;

import com.roni.library.contracts.api.IApiCallback;
import com.roni.library.contracts.api.IApiHandler;
import com.roni.library.contracts.api.NativeApiError;
import com.roni.library.contracts.api.NativeApiRequest;
import com.roni.library.contracts.api.NativeApiResponse;
import com.roni.library.security.SecurityEngine;
import com.roni.library.security.biometric.AuthenticatorType;
import com.roni.library.security.biometric.BiometricCapability;
import com.roni.library.security.biometric.BiometricPromptConfig;
import com.roni.library.security.crypto.CryptoHash;
import com.roni.library.security.keystore.KeyAlgorithm;
import com.roni.library.security.keystore.KeyStoreManager;
import com.roni.library.security.permission.PermissionStatus;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;

/**
 * Universal RPC Command Handler implementing Layer 0 {@link IApiHandler}.
 * Dispatches commands under the '/security' route prefix.
 *
 * @author infoadmstudio2020-cyber
 */
public class SecurityCommandHandler implements IApiHandler {

    private static final String ROUTE_PREFIX = "/security";

    public String getRoutePrefix() {
        return ROUTE_PREFIX;
    }

    @Override
    public String getPath() {
        return ROUTE_PREFIX;
    }

    @Override
    public String getAction() {
        return "*";
    }

    @Override
    public void handle(NativeApiRequest request, IApiCallback callback) {
        if (callback == null) return;
        if (request == null) {
            callback.onError(new NativeApiError(400, "Request cannot be null"));
            return;
        }

        try {
            NativeApiResponse response = handleRequest(request);
            callback.onResponse(response);
        } catch (Exception e) {
            callback.onError(new NativeApiError(500, "Security command dispatch error: " + e.getMessage()));
        }
    }

    public NativeApiResponse handleRequest(NativeApiRequest request) {
        String action = request.getAction() != null ? request.getAction() : "";
        Map<String, Object> params = request.getParams() != null ? request.getParams() : new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        SecurityEngine engine = SecurityEngine.getInstance();
        KeyStoreManager ksm = engine.getKeyStoreManager();

        switch (action) {
            case "keystore.generate": {
                String alias = (String) params.getOrDefault("alias", "default_key");
                String algoStr = (String) params.getOrDefault("algorithm", "AES_GCM_256");
                boolean userAuth = Boolean.TRUE.equals(params.get("requiresUserAuth"));
                KeyAlgorithm algo = KeyAlgorithm.fromString(algoStr);
                boolean success = ksm.generateKey(alias, algo, userAuth);
                data.put("success", success);
                data.put("alias", alias);
                data.put("algorithm", algo.name());
                break;
            }

            case "keystore.hasKey": {
                String alias = (String) params.get("alias");
                data.put("hasKey", ksm.hasKey(alias));
                data.put("alias", alias);
                break;
            }

            case "keystore.delete": {
                String alias = (String) params.get("alias");
                data.put("deleted", ksm.deleteKey(alias));
                data.put("alias", alias);
                break;
            }

            case "keystore.list": {
                data.put("aliases", ksm.listAliases());
                break;
            }

            case "keystore.encrypt": {
                String alias = (String) params.getOrDefault("alias", "default_key");
                Object rawText = params.get("plaintext");
                byte[] plaintextBytes;
                if (rawText instanceof String) {
                    plaintextBytes = ((String) rawText).getBytes(StandardCharsets.UTF_8);
                } else if (rawText instanceof byte[]) {
                    plaintextBytes = (byte[]) rawText;
                } else {
                    plaintextBytes = String.valueOf(rawText).getBytes(StandardCharsets.UTF_8);
                }

                Map<String, Object> encResult = ksm.encrypt(alias, plaintextBytes);
                data.putAll(encResult);
                break;
            }

            case "keystore.decrypt": {
                String alias = (String) params.get("alias");
                String cipherB64 = (String) params.get("ciphertext");
                String ivB64 = (String) params.get("iv");

                if (cipherB64 != null && ivB64 != null) {
                    byte[] cBytes = Base64.getDecoder().decode(cipherB64);
                    byte[] ivBytes = Base64.getDecoder().decode(ivB64);
                    byte[] dec = ksm.decrypt(alias, cBytes, ivBytes);
                    data.put("plaintext", new String(dec, StandardCharsets.UTF_8));
                    data.put("success", true);
                } else {
                    data.put("success", false);
                    data.put("error", "Missing ciphertext or iv");
                }
                break;
            }

            case "biometric.capability": {
                int auth = params.containsKey("authenticators") ?
                        ((Number) params.get("authenticators")).intValue() :
                        AuthenticatorType.BIOMETRIC_STRONG;
                BiometricCapability cap = engine.getBiometricService().canAuthenticate(auth);
                data.put("code", cap.getCode());
                data.put("description", cap.getDescription());
                data.put("available", cap.isAvailable());
                break;
            }

            case "biometric.authenticate": {
                String title = (String) params.getOrDefault("title", "Biometric Authentication");
                BiometricPromptConfig config = BiometricPromptConfig.builder()
                        .setTitle(title)
                        .build();

                final Map<String, Object> authResult = new HashMap<>();
                engine.getBiometricService().authenticate(config, new com.roni.library.security.biometric.BiometricAuthCallback() {
                    @Override
                    public void onAuthenticationSucceeded(String authMethod, Cipher cipher) {
                        authResult.put("authenticated", true);
                        authResult.put("method", authMethod);
                    }

                    @Override
                    public void onAuthenticationFailed(String reason) {
                        authResult.put("authenticated", false);
                        authResult.put("reason", reason);
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, String errString) {
                        authResult.put("authenticated", false);
                        authResult.put("errorCode", errorCode);
                        authResult.put("errorMessage", errString);
                    }
                });
                data.putAll(authResult);
                break;
            }

            case "permission.check": {
                String perm = (String) params.get("permission");
                PermissionStatus status = engine.getPermissionService().checkPermission(perm);
                data.put("permission", perm);
                data.put("status", status.getValue());
                data.put("granted", status.isGranted());
                break;
            }

            case "hash.sha256": {
                String input = (String) params.getOrDefault("input", "");
                data.put("sha256", CryptoHash.sha256Hex(input));
                break;
            }

            case "diagnostic":
            default: {
                data.put("state", engine.getState().name());
                data.put("hsmAvailable", ksm.isHardwareSecurityModuleAvailable());
                data.put("biometricAvailable", engine.getBiometricService().canAuthenticate(AuthenticatorType.BIOMETRIC_STRONG).isAvailable());
                data.put("registeredKeysCount", ksm.listAliases().size());
                data.put("routePrefix", ROUTE_PREFIX);
                break;
            }
        }

        return new NativeApiResponse(request.getRequestId(), 200, "OK", data, System.currentTimeMillis());
    }
}
