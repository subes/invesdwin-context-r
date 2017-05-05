package de.invesdwin.context.r.runtime.rserve;

import javax.annotation.concurrent.Immutable;

import org.math.R.RserverConf;

import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.util.lang.Strings;

@Immutable
public final class RserveProperties {

    public static final RserverConf RSERVER_CONF;
    public static final RserverConfMode RSERVER_CONF_MODE;

    static {
        final SystemProperties systemProperties = new SystemProperties(RserveProperties.class);
        RSERVER_CONF_MODE = systemProperties.getEnum(RserverConfMode.class, "RSERVER_CONF_MODE");
        if (systemProperties.containsValue("RSERVER_CONF")) {
            final String rserverConf = systemProperties.getString("RSERVER_CONF");
            RSERVER_CONF = RserverConf.parse(rserverConf);
            if (RSERVER_CONF_MODE == RserverConfMode.LOCAL) {
                //override port for local instance
                final RserverConf newLocalInstance = RserverConf.newLocalInstance(null);
                if (RserveProperties.RSERVER_CONF != null) {
                    RSERVER_CONF.port = newLocalInstance.port;
                }
                systemProperties.setString("RSERVER_CONF", Strings.asString(RSERVER_CONF));
            }
        } else {
            if (RSERVER_CONF_MODE == RserverConfMode.LOCAL) {
                RSERVER_CONF = RserverConf.newLocalInstance(null);
            } else {
                RSERVER_CONF = null;
            }
        }
        if (RSERVER_CONF != null) {
            systemProperties.maybeLogSecurityWarning("RSERVER_CONF (username)", RSERVER_CONF.login, "invesdwin");
            systemProperties.maybeLogSecurityWarning("RSERVER_CONF (password)", RSERVER_CONF.password, "invesdwin");
        }
    }

    private RserveProperties() {}

}
