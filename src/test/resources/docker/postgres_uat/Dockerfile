FROM postgres

ENV POSTGRES_USER=test_user
ENV POSTGRES_PASSWORD=password
ENV POSTGRES_DB=test_db

#ADD THE SCRIPT TO CREATE THE SCHEMA UNDER TEST
ADD drop_and_create.sql /docker-entrypoint-initdb.d/

