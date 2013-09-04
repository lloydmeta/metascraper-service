name "java"
description "Install  Java on Ubuntu"
default_attributes(
  "java" => {
    "jdk_version" => "7"
  }
)
run_list(
  "recipe[java]"
)