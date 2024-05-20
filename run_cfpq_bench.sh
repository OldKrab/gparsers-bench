MODULE="$1"

DISABLE_NEO4J_WARNING_FLAGS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.http=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED"

JMH_FLAGS='-bm avgt
-wi 0
-w 2000ms
-i 1
-r 5ms
-f 1
-tu s
-p file=go_hierarchy.csv,eclass.csv,enzyme.csv,geospecies.csv,go.csv'

"$JAVA_HOME"/bin/java $DISABLE_NEO4J_WARNING_FLAGS -Xmx10g  -jar \
   "$MODULE"/target/benchmarks.jar -o out/"$MODULE"_cfpq.res $JMH_FLAGS CFPQ


