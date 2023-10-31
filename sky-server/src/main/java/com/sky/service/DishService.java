package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DishService {

    void insertDish(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    void update(DishDTO dishDTO);

    DishVO getDishById(Long id);

    List<Dish> getDishByCategoryId(Integer categoryId);

    void modifyStatus(Integer status, Long id);

    List<DishVO> listWithFlavor(Dish dish);
}
