.DEFAULT_GOAL := exec
.PHONY :install exec

install:
	mvn install:install-file -Dfile=src/main/resources/Ludii-1.3.11.jar -DgroupId=org.Ludii -DartifactId=Ludii -Dversion=1.3.11 -Dpackaging=jar -DgeneratePom=true

exec:
	mvn compile exec:java
