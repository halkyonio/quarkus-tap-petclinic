load("@ytt:data", "data")
load("@ytt:base64", "base64")
load("@ytt:assert", "assert")

data.values.docker_repository or assert.fail("missing docker_repository")
data.values.docker_username or assert.fail("missing docker_username")
data.values.docker_password or assert.fail("missing docker_password")

# export
values = data.values

# extract the docker registry from the repository string
docker_registry = "https://index.docker.io/v1/"
parts = data.values.docker_repository.split("/", 1)
if len(parts) == 2:
    if '.' in parts[0] or ':' in parts[0]:
        docker_registry = parts[0]
    end
end
