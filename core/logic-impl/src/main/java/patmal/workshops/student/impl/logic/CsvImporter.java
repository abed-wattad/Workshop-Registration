package patmal.workshops.student.impl.logic;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Student;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

final class CsvImporter {
    static List<Student> read(WorkshopSystem system, File source) {
        List<Student> result = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(source.toPath(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
            Iterator<CSVRecord> rows = parser.iterator();
            if (!rows.hasNext()) throw new IllegalArgumentException("Missing CSV header");
            CSVRecord header = rows.next();
            if (header.size() != 3 || !header.get(0).equals("id") ||
                !header.get(1).equals("name") || !header.get(2).equals("email")) {
                throw new IllegalArgumentException("Expected id,name,email header");
            }
            while (rows.hasNext()) {
                CSVRecord row = rows.next();
                if (row.size() != 3) throw new IllegalArgumentException("Expected three CSV values");
                result.add(system.createStudent(row.get(0), row.get(1), row.get(2)));
            }
            return result;
        } catch (UncheckedIOException e) {
            if (e.getCause() instanceof org.apache.commons.csv.CSVException)
                throw new IllegalArgumentException("Invalid CSV syntax", e);
            throw e;
        } catch (org.apache.commons.csv.CSVException e) {
            throw new IllegalArgumentException("Invalid CSV syntax", e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
