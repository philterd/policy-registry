# Policy Registry

Policy Registry provides a centralized service for managing policies for use by Philter. Policies allow for configuring how sensitive information is removed or replaced in text.

Policies can be stored either in a local directory without versioning, or in a local directory using Git for versioning.

For more information please see https://www.philterd.ai.

## Building and Running

To build:

```
./mvnw clean install
```

To run:

```
docker build -t philterd/policy-registry .
docker run -p 8080:8080 philterd/policy-registry:latest
```

## Configuration

Policy Registry is configured via an `application.properties` file. Example configuration:

```
# Policy Registry
server.port=8080
policies.directory=./policies

# Use either "local" or "git" for store.
policies.store=local
```

# License

Licensed under the Apache License, version 2.0.

Copyright 2024 Philterd, LLC
