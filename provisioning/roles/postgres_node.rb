name "postgres_node"

description "A node that has postgres installed"

run_list(
  "recipe[postgresql::postgres]",
  "recipe[postgresql::create_databases]"
)