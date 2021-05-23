#!/usr/bin/env bash

## When connecting to the db with postgres client you need to select the host as localhost !

### DUMP DATA FROM PROD
# ➜ /usr/local/Cellar/libpq/13.1/bin/pg_dump --dbname=makromapa-pay --file=/Users/mmalik/Documents/MakroMapa/04_04_2021_pay.sql --schema=public --username=makromapa-pay-user --host=127.0.0.1 --port=5432
############
# ➜ /usr/local/Cellar/libpq/13.1/bin/pg_dump --dbname=makromapa-pay --file=/Users/mmalik/Documents/MakroMapa/04_04_2021_pay.sql --schema=public --username=makromapa-pay-user --host=127.0.0.1 --port=5432
######################

### RESTORE DATA FROM DATA DUMP FILE
# CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
# ➜ /usr/local/Cellar/libpq/13.1/bin/psql --file=/Users/mmalik/Documents/MakroMapa/04_04_2021_pay.sql --username=makromapa-pay-user --host=127.0.0.1 --port=5432 makromapa-pay
#
# ➜ /usr/local/Cellar/libpq/13.1/bin/psql --file=/Users/mmalik/Documents/MakroMapa/04_04_2021_pay.sql --username=makromapa-pay-user --host=127.0.0.1 --port=5432 makromapa-pay
###########

gcloud auth login
gcloud config set project makromapa-305711
./cloud_sql_proxy -instances="makromapa-305711:europe-west1:makromapa=tcp:5432"