FROM registry.access.redhat.com/ubi8/ubi:8.1-397

MAINTAINER Mountain Fog "support@mtnfog.com"

ENV GID 55
ENV USER philter

RUN groupadd -g $GID $USER || groupmod -n $USER `getent group $GID | cut -d: -f1` \
    && useradd --system --create-home --gid $USER $USER \
    && usermod --lock --shell /sbin/nologin $USER \
    && chage -E0 $USER

RUN mkdir -p /opt/filter-profile-registry \
    && mkdir -p /opt/filter-profile-registry/ssl \
    && mkdir -p /opt/filter-profile-registry/profiles

RUN dnf install -y java-11-openjdk

USER $USER:$USER

COPY files/filter-profile-registry.jar /opt/filter-profile-registry/
COPY files/application.properties /opt/filter-profile-registry/
COPY files/filter-profile-registry.conf /opt/filter-profile-registry/
COPY files/README.txt /opt/filter-profile-registry/
COPY files/LICENSE.txt /opt/filter-profile-registry/
COPY files/NOTICE.txt /opt/filter-profile-registry/

RUN keytool -genkeypair -keypass Password123! -dname "CN=$USER, O=$USER, C=US" -alias filter-profile-registry -keyalg RSA -keysize 4096 -storepass Password123! -storetype PKCS12 -keystore /opt/filter-profile-registry/ssl/filter-profile-registry.p12 -validity 3650 \
    && echo "\n" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "# SSL certificate settings" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "server.ssl.key-store-type=PKCS12" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "server.ssl.key-store=/opt/filter-profile-registry/ssl/filter-profile-registry.p12" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "server.ssl.key-store-password=Password123!" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "server.ssl.key-alias=filter-profile-registry" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "security.require-ssl=true" | tee -a /opt/filter-profile-registry/application.properties

USER root:root

RUN chown -R $USER:$USER /opt/filter-profile-registry \
    && chmod +x /opt/filter-profile-registry/filter-profile-registry.jar

USER $USER:$USER

EXPOSE 8080

WORKDIR /opt/filter-profile-registry
CMD java -jar filter-profile-registry.jar && tail -F /var/log/filter-profile-registry.log
