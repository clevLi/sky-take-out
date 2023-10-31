package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 分页查询员工
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询分类");
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        Page<Category> categories = categoryMapper.query(categoryPageQueryDTO);
        long total = categories.getTotal();
        List<Category> result = categories.getResult();
        return new PageResult(total,result);
    }

    @Override
    public void startOrForbid(Integer status, Long id) {
        log.info("正在修改状态");
        Category category = categoryMapper.selectById(id);
        category.setStatus(status);
        categoryMapper.Update(category);
    }

    @Override
    public void Update(CategoryDTO categoryDTO) {
        log.info("正在修改菜品分类");
        Category category = categoryMapper.selectById(categoryDTO.getId());
        BeanUtils.copyProperties(categoryDTO,category);
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.Update(category);

    }

    @Override
    public void insert(CategoryDTO categoryDTO) {
        log.info("正在新增分类");
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(0);

        categoryMapper.insert(category);
    }

    @Override
    public void delete(Long id) {
        log.info("正在删除{}号分类",id);
        Integer dish = dishMapper.selectByCategory(id);
        if(dish>0) throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);

        Integer setmeal = setmealMapper.selectBycategoryId(id);
        if(setmeal>0) throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);

        categoryMapper.delete(id);
    }

    @Override
    public List<Category> list(Integer type) {
        log.info("查询{}分类",type);
        List<Category> category = categoryMapper.getCategory(type);
        return category;
    }
}
