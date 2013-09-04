name "rvm_node"

description "A node that has rvm installed"

run_list(
  "recipe[rvm::rvm]"
)