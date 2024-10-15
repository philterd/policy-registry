FROM openjdk:17-jdk-alpine

RUN mkdir -p /opt/policy-registry
RUN mkdir -p /opt/policy-registry/ssl
RUN mkdir -p /opt/policy-registry/policies

COPY LICENSE.txt /opt/policy-registry/LICENSE.txt
COPY README.md /opt/policy-registry/README.md
COPY distribution/policies/*.json /opt/policy-registry/
COPY distribution/application.properties /opt/policy-registry/application.properties
COPY ./policy-registry-app/target/policy-registry.jar /opt/policy-registry/policy-registry.jar

RUN chmod +x /opt/policy-registry/policy-registry.jar

RUN keytool -genkeypair -keypass Password123! -dname "CN=$USER, O=$USER, C=US" -alias policy-registry -keyalg RSA -keysize 4096 -storepass Password123! -storetype PKCS12 -keystore /opt/policy-registry/ssl/policy-registry.p12 -validity 3650 \
    && echo "\n" | tee -a /opt/policy-registry/application.properties \
    && echo "# SSL certificate settings" | tee -a /opt/policy-registry/application.properties \
    && echo "server.ssl.key-store-type=PKCS12" | tee -a /opt/policy-registry/application.properties \
    && echo "server.ssl.key-store=/opt/policy-registry/ssl/policy-registry.p12" | tee -a /opt/policy-registry/application.properties \
    && echo "server.ssl.key-store-password=Password123!" | tee -a /opt/policy-registry/application.properties \
    && echo "server.ssl.key-alias=policy-registry" | tee -a /opt/policy-registry/application.properties \
    && echo "security.require-ssl=true" | tee -a /opt/policy-registry/application.properties

WORKDIR /opt/policy-registry
#CMD java -jar philter-profile-registry.jar && tail -F /var/log/philter-profile-registry.log

ENTRYPOINT ["java", "-jar", "policy-registry.jar"]