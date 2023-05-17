#!/usr/bin/sh
FX_PATH="path/to/javafx-sdk/lib"
JAVA_PATH="path/to/jdk/bin/java"
JAR=/path/to/ReadIt.jar
cwd="$(dirname "$0")"
$JAVA_PATH --module-path $FX_PATH --add-modules javafx.controls,javafx.graphics,javafx.media,javafx.web -jar $JAR "$@" >"$cwd"/log.txt
