# Workshop Registration System — EX2 Maven Evolution

## Requirements

- JDK 25
- Apache Maven 3.9+
- Internet access for Maven Central dependencies

Run all commands from the repository root.

IntelliJ IDEA users can execute Maven commands using
Run Anything (press Ctrl twice).

## One-time local API installation

The supplied JAR files are stored in `lib/`.

Execute these commands once per computer:

```sh
mvn install:install-file -Dfile=lib/model-2.0.jar -DgroupId=patmal.workshops -DartifactId=model -Dversion=2.0 -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -Dfile=lib/logic-2.0.jar -DgroupId=patmal.workshops -DartifactId=logic -Dversion=2.0 -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -Dfile=lib/workshop-tester-2.0.jar -DgroupId=patmal.workshops -DartifactId=workshop-tester -Dversion=2.0 -Dpackaging=jar -DgeneratePom=true
```

## Build and verify

```sh
mvn clean install
```

To inspect dependencies:

```sh
mvn dependency:tree
```

The verify phase automatically runs the supplied tester
in a separate Java process. A nonzero exit code fails
the build.

The final executable JAR is:

`app/target/workshop-cli.jar`

## Run the application

```sh
java -jar app/target/workshop-cli.jar app/src/main/resources/students.csv
```

The application also supports running without arguments:

```sh
java -jar app/target/workshop-cli.jar
```

Without an argument, the application uses the bundled
demo CSV file.

The following files are generated in the working directory:

- instructors.json
- students.json
- teams.json

## Run the official tester manually

Windows:

```sh
java -cp "lib/workshop-tester-2.0.jar;app/target/workshop-cli.jar" patmal.workshops.main.Main
```

Linux/macOS: replace the semicolon with a colon.

## Project architecture

```text
root
|-- core
|   |-- model-impl
|   `-- logic-impl
`-- app
```

Root and core are both aggregators and parent POMs.
Dependency and plugin versions are managed centrally.

All implementation packages start with:

`patmal.workshops.student.impl`

## External libraries

- Log4j 2: application logging.
- Commons Validator: email validation.
- Gson: JSON serialization without circular references.
- Commons CSV: student CSV import.
- Maven Assembly Plugin: executable uber JAR.
- Exec Maven Plugin: automatic tester execution.

The tester has test scope and is excluded from the
executable uber JAR.

## Bonus tasks

1. Student CSV import.
2. Automatic tester execution during the Maven verify phase.

## Verification results

Verified using Java 25.0.4 and Maven 3.9.16.

- Maven modules: 5/5 successful.
- Official tester scenarios: 15/15 passed.
- Official tester checks: 201/201 passed.
- Failed checks: 0.
- Skipped scenarios: 0.
- Executable JAR: successfully executed.
- JSON exports: successfully generated.
- Automatic tester execution: verified.

RESULT: ALL OK

BUILD SUCCESS

## Submission tag

`EX2-Maven-Evolution`