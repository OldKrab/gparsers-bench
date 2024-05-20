MODULE="$1"
NEO4J_PATH="$2"
NEO4J_CONFIG_PATH="$3"
DB_NAME="$4"

DISABLE_NEO4J_WARNING_FLAGS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.http=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED -Xmx94g"

JMH_FLAGS="-bm avgt
-wi 0
-w 2000ms
-i 1
-r 5ms
-f 1
-tu s
-p dbPath=$NEO4J_PATH
-p dbConfigPath=$NEO4J_CONFIG_PATH
-p dbName=$DB_NAME
"

"$JAVA_HOME"/bin/java $DISABLE_NEO4J_WARNING_FLAGS -Xmx100g -jar \
  "$MODULE"/target/benchmarks.jar -o out/"$MODULE"_yago.res $JMH_FLAGS Yago