MODULE="$1"
NEO4J_PATH="$2"
NEO4J_CONFIG_PATH="$3"
DB_NAME="$4"

DISABLE_NEO4J_WARNING_FLAGS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.http=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED"

JMH_FLAGS="-bm avgt
-tu s
-p dbPath=$NEO4J_PATH
-p dbConfigPath=$NEO4J_CONFIG_PATH
-p dbName=$DB_NAME
"

"$JAVA_HOME"/bin/java $DISABLE_NEO4J_WARNING_FLAGS -jar \
  "$MODULE"/target/benchmarks.jar $JMH_FLAGS Yago