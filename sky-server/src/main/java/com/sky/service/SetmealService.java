package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    public SetmealVO getById(long id);

    void insert(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void update(SetmealDTO setmealDTO);

    void startOrForbid(Integer status, Long id);

    void deleteBatch(List<Long> ids);

    List<Setmeal> list(Setmeal setmeal);

    List<DishItemVO> getDishItemById(Long id);
}
