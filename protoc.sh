#!/usr/bin/env bash

declare protoc_path=$HOME/.gradle/caches/modules-2/files-2.1/com.google.protobuf/protoc/3.21.12/62e794ebd383cad86347240f1c9b419c8d8fce10/protoc-3.21.12-linux-x86_64.exe
declare grpc_java_plugin_path=$HOME/.gradle/caches/modules-2/files-2.1/io.grpc/protoc-gen-grpc-java/1.51.3/6733a1769899769a54df42ad185108405b695b65/protoc-gen-grpc-java-1.51.3-linux-x86_64.exe
declare grpc_kotlin_plugin_path=./grpc_kotlin_plugin.sh

declare in_root=./app/src/grpc_proto
declare java_out_root=./app/src/main/java
declare kotlin_out_root=./app/src/main/kotlin

rm -rf $java_out_root/grpc_code_gen
rm -rf $kotlin_out_root/grpc_code_gen

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

touch $java_out_root/grpc_code_gen/.gitignore
printf "*\n!.gitignore" > $java_out_root/grpc_code_gen/.gitignore
touch $kotlin_out_root/grpc_code_gen/.gitignore
printf "*\n!.gitignore" > $kotlin_out_root/grpc_code_gen/.gitignore
