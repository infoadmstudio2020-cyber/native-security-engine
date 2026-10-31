package com.roni.library.security;

import com.roni.library.contracts.common.IDisposable;
import com.roni.library.contracts.common.RuntimeState;
import com.roni.library.security.biometric.BiometricAuthService;
import com.roni.library.security.handler.SecurityCommandHandler;
import com.roni.library.security.keystore.KeyStoreManager;
import com.roni.library.security.permission.PermissionService;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main coordinator and thread-safe singleton state machine for NativeSecurityEngine.
 * Implements {@link IDisposable} lifecycle bounds.
 *
 * @author infoadmstudio2020-cyber
 */
public class SecurityEngine implements IDisposable {

    private static volatile SecurityEngine sInstance;

    private volatile RuntimeState state = RuntimeState.UNINITIALIZED;
    private final AtomicBoolean disposed = new AtomicBoolean(false);
    private SecurityEngineConfig config;

    private final KeyStoreManager keyStoreManager;
    private final BiometricAuthService biometricService;
    private final PermissionService permissionService;
    private final SecurityCommandHandler commandHandler;

    public SecurityEngine() {
        this.keyStoreManager = new KeyStoreManager();
        this.biometricService = new BiometricAuthService();
        this.permissionService = new PermissionService();
        this.commandHandler = new SecurityCommandHandler();
    }

    public static SecurityEngine getInstance() {
        if (sInstance == null) {
            synchronized (SecurityEngine.class) {
                if (sInstance == null) {
                    sInstance = new SecurityEngine();
                }
            }
        }
        return sInstance;
    }

    public synchronized void init(Object context, SecurityEngineConfig config) {
        if (state == RuntimeState.READY) {
            return;
        }
        state = RuntimeState.INITIALIZING;
        this.config = config != null ? config : new SecurityEngineConfig.Builder().build();
        state = RuntimeState.READY;
    }

    public synchronized void initialize() {
        init(null, new SecurityEngineConfig.Builder().build());
    }

    public boolean isInitialized() {
        return state == RuntimeState.READY;
    }

    public RuntimeState getState() {
        return state;
    }

    public SecurityEngineConfig getConfig() {
        return config;
    }

    public void setConfig(SecurityEngineConfig config) {
        this.config = config;
    }

    public KeyStoreManager getKeyStoreManager() {
        return keyStoreManager;
    }

    public BiometricAuthService getBiometricService() {
        return biometricService;
    }

    public PermissionService getPermissionService() {
        return permissionService;
    }

    public SecurityCommandHandler getCommandHandler() {
        return commandHandler;
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }

    @Override
    public synchronized void release() {
        if (disposed.compareAndSet(false, true)) {
            state = RuntimeState.TERMINATED;
            if (biometricService != null) {
                biometricService.clear();
            }
            if (permissionService != null) {
                permissionService.clear();
            }
        }
    }
}
