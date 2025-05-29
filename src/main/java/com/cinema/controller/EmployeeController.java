package com.cinema.controller;

import com.cinema.dao.EmployeeDAO;
import com.cinema.model.Employee;

public class EmployeeController {
    private EmployeeDAO employeeDAO;

    public EmployeeController() {
        employeeDAO = new EmployeeDAO();
    }

    public Employee login(String username, String password) {
        return employeeDAO.login(username, password);
    }

    public boolean addEmployee(Employee employee) {
        return employeeDAO.addEmployee(employee);
    }

    public boolean updateEmployee(Employee employee) {
        return employeeDAO.updateEmployee(employee);
    }

    public boolean deleteEmployee(int id) {
        return employeeDAO.deleteEmployee(id);
    }

    public Employee getEmployeeById(int id) {
        return employeeDAO.getEmployeeById(id);
    }

    public java.util.List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }
} 