package com.sky.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    @ApiModelProperty("员工账号")
    private String username;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;

}
