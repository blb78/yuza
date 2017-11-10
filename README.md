#Yuza
Mirco service utilisateur écrit en java

## Getting Started
Développer avec le framework [Spring Boot](https://projects.spring.io/spring-boot/) et [maven](https://github.com/apache/maven/blob/master/apache-maven/README.txt)

### Prérequis
- Java 1.8
- Maven

### Installation
```
git clone git@git.skillogs.com:skillogs/yuza.git 
```

### Compile + Tests

```
mvn clean install
```

### Launch


```
mvn spring-boot:run -Dspring.profiles.active=local 
```

## Endpoints
### User Ressources

- **[<code>GET</code> users](./documentation/endpoints/GET_USERS.md)**
- **[<code>GET</code> users/{id}](./documentation/endpoints/GET_USERS_ID.md)**
- **[<code>GET</code> users/{id}/courses](./documentation/endpoints/GET_USERS_COURSES.md)**
- **[<code>POST</code> users](./documentation/endpoints/POST_USERS.md)**
