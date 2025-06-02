## About

The backend is built in java with the api handling essentially build from scratch because frameworks such as spark or spring boot were not able to handle direct access to a lambda function from cloudfront. It is run on AWS lambda utilizing the free tier for lambda. It is being accessed through a lambda url along with cloudfronts free tier to avoid API gateways costs. Its state control is a MySQL database being hosted on an Oracle cloud VM. The SQL connection works and the lambda and local testing work so do not touch those unless specifically told to.

## Build

#### Prod

Run `mvn clean package -Pproduction`

#### Local

Run `mvn clean package -Plocal`

- clean clears out the target dir so only current buld stuff is in there
- -P chooses the profile in pom.xml

If getting build issues of not finding certain dependency versions use the flag `-U` to force update deps

## Deploy

1. Build
2. Upload shaded file to aws lambda

## Run locally

1. Build locally
2. Run `mvn exec:java -Dexec.mainClass="nf.free.coursegenius.LocalRun" -Plocal`
