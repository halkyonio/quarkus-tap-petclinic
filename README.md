## Quarkus Petclinic accelerator

A Quarkus Petclinic sample accelerator

You can build the image using docker, jib or using kpack if it has been deployed on the k8s platform

Install kind and local registry locally (using registry version 2.6 !)
```bash
git clone kind-tls-pwd-registry https://github.com/snowdrop/k8s-infra.git && cd k8s-infra/kind
./k8s/kind-tls-secured-reg.sh
```
Build the runtimes images needed
```bash
git clone https://github.com/quarkusio/quarkus-buildpacks.git && cd quarkus-buildpacks

# Generate the buildpacks image (pack ...)
./k8s/create-buildpacks.sh

# Tag and push the images to a private docker registry
export REGISTRY_URL="registry.local:5000"
docker tag redhat/buildpacks-builder-quarkus-jvm:latest $REGISTRY_URL/redhat-buildpacks/quarkus-java:latest
docker tag redhat/buildpacks-stack-quarkus-run:jvm $REGISTRY_URL/redhat-buildpacks/quarkus:run
docker tag redhat/buildpacks-stack-quarkus-build:jvm $REGISTRY_URL/redhat-buildpacks/quarkus:build

docker push $REGISTRY_URL/redhat-buildpacks/quarkus-java:latest
docker push $REGISTRY_URL/redhat-buildpacks/quarkus:build
docker push $REGISTRY_URL/redhat-buildpacks/quarkus:run
```
Login to the container registry to check if you can
```bash
docker login -u admin -p snowdrop registry.local:5000
WARNING! Using --password via the CLI is insecure. Use --password-stdin.
Login Succeeded
```
Fetch the TBS images and push them to your local registry
```bash
imgpkg copy -b "registry.pivotal.io/build-service/bundle:1.2.2" --to-repo registry.local:5000/kpack --registry-ca-cert-path $HOME/local-registry.crt
```
Extract the files to configure TBS to use your local registry
```bash
imgpkg pull -b "registry.local:5000/kpack:1.2.2" -o ./k8s/kpack --registry-ca-cert-path $HOME/local-registry.crt
```
Deploy TBS using your custom config
```bash
ytt -f ./k8s/kpack/values.yaml \
    -f ./k8s/kpack/config/ \
    -f $HOME/local-registry.crt \
    -v docker_repository="registry.local:5000/" \
    -v docker_username="admin" \
    -v docker_password="snowdrop" \
    | kbld -f ./k8s/kpack/.imgpkg/images.yml -f- \
    | kapp deploy -a kpack -f- -y

kapp delete -a kpack
```

Create a secret to access your local registry
```bash
kubectl create ns demo
kubectl create secret docker-registry registry-creds -n demo \
  --docker-server="https://registry.local:5000" \
  --docker-username="admin" \
  --docker-password="snowdrop"
  
kubectl delete -n demo secret/registry-creds  
```

Deploy the kpack runtime CRs
```bash
kapp deploy -a runtime-kpack \
  -f k8s/runtime-kpack/sa.yml \
  -f k8s/runtime-kpack/clusterstore.yml \
  -f k8s/runtime-kpack/clusterbuilder.yml \
  -f k8s/runtime-kpack/clusterstack.yml  

kapp delete -a runtime-kpack -y
```
To build a quarkus buildpack image using the code of the local project
```bash
# To be executed at the root of the project ;-)
kp image create quarkus-petclinic-image \
  --tag registry.local:5000/quarkus-petclinic \
  --local-path ./ \
  -c runtime \
  -n demo \
  --registry-ca-cert-path $HOME/local-registry.crt
```
To list the image and status
```bash
kp image list -n demo
kp image status quarkus-petclinic-image -n demo
```
To delete the image/build
```bash
kp image delete quarkus-petclinic-image -n demo
```

We can now deploy the application
```bash
kapp deploy -a quarkus-petclinic \
  -n demo \
  -f ./k8s/service.yml \
  -f ./k8s/deployment.yml

kapp delete -a quarkus-petclinic -n -y
```
and play with it from the browser `http://localhost:31000` :-)
