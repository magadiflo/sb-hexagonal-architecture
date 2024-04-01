# [Creación de un API REST usando arquitectura hexagonal con Springboot 3 - 2024](https://www.youtube.com/watch?v=xn3E3EKK5jM&t=54s)

Tutorial tomado del canal de youtube de **Dev Dominio**

---

## Dependencias

Notar que estamos especificando la versión de `Lombook` y la de `MapStruct`, dado que al tener ambas librerías en el
mismo proyecto de `Spring Boot` pueden generar conflicto y no trabajar como se espera, es por eso que es necesario
especificar las versiones.

````xml
<!--Spring Boot 3.2.4-->
<!--Java 21-->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
````

## Plugins

Los plugins dentro del `pom.xml` también los modificaremos, a causa de que estamos trabajando con `Lombook`
y `MapStruct`. Al realizar esta configuración, estaremos permitiendo que tanto `Lombook` y `MapStruct` puedan trabajar
juntos:

````xml

<build>
    <plugins>
        <!--        Plugin por defecto-->
        <!--        <plugin>-->
        <!--            <groupId>org.springframework.boot</groupId>-->
        <!--            <artifactId>spring-boot-maven-plugin</artifactId>-->
        <!--            <configuration>-->
        <!--                <excludes>-->
        <!--                    <exclude>-->
        <!--                        <groupId>org.projectlombok</groupId>-->
        <!--                        <artifactId>lombok</artifactId>-->
        <!--                    </exclude>-->
        <!--                </excludes>-->
        <!--            </configuration>-->
        <!--        </plugin>-->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>21</source>
                <target>21</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.30</version>
                    </path>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>1.5.5.Final</version>
                    </path>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-mapstruct-binding</artifactId>
                        <version>0.2.0</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
````