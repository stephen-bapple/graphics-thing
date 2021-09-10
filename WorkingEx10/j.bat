del *.class
javac -cp .;\lwjgl\jar\lwjgl.jar %1.java
java -cp .;\lwjgl\jar\lwjgl.jar -Djava.library.path=\LWJGL\native %1 %2 %3 %4 %5