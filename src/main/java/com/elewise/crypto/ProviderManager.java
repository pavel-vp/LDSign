package com.elewise.crypto;

import com.elewise.crypto.capicom.CapicomProvider;

public class ProviderManager {

    private static final String PROVIDER_CAPICOM = "CAPICOM";

    private static IProvider provider;

    public static synchronized IProvider getProvider(String providerName) {
        if (PROVIDER_CAPICOM.equalsIgnoreCase(providerName)) {
            if (provider == null) {
                provider = new CapicomProvider();
            }
        }
        return provider;
    }


}
