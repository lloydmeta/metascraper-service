name "sbt_node"

description "SBT node"

run_list(
  "recipe[chef-sbt::default]"
)