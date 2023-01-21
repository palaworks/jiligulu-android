#!/usr/bin/env bash

declare protoc_gen_grpc_kotlin_jar_path=$HOME/.gradle/caches/modules-2/files-2.1/io.grpc/protoc-gen-grpc-kotlin/1.3.0/cb20cb447ec7c77d59d118580db3630c92b9b0bb/protoc-gen-grpc-kotlin-1.3.0-jdk8.jar

java -jar $protoc_gen_grpc_kotlin_jar_path $@
