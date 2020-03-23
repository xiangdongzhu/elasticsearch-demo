package com.zxd.elasticsearch.elasticsearchdemo.controller;

import com.zxd.elasticsearch.elasticsearchdemo.domain.Employee;
import com.zxd.elasticsearch.elasticsearchdemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("/addEmployee")
    public String addEmployee(@RequestBody  Employee emp){
        return employeeService.addEmployee(emp);
    }

    @RequestMapping("/searchEmployee")
    public String search(@RequestBody  Employee emp){
        return employeeService.boolQuery(emp);
    }

    @RequestMapping("/aggSearch")
    public String aggSearch(){
        try {
            return employeeService.searchAggre();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
