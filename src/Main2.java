import com.mns.todo.model.Student;

import java.util.*;
import java.util.stream.*;


/**
 * Demonstration des API Stream()
 */
public class Main2 {

    public static void main(String[] args) {
        List<Student> students = Stream
                .of("Ohara",
                        "Edouard",
                        "Steve",
                        "Tristan",
                        "Jonathan",
                        "Stanislas",
                        "Laurent",
                        "Tetiana",
                        "Axel",
                        "Victor",
                        "Lucas",
                        "François",
                        "Hélène",
                        "Romain",
                        "Xavier",
                        "Stéphane",
                        "Flavio")
                .map(Student::new)
                .toList();
        students.forEach(Student::setAverages);
        students.stream().map(student -> student.getName() + " : " + student.getTotalAverage()).forEach(System.out::println);

        boolean hasAnyStudentLessThan5 = students.stream()
                .map(Student::getTotalAverage)
                .noneMatch(mark -> mark < 5);


        System.out.println(students.stream().noneMatch(student -> student.getTotalAverage() < 5)); // Personne n'a moins de 5 si true
        System.out.println(students.stream().anyMatch(student -> student.getTotalAverage() > 10)); // Quelqu'un a plus de 10 si true

        System.out.println(
                students.stream()
                        .max(Comparator.comparingDouble(Student::getTotalAverage))
                        .orElse(null)
        ); // L'étudiant ayant la meilleure moyenne

        System.out.println(
                students.stream()
                        .mapToDouble(Student::getTotalAverage)
                        .average().orElse(0)
        ); // La moyenne de la classe

        System.out.println(
                students.stream()
                        .collect(Collectors
                                .toMap(Student::getName,
                                        Student::getMathsAverage)
                        )
        ); //Map name : Moyenne de maths.

        Map<String, Double> averages = new HashMap<>();
        for (Student student : students) {
            averages.put(student.getName(), student.getMathsAverage());
        }

    }
}