# Keptn Notification service

This is a Keptn service that listens to Keptn Cloud Events and sends a notification about the event to a chat app. (currently Slack but extendable)

---

# notification-service

This implements a notification-service for Keptn. If you want to learn more about Keptn visit us on [keptn.sh](https://keptn.sh)

## Installation

### Deploy in your Kubernetes cluster

To deploy the current version of the *notification-service* in your Keptn Kubernetes cluster, apply the [`deploy/service.yaml`](deploy/service.yaml) file:

```console
kubectl apply -f deploy/service.yaml
```
This should install the `notification-service` together with a Keptn `distributor` into the `keptn` namespace, which you can verify using:

```console
kubectl -n keptn get deployment notification-service -o wide
kubectl -n keptn get pods -l run=notification-service
```

### Uninstall

To delete a deployed *notification-service*, use the file `deploy/*.yaml` files from this repository and delete the Kubernetes resources:

```console
kubectl delete -f deploy/service.yaml
```

## Usage

The goal of the *notification-service* is to inform the user about a new event via a notification in a chat app. (currently Slack but extendable)

It needs the following configuration:

### Kubernetes secrets

* for Slack Environment

  The Kubernetes secret `slack-access` containing the values `SLACK_TOKEN` and `SLACK_CHANNEL` is needed.

* for Keptn Environment

  The Kubernetes secret `keptn-access` containing the value `KEPTN_BRIDGE_DOMAIN` is needed.

<!-- add installation here -->

## Development

This is an open source project, so I welcome any contributions to make it even better!

### Build yourself

<!-- Check / change the names of everything / create files and folders -->

* Build the binary: `./mvnw package -Pnative` (with GraalVM) `./mvnw package -Pnative -Dquarkus.native.container-build=true` (without GraalVM)
* Build not a binary: `./mvnw package`
* Run tests: ``
* Build the docker image: `docker build -f src/main/docker/Dockerfile.native -t quarkus/notification-service .`
* Run the docker image locally: `docker run -i --rm -p 8080:8080 quarkus/notification-service`

<!-- Also true for my project? -->
* Push the docker image to DockerHub: `docker push keptnsandbox/notification-service:dev`
* Deploy the service using `kubectl`: `kubectl apply -f deploy/`
* Delete/undeploy the service using `kubectl`: `kubectl delete -f deploy/`
* Watch the deployment using `kubectl`: `kubectl -n keptn get deployment notification-service -o wide`

<!-- test this -->

* Get logs using `kubectl`: `kubectl -n keptn logs deployment/notification-service -f`
* Watch the deployed pods using `kubectl`: `kubectl -n keptn get pods -l run=notification-service`

## License

Please find more information in the [LICENSE](LICENSE) file.
