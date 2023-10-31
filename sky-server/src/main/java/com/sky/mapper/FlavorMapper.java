package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FlavorMapper {



    void insertBatch(List<DishFlavor> flavors);

    /**
     * 通过菜品ids批量删除flavor
     * @param dishIds
     */
    void deleteBatch(List<Long> dishIds);

    /**
     * 根据菜品id删除口味
     * @param id
     */
    void deleteByDish(Long id);

    /**
     * 根据菜品id获取口味
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishID(Long id);
}
