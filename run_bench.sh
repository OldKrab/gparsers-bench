MODULE="$1"

if [ "$MODULE" -eq "" ]; then
    echo "Error: Please provide module."
    exit 1
fi
REGEX="$2"


DISABLE_NEO4J_WARNING_FLAGS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.http=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED"

JMH_FLAGS='-bm avgt
-wi 15
-w 100ms
-i 10
-r 100ms
-f 2
-tu ms
-p file=atom-primitive.owl,biomedical-mesure-primitive.owl'

/usr/lib/jvm/java-11-openjdk/bin/java $DISABLE_NEO4J_WARNING_FLAGS -jar \
  "$MODULE"/target/benchmarks.jar $JMH_FLAGS $REGEX