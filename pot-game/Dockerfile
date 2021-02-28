FROM haiyangzou/haipine

COPY target/lib /home/pot-game/lib
COPY target/pot-game-releases.jar /home/pot-game/pot-game-releases.jar

ENV JAVA_HOME /usr/lib/jvm/default-jvm
ENV JRE_HOME /usr/lib/jvm/default-jvm/jre
ENV CLASSPATH .:${JAVA_HOME}/lib:${JRE_HOME}/lib:/home/pot-game/lib

WORKDIR /home/pot-game
CMD  java -jar pot-game.jar
