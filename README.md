# Quarkus Petclinic accelerator

A Quarkus Petclinic sample accelerator

You can build the image using docker, jib, s2i or using buildpack

## Build the image using Buildpack

To test locally if the project can be built using `Buildpacks` execute this command using Quarkus (>= 2.7) and the extension `quarkus-container-image-buildpack`:
```bash
./mvnw clean package \
    -Dquarkus.container-image.build=true \
    -Dquarkus.buildpack.jvm-builder-image=codejive/buildpacks-quarkus-builder:jvm
    -Dquarkus.container-image.builder=buildpack
```