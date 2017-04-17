package net.vpc.app.vainruling.core.restclient;

/**
 * Created by vpc on 2/26/17.
 */
public interface TypeParser {
    Object parse(String value,Class toType);
}
