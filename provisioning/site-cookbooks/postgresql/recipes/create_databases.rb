%w{ development test staging production }.each do |environment|
  database_name = "vagrant_#{environment}"

  execute "create Databases: #{database_name}" do
    not_if "psql -qAt --list | grep -q '^#{database_name}\|'", :user => 'postgres'
    user 'postgres'
    command <<CMD
(createdb -E UTF8 #{database_name})
CMD
    action :run
  end

end