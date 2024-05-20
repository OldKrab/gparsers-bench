
mkdir -p out
bash run_rdf_bench.sh gparsers
bash run_rdf_bench.sh meerkat
bash run_cfpq_bench.sh gparsers
bash run_cfpq_bench.sh meerkat
bash run_yago_bench.sh gparsers /home/old/diploma/sandbox/rlqdag/neo4j_4.4.33 /home/old/diploma/sandbox/rlqdag/neo4j_4.4.33/conf/neo4j.conf yago-neoj4
bash run_yago_bench.sh meerkat
