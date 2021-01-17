#!/bin/bash

liquibase --url="jdbc:postgresql://localhost:5431/postgres"  --username=postgres --password=test --changeLogFile=changelog.xml update
