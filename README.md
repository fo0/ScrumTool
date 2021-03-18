![Build-Publish](https://github.com/fo0/ScrumTool/workflows/Build-Publish/badge.svg?event=push)

# ScrumTool
![Demo-GUI](docs/chrome_2020-08-17_09-49-26.png)

### Getting Started
Just download the latest app (via releases) and start it via <br>
```
java -jar ScrumTool-VERSION.jar
```

## Wiki
https://github.com/fo0/ScrumTool/wiki

## Releases
Latest Stable: https://github.com/fo0/ScrumTool/releases

# Configurations
for applying configurations just create a spring application.properties file in the folder of your scrumtool.jar and add the following properties 

```properties

scrumtool.database.inmem=false #default=true
```

# SSL Cert
I support two ways of SSL Cert
## PEM (i.e. lets encrypt)
create or add to your application.properties
```properties
server.ssl.enabled=true
server.ssl.key-store-type=PEMCFG.MOD
server.ssl.key-store=file:keystore.properties
server.ssl.key-store-password=dummy
server.ssl.alias=keycert
security.require-ssl=true
```

create or add to your keystore.properties
```properties
alias=keycert
source.key=/etc/letsencrypt/live/<YOUR_DOMAIN>/privkey.pem
source.cert=/etc/letsencrypt/live/<YOUR_DOMAIN>/cert.pem
```

# Keystore
read the spring docs or use https://www.baeldung.com/spring-boot-https-self-signed-certificate
