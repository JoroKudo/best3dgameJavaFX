Ode4J modifications

in OdeHelper.java I uncommented line 761 to read thus:
 	@SafeVarargs //--> Not available in Java 6!

maven failed to work with some obscure message I couldn't be bothered
to spend ages on so I manually compiled using the following procedure

cd ode4j/core/src/main/java
javac `find . -name *.java` -Xlint:unchecked
jar cf ode4j.jar .

The Jar file includes all code message etc as per that path in
the git repo
