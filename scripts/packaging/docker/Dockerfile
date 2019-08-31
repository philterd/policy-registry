FROM amazoncorretto:11

MAINTAINER Mountain Fog "support@mtnfog.com"

ARG GID=55

RUN mkdir -p /opt/filter-profile-registry \
    && mkdir -p /opt/filter-profile-registry/ssl \
    && yum upgrade -y \
    && yum install -y shadow-utils.x86_64 python3 python3-devel gcc

RUN groupadd -g $GID philter || groupmod -n philter `getent group $GID | cut -d: -f1` \
    && useradd --system --create-home --gid philter philter \
    && usermod --lock --shell /sbin/nologin philter \
    && chage -E0 philter

USER philter:philter

COPY files/filter-profile-registry.jar /opt/filter-profile-registry/
COPY files/application.properties /opt/filter-profile-registry/
COPY files/filter-profile-registry.conf /opt/filter-profile-registry/
COPY files/README.txt /opt/filter-profile-registry/
COPY files/LICENSE.txt /opt/filter-profile-registry/
COPY files/NOTICE.txt /opt/filter-profile-registry/

USER root:root

RUN chown -R philter:philter /opt/filter-profile-registry \
    && chmod +x /opt/philter/filter-profile-registry.jar

USER philter:philter

RUN keytool -genkeypair -keypass Password123! -dname "CN=philter, O=philter, C=US" -alias philter -keyalg RSA -keysize 4096 -storepass Password123! -storetype PKCS12 -keystore /opt/philter/ssl/philter.p12 -validity 3650 \
    && echo "\n" | tee -a /opt/philter/application.properties \
    && echo "# SSL certificate settings" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "server.ssl.key-store-type=PKCS12" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "server.ssl.key-store=/opt/philter/ssl/philter.p12" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "server.ssl.key-store-password=Password123!" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "server.ssl.key-alias=philter" | tee -a /opt/filter-profile-registry/application.properties \
    && echo "security.require-ssl=true" | tee -a /opt/filter-profile-registry/application.properties

EXPOSE 8080

WORKDIR /opt/filter-profile-registry
CMD java -jar filter-profile-registry.jar && tail -F /var/log/filter-profile-registry.log
