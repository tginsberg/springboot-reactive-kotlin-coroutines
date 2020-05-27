## Reactive Spring Boot with Kotlin Coroutines

[![license](https://img.shields.io/github/license/tginsberg/springboot-reactive-kotlin-coroutines.svg)]()


This code supports a [blog post I wrote](https://todd.ginsberg.com/post/springboot-reactive-kotlin-coroutines/), and implements a reactive counter using Spring Boot, Kotlin, Kotlin Coroutines, and Redis.


### Getting The Code

```
git clone https://github.com/tginsberg/springboot-reactive-kotlin-coroutines.git
```

### Build Information

Technologies used in this project:

1. Java 11 (but 8 will work fine)
2. Spring Boot 2.3 (but 2.2.x will work fine)
3. Kotlin 1.3.72
4. Gradle 6.3

To run this project, you'll need:

1. Redis installed and ready to use on the default port
2. A cursory understanding of reactive concepts and Spring Boot

### Running the server

```
./gradlew bootRun
```

### Endpoints

| Purpose                  | Method | URL      | Accept Header       |
|--------------------------|--------|----------|---------------------|
| Current state of counter | GET    | `/`      | `application/json`  |
| Counter event stream     | GET    | `/`      | `text/event-stream` |
| Increment counter        | PUT    | `/up`   | `application/json`  |
| Decrement counter        | PUT    | `/down` | `application/json`  |
