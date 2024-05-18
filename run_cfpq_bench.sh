MODULE="$1"

REGEX="$2"


DISABLE_NEO4J_WARNING_FLAGS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.http=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED"

JMH_FLAGS='-bm avgt
-f 2
-tu s
-p file=eclass.csv,go_hierarchy.csv,go.csv,geospecies.csv'

 "$JAVA_HOME"/bin/java $DISABLE_NEO4J_WARNING_FLAGS -Xmx10g  -jar \
   gparsers/target/benchmarks.jar -o gparsers.res2 $JMH_FLAGS CFPQ

"$JAVA_HOME"/bin/java $DISABLE_NEO4J_WARNING_FLAGS -Xmx10g -jar \
  meerkat/target/benchmarks.jar -o meerkat.res2 $JMH_FLAGS CFPQ
