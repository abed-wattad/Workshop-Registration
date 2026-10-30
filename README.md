# Workshop Registration System — EX1 Vanilla

Plain Java implementation of the instructor, student, workshop and team APIs supplied in the course's `model-1.0.jar` and `logic-1.0.jar`. No Maven, Gradle, or frameworks.

## Requirements

**Java JDK 25 is required to run the original supplied API and tester JARs.** The implementation source and packaged implementation JAR also compile under Java 21, but the supplied JARs have Java 25 bytecode and will not run unchanged on Java 21. Use JDK 25 in IntelliJ and for final verification.

## Build (Windows PowerShell)

From this project's root folder:

```powershell
New-Item -ItemType Directory -Force out, artifacts | Out-Null
javac -cp 'lib/model-1.0.jar;lib/logic-1.0.jar' -d out (Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName)
jar cf artifacts/workshop-implementation.jar -C out .
java -cp 'lib/workshop-tester.jar;lib/logic-1.0.jar;lib/model-1.0.jar;artifacts/workshop-implementation.jar' patmal.workshops.main.Main
```

On Linux/macOS, substitute `:` for `;` in the classpath and compile using:

```bash
mkdir -p out artifacts
javac -cp 'lib/model-1.0.jar:lib/logic-1.0.jar' -d out $(find src -name '*.java')
jar cf artifacts/workshop-implementation.jar -C out .
java -cp 'lib/workshop-tester.jar:lib/logic-1.0.jar:lib/model-1.0.jar:artifacts/workshop-implementation.jar' patmal.workshops.main.Main
```

## IntelliJ

1. Open this directory as a Java project with **JDK 25**.
2. Mark `src` as Sources Root and add the two API jars in `lib` as dependencies.
3. Use `patmal.workshops.main.Main` from `workshop-tester.jar` as the test main class, with the tester and your compiled classes on the classpath.

## Design

- `WorkshopSystemImpl` is the single public, no-argument system implementation.
- `InstructorImpl`, `StudentImpl`, `WorkshopImpl`, `TeamImpl`, and `PersonImpl` implement the data model.
- Maps preserve creation order, and workshop/team IDs begin at 1.
- `createTeam` validates every member before changing any state or consuming an ID.
- Unknown object IDs return `null`; unknown aggregate query IDs return empty lists.
- All returned lists are defensive copies.

**Verification:** All 105 checks across 9 scenarios passed using a local Java 21 compatibility test in which ONLY the major class-file version of temporary copies of the provided JARs was adjusted to 21. The test copies are not included in this deliverable; the original supplied JARs remain untouched. Since Java 25 is not installed here, the original Java 25 tester run could not be performed. See `tester-output.txt` for the local test report.

**Note:** The compiled API jars use packages `patmal.workshops.api.model` and `patmal.workshops.api.logic`. These differ from the package names printed in part of the assignment document; the code uses the real jar package names.

## Submission

Push this project's source and root-level `artifacts/` directory to a **public** GitHub repository. Tag the final commit exactly `EX1-Vanilla` and push that tag. Submit a plain text file containing only your full repository URL. Submit a `.txt` file containing only the real URL of your repository.
