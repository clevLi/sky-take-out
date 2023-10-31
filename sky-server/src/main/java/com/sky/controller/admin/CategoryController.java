package com.sky.controller.admin;

import com.alibaba.druid.sql.PagerUtils;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.JstlUtils;

import java.util.List;

@Api(tags = "分类管理")
@RestController
@RequestMapping("/admin/category")
@Slf4j

public class CategoryController {

    @Autowired
     private CategoryService categoryService;

    @ApiOperation("分页查询分类")
    @GetMapping("/page")
    public Result<PageResult> pageCategroy(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询分类");
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("启用禁用分类")
    @PostMapping("/status/{status}")
    public Result startOrForbid(@PathVariable String status,String id){

        long categoryID = Long.parseLong(id);
        int statu = Integer.parseInt(status);
        categoryService.startOrForbid(statu,categoryID);
        return Result.success();
    }

    @ApiOperation("修改分类信息")
    @PutMapping()
    public Result Update(@RequestBody CategoryDTO categoryDTO){
        categoryService.Update(categoryDTO);
        return Result.success();
    }

    @ApiOperation("新增分类")
    @PostMapping()
    public Result insert(@RequestBody CategoryDTO categoryDTO){
        categoryService.insert(categoryDTO);
        return Result.success();
    }

    @ApiOperation("根据id删除分类")
    @DeleteMapping()
    public Result deleteCategory(String id){
        long categoryId = Long.parseLong(id);
        categoryService.delete(categoryId);
        return Result.success();
    }

    @ApiOperation("根据类型返回分类")
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

}
