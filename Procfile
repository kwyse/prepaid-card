release: java $JAVA_OPTS -Ddw.database.url=$JDBC_DATABASE_URL -Ddw.database.user=$JDBC_DATABASE_USERNAME -jar ./target/prepaid-card-1.0-SNAPSHOT.jar db migrate config.yml
web: java $JAVA_OPTS -Ddw.database.url=$JDBC_DATABASE_URL -Ddw.database.user=$JDBC_DATABASE_USERNAME -Ddw.server.applicationConnectors[0].port=$PORT -jar ./target/prepaid-card-1.0-SNAPSHOT.jar server ./config.yml
