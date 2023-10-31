package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "用户相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation("员工登陆")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);
        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }


    @PostMapping()
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}",employeeDTO.getUsername());
        employeeService.insert(employeeDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询员工")
    public Result<PageResult> employeePage(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数为:{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用员工")
    public Result startOrForbid(@PathVariable Integer status,Long id){
        String flag = "禁用";
        if(status==1) flag = "启用";
        log.info("{}员工账号:{}",flag,id);
        employeeService.startOrForbid(status,id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工信息")
    public Result<Employee> queryEmployeeById(@PathVariable Long id){
        log.info("根据id:{}查询员工信息",id);
        Employee employee = employeeService.queryById(id);
        return Result.success(employee);
    }

    @PutMapping()
    @ApiOperation("编辑员工信息")
    public Result updateInfo(@RequestBody EmployeeDTO employeeDTO){
        log.info("修改员工：{}的信息",employeeDTO.getId());
        employeeService.update(employeeDTO);
        return  Result.success();
    }

    @PutMapping("/editPassword")
    @ApiOperation("修改密码")
    public Result pwdEdit(@RequestBody PasswordEditDTO passwordEditDTO){

        //TODO 找到为什么empid传入失败
        passwordEditDTO.setEmpId(BaseContext.getCurrentId());
        log.info("员工:{}正在修改密码",passwordEditDTO.getEmpId());
        employeeService.passwordEdit(passwordEditDTO);
        return Result.success();
    }



}
