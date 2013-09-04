name "dev_server"

description "A server running Postgres, Java, Play, Scala, Redis"

run_list(
  "role[base]",
  "role[java_node]",
  "role[sbt_node]",
  "recipe[play]",
)

# removed "role[rvm_node]", "role[postgres_node]", and "role[redis_node]" for now