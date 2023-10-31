package com.sky.service;

import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 启用/禁用分类
     * @param status
     * @param id
     */


    void startOrForbid(Integer status,Long id);

    /**
     * 修改菜品分类
     * @param categoryDTO
     */

    void Update(CategoryDTO categoryDTO);

    /**
     * 新增分类
     * @param categoryDTO
     */
    void insert(CategoryDTO categoryDTO);

    /**
     * 根据id删除分类
     * @param id
     */
    void delete(Long id);

    /**
     * 根据类型获得分类
     * @param type
     */
    List<Category> list(Integer type);
}
