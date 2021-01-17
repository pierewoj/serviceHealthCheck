#! /bin/bash
cd messages
docker-compose up --build
cd ..
cd database-models
docker-compose up --build
cd ..
cp messages/src/target/original-messages-1.0-SNAPSHOT.jar server/src/jars
cp messages/src/target/original-messages-1.0-SNAPSHOT.jar worker/src/jars
cp messages/src/target/original-messages-1.0-SNAPSHOT.jar updater/src/jars
cp database-models/src/target/original-database-models-1.0-SNAPSHOT.jar server/src/jars
cp database-models/src/target/original-database-models-1.0-SNAPSHOT.jar updater/src/jars
