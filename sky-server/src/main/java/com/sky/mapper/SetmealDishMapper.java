package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据套餐id选择套餐菜品关系
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    public List<SetmealDish> getBySetmealId(long id);

    /**
     * 批量插入套餐菜品关联
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);
}
