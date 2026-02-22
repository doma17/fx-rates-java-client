rootProject.name = "fx-rates-java-client"

include("fx-rates-core")
include("fx-rates-spring-boot-starter")

project(":fx-rates-core").projectDir = file("exchangerate-core")
project(":fx-rates-spring-boot-starter").projectDir = file("exchangerate-spring-boot-starter")
