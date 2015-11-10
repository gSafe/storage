# GSAFE-STORAGE
Application de stockage de fichiers avec une notion de cloisonnement.

## BUILD DU PROJET
Si vous ne possédez pas l'exécutable du projet, il est possible de le construire de la manière suivante :
À la racine du projet, éxécuter : mvn clean package
Cette commande permettera de créer d'une part le binaire du serveur :
  `./storage-api/target/storage.jar` (qui est l'exécutable du projet).
et d'autre part le binaire du client :
  `./storage-client/target/storage-client-[version].jar

## ÉXÉCUTER L'APPLICATION
Pour lancer le serveur
java -jar storage.jar server {path/config.yml}
le fichier config.yml est le fichier de configuration de l'application. Des exemples sont fournit à ./storage-api/src/main/resources/*.yml.exemple

Une fois le serveur en fonctionnement, une documentation embarquée est disponible à l'adresse : `/docs`

## CHIFFREMENT
Optionnellement, le stockage peut être configuré pour chiffrer les données y transitant.
Pour faciliter les développements et l'automatisation, il est possible également de configurer la clef maître et un salt crypto dans le fichier de config même si cette pratique est déconseillé. Nous préconisons plutôt la saisie au démarrage du service.

- chiffrement symétrique AES
- clef de 128 bits 
- cipher-block chaining (CBC)
- taille block de 128 bits
- PKCS5Padding
- fonction de dérivation PBKDF2--WithHmac--SHA1
- clef à ce seul usage, pas de clef commune
- dérivé d'un secret maître et d'un salt

## CONFIGURATION HTTPS AVEC AUTHENTICATION MUTUELLE

### Configuartion serveur 

Modifier la configuration de l'application comme suit:

```
http :
  port: 8443
  
  ....
  
  connectorType: nonblocking+ssl
  ssl:

    keyStore : path to server.com.jks (certificats signés par la CA)
    keyStorePassword : ???
    validatePeers : false
    trustStore: path to ca.jks  (Certificat de la CA)
    trustStorePassword: ???
    trustStoreType: JKS
    needClientAuth : true
  
    ....

```

## Configuartion client 
 

Instancier le client comme suit : 
 
```
StorageClientBuilder scb = new StorageClientBuilder();
StorageClient storageClient = scb.serviceUrl("https://server.com")
        // Verifier le nom DNS, mettre true
        .verifyHost(false)
        // (Certificat de la CA)
        .addTrustStore("path to trust.jks", "trustStorePassword")
        // (certificats signés par la CA)
        .addKeyStore("path to key.jks", "keyStorePassword")
        .build();
```




