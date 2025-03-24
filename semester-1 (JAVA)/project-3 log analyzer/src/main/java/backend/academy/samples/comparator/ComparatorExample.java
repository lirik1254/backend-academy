package backend.academy.samples.comparator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ComparatorExample {
    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>();

        employees.add(new Employee(1010, "Rajeev", 100000.00, LocalDate.of(2010, 7, 10)));
        employees.add(new Employee(1004, "Chris", 95000.50, LocalDate.of(2017, 3, 19)));
        employees.add(new Employee(1015, "David", 134000.00, LocalDate.of(2017, 9, 28)));
        employees.add(new Employee(1009, "Steve", 100000.00, LocalDate.of(2016, 5, 18)));

        log.info("Employees : {}", employees);

        // Sort employees by Name
        employees.sort(Comparator.comparing(Employee::name));
        log.info("Employees (Sorted by Name) : {}", employees);

        // Sort employees by Salary
        employees.sort(Comparator.comparingDouble(Employee::salary));
        log.info("Employees (Sorted by Salary) : {}", employees);

        // Sort employees by JoiningDate
        employees.sort(Comparator.comparing(Employee::joiningDate));
        log.info("Employees (Sorted by JoiningDate) : {}", employees);

        // Sort employees by Name in descending order
        employees.sort(Comparator.comparing(Employee::name).reversed());
        log.info("Employees (Sorted by Name in descending order) : {}", employees);

        // Chaining multiple Comparators
        // Sort by Salary. If Salary is same then sort by Name
        employees.sort(Comparator.<Employee>comparingDouble(Employee::salary).thenComparing(Employee::name));
        log.info("Employees (Sorted by Salary and Name) : {}", employees);
    }
}
