package adapter.in.services;

import jakarta.ws.rs.core.CacheControl;

public class CacheExpirationFactory {
    public static CacheControl get10sPrivateNoMustValidateExpiration() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(10);
        cacheControl.setMustRevalidate(false);
        cacheControl.setPrivate(true);
        return cacheControl;
    }
}
