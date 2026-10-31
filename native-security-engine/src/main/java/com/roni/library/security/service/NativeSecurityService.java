package com.roni.library.security.service;

import com.roni.library.security.SecurityEngine;
import com.roni.library.security.biometric.BiometricAuthService;
import com.roni.library.security.keystore.KeyStoreManager;
import com.roni.library.security.permission.PermissionService;
import com.roni.library.service.BaseService;
import com.roni.library.service.ServiceRegistry;

/**
 * Layer 1 ecosystem integration service extending {@link BaseService}.
 * Registers the NativeSecurityEngine subsystem into the {@link ServiceRegistry}.
 *
 * @author infoadmstudio2020-cyber
 */
public class NativeSecurityService extends BaseService {

    public static final String SERVICE_NAME = "NativeSecurityService";

    private final SecurityEngine engine;

    public NativeSecurityService() {
        this(SecurityEngine.getInstance());
    }

    public NativeSecurityService(SecurityEngine engine) {
        super(SERVICE_NAME);
        this.engine = engine;
    }

    @Override
    protected void onInit() {
        if (engine != null && !engine.isInitialized()) {
            engine.initialize();
        }
        ServiceRegistry.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        ServiceRegistry.getInstance().unregister(getName());
        if (engine != null) {
            engine.release();
        }
    }

    public SecurityEngine getSecurityEngine() {
        return engine;
    }

    public PermissionService getPermissionService() {
        return engine.getPermissionService();
    }

    public BiometricAuthService getBiometricService() {
        return engine.getBiometricService();
    }

    public KeyStoreManager getKeyStoreManager() {
        return engine.getKeyStoreManager();
    }
}
