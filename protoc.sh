#!/usr/bin/env bash

mkdir -p bin

rm -f bin/.gitignore
touch bin/.gitignore
printf "*\n!.gitignore" > bin/.gitignore

wget -nc -O bin/protoc "https://search.maven.org/remotecontent?filepath=com/google/protobuf/protoc/3.21.12/protoc-3.21.12-linux-x86_64.exe"
wget -nc -O bin/grpc_java_plugin "https://search.maven.org/remotecontent?filepath=io/grpc/protoc-gen-grpc-java/1.53.0/protoc-gen-grpc-java-1.53.0-linux-x86_64.exe"
wget -nc -O bin/protoc_gen_grpc_kotlin.jar "https://search.maven.org/remotecontent?filepath=io/grpc/protoc-gen-grpc-kotlin/1.3.0/protoc-gen-grpc-kotlin-1.3.0-jdk8.jar"

chmod +x bin/protoc
chmod +x bin/grpc_java_plugin

declare protoc_path=bin/protoc
declare grpc_java_plugin_path=bin/grpc_java_plugin
declare grpc_kotlin_plugin_path=grpc_kotlin_plugin.sh

declare in_root=app/src/grpc_proto
declare java_out_root=app/src/main/java
declare kotlin_out_root=app/src/main/kotlin

rm -rf $java_out_root/grpc_code_gen
rm -rf $kotlin_out_root/grpc_code_gen

mkdir $java_out_root/grpc_code_gen
mkdir $kotlin_out_root/grpc_code_gen

touch $java_out_root/grpc_code_gen/.gitignore
printf "*\n!.gitignore" > $java_out_root/grpc_code_gen/.gitignore
touch $kotlin_out_root/grpc_code_gen/.gitignore
printf "*\n!.gitignore" > $kotlin_out_root/grpc_code_gen/.gitignore

$protoc_path \
--plugin=protoc-gen-grpc-java=$grpc_java_plugin_path \
--plugin=protoc-gen-grpc-kotlin=$grpc_kotlin_plugin_path \
--proto_path=$in_root/comment \
--java_out=$java_out_root \
--grpc-java_out=$java_out_root \
--kotlin_out=$kotlin_out_root \
--grpc-kotlin_out=$kotlin_out_root \
$in_root/comment/*.proto \
&&
$protoc_path \
--plugin=protoc-gen-grpc-java=$grpc_java_plugin_path \
--plugin=protoc-gen-grpc-kotlin=$grpc_kotlin_plugin_path \
--proto_path=$in_root/post \
--java_out=$java_out_root \
--grpc-java_out=$java_out_root \
--kotlin_out=$kotlin_out_root \
--grpc-kotlin_out=$kotlin_out_root \
$in_root/post/*.proto \
&&
$protoc_path \
--plugin=protoc-gen-grpc-java=$grpc_java_plugin_path \
--plugin=protoc-gen-grpc-kotlin=$grpc_kotlin_plugin_path \
--proto_path=$in_root/token \
--java_out=$java_out_root \
--grpc-java_out=$java_out_root \
--kotlin_out=$kotlin_out_root \
--grpc-kotlin_out=$kotlin_out_root \
$in_root/token/*.proto

