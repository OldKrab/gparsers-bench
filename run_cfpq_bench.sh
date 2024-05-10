MODULE="$1"

REGEX="$2"


DISABLE_NEO4J_WARNING_FLAGS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.http=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED"

JMH_FLAGS='-bm avgt
-f 2
-tu s
-p file=eclass.csv,enzyme.csv'

"$JAVA_HOME"/bin/java $DISABLE_NEO4J_WARNING_FLAGS -jar \
  gparsers/target/benchmarks.jar -o gparsers.res $JMH_FLAGS CFPQ

"$JAVA_HOME"/bin/java $DISABLE_NEO4J_WARNING_FLAGS -jar \
  meerkat/target/benchmarks.jar -o meerkat.res $JMH_FLAGS CFPQ