FROM quay.io/keycloak/keycloak:24.0

COPY ./import /opt/keycloak/data/import

CMD [ "start-dev", "--import-realm", "--http-port", "80", "--https-port", "443" ]

EXPOSE 80