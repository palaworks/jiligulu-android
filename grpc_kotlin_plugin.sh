#!/usr/bin/env bash

declare protoc_gen_grpc_kotlin_jar_path=./bin/protoc_gen_grpc_kotlin.jar

java -jar $protoc_gen_grpc_kotlin_jar_path $@

