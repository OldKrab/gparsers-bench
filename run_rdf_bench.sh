MODULE="$1"

DISABLE_NEO4J_WARNING_FLAGS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.http=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED"

JMH_FLAGS='-bm avgt
-wi 0
-w 2000ms
-i 1
-r 5ms
-f 1
-tu ms
-p file=atom-primitive.owl,biomedical-mesure-primitive.owl,foaf.rdf,generations.owl,people_pets.rdf,pizza.owl,skos.rdf,travel.owl,univ-bench.owl,wine.rdf'
"$JAVA_HOME"/bin/java $DISABLE_NEO4J_WARNING_FLAGS -Xmx100g  -jar \
  "$MODULE"/target/benchmarks.jar -o out/"$MODULE"_rdf.res $JMH_FLAGS RDF