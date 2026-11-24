package patmal.workshops.student.impl.logic;

import patmal.workshops.student.impl.model.StudentImpl;
import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.*;
import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/** Explicit JSON trees avoid circular references in the model. */
final class JsonExporter {
    private static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    private static Map<String, Object> person(Person person) {
        if (person == null) return null;
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", person.getId());
        data.put("name", person.getName());
        data.put("email", person.getEmail());
        return data;
    }
    private static Map<String, Object> workshop(Workshop workshop, boolean includeInstructor) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", workshop.getId());
        data.put("name", workshop.getName());
        data.put("description", workshop.getDescription());
        if (includeInstructor) data.put("instructor", person(workshop.getInstructor()));
        return data;
    }
    private static Map<String, Object> team(Team team, boolean includeWorkshop, boolean idsOnly) {
        if (team == null) return null;
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", team.getId());
        data.put("name", team.getName());
        List<Object> members = new ArrayList<>();
        for (Student student : team.getStudents()) {
            members.add(idsOnly ? student.getId() : person(student));
        }
        data.put(idsOnly ? "studentIds" : "students", members);
        if (includeWorkshop) data.put("workshop", workshop(team.getWorkshop(), true));
        return data;
    }
    static void instructors(WorkshopSystem system, File file) {
        List<Object> result = new ArrayList<>();
        for (Instructor instructor : system.getInstructors()) {
            Map<String, Object> data = person(instructor);
            List<Object> workshops = new ArrayList<>();
            for (Workshop workshop : system.getWorkshops()) {
                if (workshop.getInstructor() != instructor) continue;
                Map<String, Object> item = workshop(workshop, false);
                List<Object> teams = new ArrayList<>();
                for (Team team : system.getTeams()) {
                    if (team.getWorkshop() == workshop) teams.add(team(team, false, false));
                }
                item.put("teams", teams);
                workshops.add(item);
            }
            data.put("workshops", workshops);
            result.add(data);
        }
        write(file, result);
    }
    static void students(WorkshopSystem system, File file) {
        List<Object> result = new ArrayList<>();
        for (Student student : system.getStudents()) {
            Map<String, Object> data = person(student);
            data.put("team", team(((StudentImpl) student).getTeam(), true, true));
            result.add(data);
        }
        write(file, result);
    }
    static void teams(WorkshopSystem system, File file) {
        List<Object> result = new ArrayList<>();
        for (Team team : system.getTeams()) result.add(team(team, true, false));
        write(file, result);
    }
    private static void write(File file, List<Object> data) {
        try {
            Files.writeString(file.toPath(), GSON.toJson(data), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
