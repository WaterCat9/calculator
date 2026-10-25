# ===== Stage 1: builder - compile and package with Maven =====
# FROM specifies the base image. maven:3.8-openjdk-17 bundles Maven 3.8 and JDK 17,
# which matches the maven.compiler.target in our pom.xml.
FROM maven:3.8-openjdk-17 AS builder

# WORKDIR creates and switches to /app inside the container.
WORKDIR /app

# COPY copies files from the build context (your project folder) into the image.
# We copy pom.xml and src only - target/ and .git/ are not needed for a clean build.
COPY pom.xml .
COPY src ./src

# RUN executes a command during the build. mvn package compiles, runs the 9 JUnit
# tests and produces the shaded fat jar that contains all dependencies.
RUN mvn -B package

# ===== Stage 2: runtime - only the JDK and the jar are needed =====
# A second FROM starts a fresh image, so the final image only carries the jar.
# We reuse maven:3.8-openjdk-17 as the runtime base image: the classic
# openjdk:17-jre-slim image is no longer cached by domestic mirror registries
# (403 Forbidden / not found), while this image is verified to be available.
# It is larger than a slim JRE, but it fully works with the same JDK 17.
FROM maven:3.8-openjdk-17

WORKDIR /app

# COPY --from=builder copies the jar produced in stage 1 into this new image.
COPY --from=builder /app/target/calculator-1.0.jar calculator-1.0.jar

# ENTRYPOINT defines the fixed command run when the container starts.
# It is written as a JSON array (no shell parsing). Arguments given to
# "docker run" are appended after it, e.g. "docker run ... 4 + 2" executes
# "java -jar calculator-1.0.jar 4 + 2".
# (CMD would be replaced by those arguments instead of appended, so we use ENTRYPOINT.)
ENTRYPOINT ["java", "-jar", "calculator-1.0.jar"]
