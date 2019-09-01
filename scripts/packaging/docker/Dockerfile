FROM 341239660749.dkr.ecr.us-east-1.amazonaws.com/mtnfog/philter-base-java8

MAINTAINER Mountain Fog "support@mtnfog.com"

RUN mkdir -p /opt/filter-profile-registry \
    && mkdir -p /opt/filter-profile-registry/ssl \
    && mkdir -p /opt/filter-profile-registry/profiles \
    && yum upgrade -y \
    && yum install -y shadow-utils.x86_64 python3 python3-devel gcc

USER $USER:$USER

COPY files/filter-profile-registry.jar /opt/filter-profile-registry/
COPY files/application.properties /opt/filter-profile-registry/
COPY files/filter-profile-registry.conf /opt/filter-profile-registry/
COPY files/README.txt /opt/filter-profile-registry/
COPY files/LICENSE.txt /opt/filter-profile-registry/
COPY files/NOTICE.txt /opt/filter-profile-registry/

USER root:root

RUN chown -R $USER:$USER /opt/filter-profile-registry \
    && chmod +x /opt/filter-profile-registry/filter-profile-registry.jar

USER $USER:$USER

RUN keytool -genkeypair -keypass Password123! -dname "CN=$USER, O=$USER, C=US" -alias filter-profile-registry -keyalg RSA -keysize 4096 -storepass Password123! -storetype PKCS12 -keystore /opt/filter-profile-registry/ssl/filter-profile-registry.p12 -validity 3650 \
    && echo "\n" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "# SSL certificate settings" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "server.ssl.key-store-type=PKCS12" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "server.ssl.key-store=/opt/filter-profile-registry/ssl/filter-profile-registry.p12" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "server.ssl.key-store-password=Password123!" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "server.ssl.key-alias=filter-profile-registry" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "security.require-ssl=true" | tee -a /opt/filter-profile-registry/application.properties

EXPOSE 8080

WORKDIR /opt/filter-profile-registry
CMD java -jar filter-profile-registry.jar && tail -F /var/log/filter-profile-registry.log
