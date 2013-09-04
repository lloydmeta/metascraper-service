package "unzip" do
  action :install
end

remote_file "#{node[:play2][:path][:prefix]}/play-#{node[:play2][:version]}.zip" do
  source "http://downloads.typesafe.com/play/#{node[:play2][:version]}/play-#{node[:play2][:version]}.zip"
  action :create_if_missing
end

execute "extract" do
  user "root"
  command "unzip play-#{node[:play2][:version]}.zip"
  cwd "#{node[:play2][:path][:prefix]}"
  not_if do
    File.exists?("#{node[:play2][:path][:prefix]}/play-#{node[:play2][:version]}")
  end
end

bash "set permissions" do
  user "root"
  cwd node[:play2][:path][:prefix]
  code "chown vagrant -R play-#{node[:play2][:version]}"
end

template "/etc/profile.d/play.sh" do
  source "profile.d.play.erb"
  owner "root"
  group "root"
  mode 0755
  variables(:play_dir => "#{node[:play2][:path][:prefix]}/play-#{node[:play2][:version]}")
end
