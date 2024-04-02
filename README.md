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

---

# INFRASTRUCTURE

---

En esta capa vamos a definir los elementos de salida y entrada a nuestra aplicación. Empezaremos creando las entidades,
mismos que serán usados para poder almacenar los datos en la base de datos.

## Entidades

````java

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String address;
}
````

## Repositorio

Crearemos la interfaz de repositorio que al extender de la interfaz de Spring Boot Data JPA, nos permitirá interactuar
con la entidad `StudentEntity` y la base de datos:

````java
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
}
````

## Mapeando: dominio - entidad

Recordemos que en el `pom.xml` hemos agregado la dependencia del `mapstruct`, esta dependencia nos permitirá establecer
el mapeo entre el dominio y las clases de entidad. Así que ahora necesitamos crear una interfaz al que le
agregaremos la anotación `@Mapper(componentModel = "spring")`, esta anotación nos permite decirle a spring que inyecte
esta interfaz como un componente de spring.

````java

@Mapper(componentModel = "spring") // Permite que esta interfaz se inyecte como un componente de Spring
public interface StudentPersistentMapper {

    // Convertirá un Student en un StudentEntity. Como ambas clases tienen los mismos atributos, no es necesario
    // agregar anotaciones adicionales. Si tuvieran campos distintos podríamos usar por ejemplo:
    // @Mapping(target = "age", source = "edad")
    StudentEntity toStudentEntity(Student student);

    Student toStudent(StudentEntity entity);

    List<Student> toStudentList(List<StudentEntity> studentEntityList);
}
````

## Implementación del puerto de salida (output) de la capa de aplicación

En la capa de aplicación definimos una interfaz como puerto de salida (output) llamada `StudentPersistentPort`. Esta
interfaz está siendo inyectada la clase `StudentService` de la misma capa de aplicación, eso significa que, esa clase
está esperando una implementación concreta de la interfaz `StudentPersistentPort`, es por eso que ahora en esta capa de
aplicación realizamos la implementación concreta de dicha interfaz:

````java

@RequiredArgsConstructor
@Service
public class StudentPersistentAdapter implements StudentPersistentPort {

    private final StudentRepository studentRepository;
    private final StudentPersistentMapper studentPersistentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAllStudents() {
        List<StudentEntity> studentEntityList = this.studentRepository.findAll();
        return this.studentPersistentMapper.toStudentList(studentEntityList);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findStudentById(Long id) {
        return this.studentRepository.findById(id)
                .map(this.studentPersistentMapper::toStudent);
    }

    @Override
    @Transactional
    public Student saveStudent(Student student) {
        StudentEntity studentEntity = this.studentPersistentMapper.toStudentEntity(student);
        StudentEntity studentSaved = this.studentRepository.save(studentEntity);
        return this.studentPersistentMapper.toStudent(studentSaved);
    }

    @Override
    @Transactional
    public void deleteStudentById(Long id) {
        this.studentRepository.deleteById(id);
    }
}
````

## Modelo de request

Crearemos un `DTO` que nos permitirá mapear la información enviada desde el cliente hacia en el endpoint rest. Este
dto se usará tanto en el endpoint para guardar y actualizar.

Notar que en este `DTO` estamos estableciendo las anotaciones de validación. Estas anotaciones se activarán cuando
en el parámetro del método que use el dto usemos la anotación `@Valid`.

````java

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentCreateRequest {
    @NotBlank(message = "El campo firstName no puede estar vacío o ser nulo")
    private String firstName;

    @NotBlank(message = "El campo lastName no puede estar vacío o ser nulo")
    private String lastName;

    @NotNull(message = "El campo age no puede ser nulo")
    private Integer age;

    @NotBlank(message = "El campo address no puede estar vacío o ser nulo")
    private String address;
}
````

## Modelo de response

Este `DTO` se usará para enviar información desde el backend hacia el cliente http.

````java

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String address;
}
````

## Mapeando: dominio - dto

`unmappedTargetPolicy = ReportingPolicy.IGNORE`: Esta configuración indica a `MapStruct` que ignore las propiedades
de destino que no están mapeadas explícitamente en el mapper. Esto evita que se generen advertencias en tiempo de
compilación por propiedades no mapeadas.

Tomemos como ejemplo el siguiente método:

````java
Student toStudent(StudentCreateRequest request);
````

Si en el campo de destino `(Student)` existe un atributo que no hay en clase de origen `(StudentCreateRequest)`, con la
configuración `unmappedTargetPolicy = ReportingPolicy.IGNORE`, ignoramos que se haga el mapeo de ese campo de destino.

En nuestro ejemplo, el modelo de dominio `Student` tiene un atributo llamado `id` que no tiene la clase de origen
`StudentCreateRequest`, por lo tanto, con la configuración anterior, **el mapeo de ese campo se va a ignorar.**

````java

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentRestMapper {
    Student toStudent(StudentCreateRequest request);

    StudentResponse toStudentResponse(Student student);

    List<StudentResponse> toStudentResponseList(List<Student> students);
}
````

## Adapter Input Rest: StudentRestAdapter

En esta clase definimos los endpoints de nuestros estudiantes. Observar que en los endpoints estamos regresando siempre
el dto `StudentResponse` que hemos creado, pero podríamos usar el propio dominio `Student`, eso va a depender de cómo
queremos manejarlo y qué información queremos exponer. Sin embargo, por ningún motivo debemos enviar como respuesta las
entidades de la aplicación `StudentEntity`, ni mucho menos usarlos como parámetros de los métodos de los endpoints.

````java

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/students")
public class StudentRestAdapter {

    private final StudentServicePort studentServicePort;
    private final StudentRestMapper studentRestMapper;

    @GetMapping
    public ResponseEntity<List<StudentResponse>> findAllStudents() {
        List<StudentResponse> studentResponses = this.studentRestMapper.toStudentResponseList(this.studentServicePort.findAllStudents());
        return ResponseEntity.ok(studentResponses);
    }

    @GetMapping(path = "/{studentId}")
    public ResponseEntity<StudentResponse> findStudent(@PathVariable Long studentId) {
        Student student = this.studentServicePort.findStudentById(studentId);
        return ResponseEntity.ok(this.studentRestMapper.toStudentResponse(student));
    }

    @PostMapping
    public ResponseEntity<StudentResponse> saveStudent(@Valid @RequestBody StudentCreateRequest studentCreateRequest) {
        Student student = this.studentRestMapper.toStudent(studentCreateRequest);
        Student studentDB = this.studentServicePort.saveStudent(student);
        URI uri = URI.create("/api/v1/students/" + studentDB.getId());
        return ResponseEntity.created(uri).body(this.studentRestMapper.toStudentResponse(studentDB));
    }

    /**
     * Para este caso particular el dto StudentCreateRequest, será el mismo que usamos en el endpoint saveStudent().
     * Sin embargo, podríamos crear un dto exclusivo para este updateStudent(), 
     * todo va a depender de qué campos consideramos deben ser actualizables.
     */
    @PutMapping(path = "/{studentId}")
    public ResponseEntity<StudentResponse> updateStudent(@Valid @RequestBody StudentCreateRequest studentCreateRequest, @PathVariable Long studentId) {
        Student student = this.studentRestMapper.toStudent(studentCreateRequest);
        Student studentDB = this.studentServicePort.updateStudent(studentId, student);
        return ResponseEntity.ok(this.studentRestMapper.toStudentResponse(studentDB));
    }

    @DeleteMapping(path = "/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId) {
        this.studentServicePort.deleteStudentById(studentId);
        return ResponseEntity.noContent().build();
    }
}
````

