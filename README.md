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

---

# DOMAIN

---

## Modelo de dominio

**El proyecto consiste en la creación de un CRUD para estudiantes,** así que, como primera implementación utilizando
`arquitectura hexagonal` será crear el modelo de dominio con el que vamos a representar a un `Student`.

Utilizando el siguiente directorio, crearemos nuestra clase de dominio `Student.java`:

> `/domain/model/Student.java`

La clase `Student` será nuestra clase de dominio. Las clases de dominio o modelos de dominio simplemente son `POJOS`.
Veamos con más detalle lo que son las **clases de dominio (POJO - Plain Old Java Object):**

- Una clase de dominio, también conocida como `POJO`, es una clase simple que típicamente encapsula datos y **no tiene
  ninguna lógica relacionada con la persistencia o la manipulación de datos.**


- Estas clases se utilizan para modelar objetos del mundo real en tu aplicación.


- Suelen ser clases simples con atributos, métodos de acceso (getters y setters) y, a veces, métodos de utilidad.


- No tienen ninguna anotación específica de persistencia, ya que no están directamente relacionadas con el
  almacenamiento de datos en una base de datos. Por lo tanto, son independientes de la capa de persistencia.

Por consiguiente, las clases de dominio son objetos con el que representamos el dominio, de tal forma que si más
adelante dejamos de usar PostgresSQL o MySQL y usamos una base de datos no relacional como MongoDB o Casandra,
este objeto de dominio va a permanecer invariable, es decir, no va a cambiar con ese tipo de particularidades.

### Modelo Student

````java

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String address;
}
````

Adicionalmente, crearemos un paquete de excepciones para nuestro **modelo de dominio**. En esta oportunidad crearemos la
excepción `StudentNotFoundException`:

### Excepción para el modelo Student

````java
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
````