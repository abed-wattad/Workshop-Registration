package patmal.workshops.impl;

import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * In-memory workshop registry.
 * Unknown single-object ids return null; unknown aggregate ids return an empty list.
 * Rejected mutations throw IllegalArgumentException without changing the registry.
 */
public final class WorkshopSystemImpl implements WorkshopSystem {
    private final Map<String, InstructorImpl> instructors = new LinkedHashMap<>();
    private final Map<String, StudentImpl> students = new LinkedHashMap<>();
    private final Map<Integer, WorkshopImpl> workshops = new LinkedHashMap<>();
    private final Map<Integer, TeamImpl> teams = new LinkedHashMap<>();
    private int nextWorkshopId = 1;
    private int nextTeamId = 1;

    public WorkshopSystemImpl() { }

    @Override public Instructor createInstructor(String id, String name, String email) {
        if (instructors.containsKey(id)) throw new IllegalArgumentException("Duplicate instructor id: " + id);
        InstructorImpl instructor = new InstructorImpl(id, name, email);
        instructors.put(id, instructor);
        return instructor;
    }

    @Override public Student createStudent(String id, String name, String email) {
        if (students.containsKey(id)) throw new IllegalArgumentException("Duplicate student id: " + id);
        StudentImpl student = new StudentImpl(id, name, email);
        students.put(id, student);
        return student;
    }

    @Override public Workshop createWorkshop(String name, String description) {
        WorkshopImpl workshop = new WorkshopImpl(nextWorkshopId, name, description);
        workshops.put(nextWorkshopId, workshop);
        nextWorkshopId++;
        return workshop;
    }

    @Override public void associateWorkshopWithInstructor(Instructor instructor, Workshop workshop) {
        // Ensure references actually belong to this system.
        if (!(instructor instanceof InstructorImpl) ||
            instructors.get(instructor.getId()) != instructor ||
            !(workshop instanceof WorkshopImpl) ||
            workshops.get(workshop.getId()) != workshop) {
            throw new IllegalArgumentException("Unknown instructor or workshop");
        }
        InstructorImpl newInstructor = (InstructorImpl) instructor;
        WorkshopImpl target = (WorkshopImpl) workshop;
        Instructor previous = target.getInstructor();
        if (previous == newInstructor) return;
        if (previous != null) ((InstructorImpl) previous).removeWorkshop(target);
        target.setInstructor(newInstructor);
        newInstructor.addWorkshop(target);
    }

    @Override public Team createTeam(String name, Workshop workshop, List<String> members) {
        if (!(workshop instanceof WorkshopImpl) || workshops.get(workshop.getId()) != workshop) {
            throw new IllegalArgumentException("Unknown workshop");
        }
        if (members == null) throw new IllegalArgumentException("Null member list");

        // Validate the entire team before changing students, teams, or the id counter.
        List<Student> chosen = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String memberId : members) {
            if (!seen.add(memberId)) throw new IllegalArgumentException("Duplicate team member: " + memberId);
            StudentImpl student = students.get(memberId);
            if (student == null) throw new IllegalArgumentException("Unknown student: " + memberId);
            if (!student.isFree()) throw new IllegalArgumentException("Student already in team: " + memberId);
            chosen.add(student);
        }

        TeamImpl team = new TeamImpl(nextTeamId, name, workshop, chosen);
        for (Student student : chosen) ((StudentImpl) student).setTeam(team);
        teams.put(nextTeamId, team);
        nextTeamId++;
        return team;
    }

    @Override public List<Instructor> getInstructors() { return new ArrayList<>(instructors.values()); }
    @Override public Instructor getInstructor(String id) { return instructors.get(id); }
    @Override public List<Student> getStudents() { return new ArrayList<>(students.values()); }
    @Override public Student getStudent(String id) { return students.get(id); }
    @Override public List<Workshop> getWorkshops() { return new ArrayList<>(workshops.values()); }
    @Override public Workshop getWorkshop(Integer id) { return workshops.get(id); }
    @Override public List<Team> getTeams() { return new ArrayList<>(teams.values()); }
    @Override public Team getTeam(Integer id) { return teams.get(id); }

    @Override public List<Student> getFreeStudents() {
        List<Student> result = new ArrayList<>();
        for (StudentImpl student : students.values()) {
            if (student.isFree()) result.add(student);
        }
        return result;
    }

    @Override public List<Student> getStudentsOfWorkshop(Integer workshopId) {
        if (!workshops.containsKey(workshopId)) return new ArrayList<>();
        List<Student> result = new ArrayList<>();
        for (TeamImpl team : teams.values()) {
            if (team.getWorkshop().getId().equals(workshopId)) result.addAll(team.getStudents());
        }
        return result;
    }

    @Override public List<Student> getStudentsOfInstructor(String instructorId) {
        InstructorImpl instructor = instructors.get(instructorId);
        if (instructor == null) return new ArrayList<>();
        List<Student> result = new ArrayList<>();
        for (Workshop workshop : instructor.getWorkshops()) {
            result.addAll(getStudentsOfWorkshop(workshop.getId()));
        }
        return result;
    }
}
