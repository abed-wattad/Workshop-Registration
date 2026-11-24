package patmal.workshops.student.impl.model;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import java.util.ArrayList;
import java.util.List;

public final class StudentImpl extends PersonImpl implements Student {
    private TeamImpl team;

    public StudentImpl(String id, String name, String email) {
        super(id, name, email);
    }

    public TeamImpl getTeam() { return team; }
    public boolean isFree() { return team == null; }
    public void setTeam(TeamImpl team) { this.team = team; }

    @Override public String getTeamName() {
        return team == null ? null : team.getName();
    }

    @Override public Instructor getInstructor() {
        return team == null ? null : team.getWorkshop().getInstructor();
    }

    @Override public List<Student> getTeamMates() {
        List<Student> result = new ArrayList<>();
        if (team != null) {
            for (Student member : team.getStudents()) {
                if (member != this) result.add(member);
            }
        }
        return result;
    }
}
