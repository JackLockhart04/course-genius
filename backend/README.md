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
