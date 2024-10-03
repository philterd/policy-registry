package com.mtnfog.philter.registry.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.util.Properties;

public final class Status {

    private final String version;
    private final String status;

    public Status(String status) throws IOException {

        this.status = status;

        final Properties properties = new Properties();
        properties.load(Status.this.getClass().getClassLoader().getResourceAsStream("internal.properties"));
        version = properties.getProperty("build.version");

    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37).
                append(status).
                toHashCode();

    }

    @Override
    public boolean equals(Object o) {

        return EqualsBuilder.reflectionEquals(this, o);

    }

    public String getStatus() {
        return status;
    }

    public String getVersion() {
        return version;
    }

}
