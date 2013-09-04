name "base_server"

description "Base"

run_list(
  "recipe[chef-locale::default]",
  "recipe[apt::default]"
)