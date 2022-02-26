FROM haiyangzou/haipine

COPY target/lib /home/pot-room/lib
COPY target/pot-room-releases.jar /home/pot-room/pot-room-releases.jar
COPY groovy /home/pot-room/groovy
ENV CLASSPATH .:${JAVA_HOME}/lib:${JRE_HOME}/lib:/home/pot-room/lib

WORKDIR /home/pot-room
CMD  java -jar pot-room-releases.jar
