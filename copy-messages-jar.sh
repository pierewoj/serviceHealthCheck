#! /bin/bash
cd messages
docker-compose up --build
cd ..
cp messages/src/target/original-messages-1.0-SNAPSHOT.jar server/src/jars
cp messages/src/target/original-messages-1.0-SNAPSHOT.jar worker/src/jars
cp messages/src/target/original-messages-1.0-SNAPSHOT.jar updater/src/jars
