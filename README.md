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

---

# APPLICATION

---

En la capa de aplicación crearemos los puertos de entrada `(input)` y de salida `(output)`, además **crearemos la
implementación del puerto de entrada (service) que a su vez hará uso del puerto de salida (como inyección de
dependencia).**

## Puerto de entrada (input)

Esta interfaz contendrá operaciones que se pueden hacer con el modelo de dominio Student. Aquí detallamos la
funcionalidad de la aplicación.

Para nuestro proyecto, aquí definimos los métodos `CRUD`:

````java
public interface StudentServicePort {
    List<Student> findAllStudents();

    Student findStudentById(Long id);

    Student saveStudent(Student student);

    Student updateStudent(Long id, Student student);

    void deleteStudentById(Long id);
}
````

## Puerto de salida (output)

Aquí definimos los métodos necesarios que permitirán **interactuar** con la parte externa, **con la parte de
persistencia.** Puede haber distintos puertos de salida, por ejemplo, si se quiere mandar hacia una cola de mensajes.

Los métodos siguientes se utilizarán para almacenar los objetos de dominio, es por eso que aquí no declaramos el método
update, ya que será realizado por el método save.

````java
public interface StudentPersistentPort {
    List<Student> findAllStudents();

    Optional<Student> findStudentById(Long id);

    Student saveStudent(Student student);

    void deleteStudentById(Long id);
}
````

## Implementación del puerto de entrada (Servicio)

El servicio de la capa de aplicación implementa el puerto de entrada y hace uso del puerto de salida. En otras palabras,
aquí estamos definiendo la implementación del puerto de entrada (StudentServicePor) y además, estamos inyectando una
futura implementación del puerto de salida (StudentPersistentPort) y digo **"futura implementación"** puesto que lo que
realmente estamos usando es el puerto de salida (interfaz StudentPersistentPort) que en tiempo de ejecución tomará
una implementación concreta.

````java

@RequiredArgsConstructor
@Service
public class StudentService implements StudentServicePort {

    private final StudentPersistentPort studentPersistentPort;

    @Override
    public List<Student> findAllStudents() {
        return this.studentPersistentPort.findAllStudents();
    }

    @Override
    public Student findStudentById(Long id) {
        return this.studentPersistentPort.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Error al buscar estudiante. No se encuentra con id: %s".formatted(id)));
    }

    @Override
    public Student saveStudent(Student student) {
        return this.studentPersistentPort.saveStudent(student);
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        return this.studentPersistentPort.findStudentById(id)
                .map(studentDB -> {
                    studentDB.setFirstName(student.getFirstName());
                    studentDB.setLastName(student.getLastName());
                    studentDB.setAge(student.getAge());
                    studentDB.setAddress(student.getAddress());
                    return studentDB;
                })
                .map(this.studentPersistentPort::saveStudent)
                .orElseThrow(() -> new StudentNotFoundException("Error al actualizar estudiante. No se encuentra con id: %s".formatted(id)));
    }

    @Override
    public void deleteStudentById(Long id) {
        if (this.studentPersistentPort.findStudentById(id).isEmpty()) {
            throw new StudentNotFoundException("Error al eliminar estudiante. No se encuentra con id: %s".formatted(id));
        }
        this.studentPersistentPort.deleteStudentById(id);
    }
}
````