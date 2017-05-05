package de.invesdwin.context.r.runtime.rserve.pool.internal;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.math.R.Rsession;

import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;
import de.invesdwin.util.lang.Reflections;

@ThreadSafe
public final class RsessionLogger implements org.math.R.Logger {

    @GuardedBy("this")
    private final StringBuilder errorMessage = new StringBuilder();

    public RsessionLogger() {}

    @Override
    public synchronized void println(final String text, final Level level) {
        switch (level) {
        case OUTPUT:
            IScriptTaskRunner.LOG.debug(text);
            errorMessage.setLength(0);
        case INFO:
            IScriptTaskRunner.LOG.trace(text);
            errorMessage.setLength(0);
            break;
        case WARNING:
            IScriptTaskRunner.LOG.warn(text);
            errorMessage.append(text);
            errorMessage.append("\n");
            break;
        case ERROR:
            IScriptTaskRunner.LOG.error(text);
            errorMessage.append(text);
            errorMessage.append("\n");
            break;
        default:
            IScriptTaskRunner.LOG.trace(text);
            errorMessage.setLength(0);
            break;
        }
    }

    public synchronized String getErrorMessage() {
        return String.valueOf(errorMessage).trim();
    }

    @Override
    public synchronized void close() {
        errorMessage.setLength(0);
    }

    public static RsessionLogger get(final Rsession rsession) {
        return (RsessionLogger) Reflections.field("console").ofType(org.math.R.Logger.class).in(rsession).get();
    }

}