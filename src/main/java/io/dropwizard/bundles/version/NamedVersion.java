package io.dropwizard.bundles.version;

public class NamedVersion {

    public final String version;
    public final String name;

    public NamedVersion(String version, String name) {
        this.version = version;
        this.name = name;
    }
}