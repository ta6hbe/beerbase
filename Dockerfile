FROM adoptopenjdk:11-jdk-hotspot as builder

WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src

RUN     ./gradlew build -x test
ARG     JAR_FILE=build/libs/*.jar
COPY    ${JAR_FILE} build/libs/app.jar
RUN     mkdir -p build/libs/dependency && (cd build/libs/dependency; jar -xf ../app.jar)

FROM adoptopenjdk:11-jdk-hotspot
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/libs/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.hperperidis.beerbase.BeerbaseApplication"]
