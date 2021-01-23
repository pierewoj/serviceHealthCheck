#!/bin/bash

liquibase --url="jdbc:postgresql://db:5432/postgres"  --username=postgres --password=test --changeLogFile=changelog.xml update
