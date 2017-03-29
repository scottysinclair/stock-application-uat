#
# The base image has db2-express installed as well as a small Java utility
# to execute SQL scripts
#
FROM scottysinclair/db2-express-sr

ENV DB2INST1_PASSWORD=password
ENV LICENSE=accept

USER db2inst1

#copy the script to create the schema into the executor folder.
COPY drop_and_create.sql /home/db2inst1/script-executor/todo

