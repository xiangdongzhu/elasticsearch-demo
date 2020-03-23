package com.zxd.elasticsearch.elasticsearchdemo.domain;

import lombok.Data;

@Data
public class Employee {
    private String name;
    private Integer age;
    private Integer gtage;
    private Integer ltage;
    private String position;
    private String country;
    private String join_date;
    private String salary;
}
