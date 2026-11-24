package patmal.workshops.student.impl.app;

import patmal.workshops.student.impl.logic.WorkshopSystemImpl;
import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.*;
import org.apache.logging.log4j.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

public final class Main {
    private static final Logger LOG = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
        if (args.length > 1) throw new IllegalArgumentException("Usage: java -jar workshop-cli.jar [students.csv]");
        WorkshopSystem system = new WorkshopSystemImpl();
        Instructor instructor = system.createInstructor("i1", "Dana Levi", "dana@example.com");
        Workshop java = system.createWorkshop("Java", "Java programming");
        Workshop maven = system.createWorkshop("Maven", "Build tools");
        system.associateWorkshopWithInstructor(instructor, java);
        system.associateWorkshopWithInstructor(instructor, maven);
        List<Student> students;
        if (args.length == 1) {
            students = system.importStudentDataFromFile(new File(args[0]));
        } else {
            // Default demo also works when launched without an argument.
            Path temporary = Files.createTempFile("workshop-students-", ".csv");
            try (InputStream input = Main.class.getResourceAsStream("/students.csv")) {
                if (input == null) throw new IOException("Missing bundled students.csv");
                Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING);
                students = system.importStudentDataFromFile(temporary.toFile());
            } finally {
                Files.deleteIfExists(temporary);
            }
        }
        if (students.size() < 2) throw new IllegalArgumentException("Demo needs at least two students");
        system.createTeam("Alpha", java, List.of(students.get(0).getId()));
        system.exportInstructorsDataToJson(new File("instructors.json"));
        system.exportStudentsDataToJson(new File("students.json"));
        system.exportTeamsDataToJson(new File("teams.json"));
        LOG.info("Created demo with {} students; {} free", students.size(), system.getFreeStudents().size());
        LOG.info("Exported instructors.json, students.json and teams.json to {}", Path.of("").toAbsolutePath());
    }
}
