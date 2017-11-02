## Some commands

### Compile + Tests

```sh
mvn clean install
```

### Launch


```sh
mvn spring-boot:run -Dspring.profiles.active=local 
```

### Url

- /users [GET,POST]
- /users/{id} [GET,PUT,DELETE]
- /users/authenticate [POST]

exemple d'objet json pour la creation :

```
{
 	"email": "toto@toto.com",
 	"firstName": "toto",
 	"lastName": "toto",
 	"password":"toto" 
 	}
 ```

exemple d'objet json pour l'auth :
 
```
{
 	"email": "toto@toto.com",
 	"password":"toto" 
 	}
 ```
