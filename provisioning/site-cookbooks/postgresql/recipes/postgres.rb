# enable_pgdg_apt and apt_pgdg_postgresql directives
# allow us to install version 9.2 of Postgresql
node.override["postgresql"]["version"] = "9.2"
node.override['postgresql']['enable_pgdg_apt'] = true
node.override['postgresql']['dir'] = "/var/lib/postgresql/9.2/main"
# apparently the next line is required for chef-solo
# https://github.com/opscode-cookbooks/postgresql#chef-solo-note
node.default["postgresql"]["password"]["postgres"] = "password"

include_recipe "postgresql::apt_pgdg_postgresql"
include_recipe "postgresql::server"
include_recipe "postgresql::default"