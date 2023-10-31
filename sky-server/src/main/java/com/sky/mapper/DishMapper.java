package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id选择菜品的数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer selectByCategory(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 返回菜品信息+分类名称
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> get(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id = #{id}")
    Dish getById(long id);

    /**
     * 通过ids批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 修改菜品
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据菜品id获取dishVO
     * @param id
     * @return
     */
    DishVO getByDishIdWithCategory(long id);

    /**
     * 根据分类id获取菜品信息
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> getByCategoryId(Integer categoryId);

    /**
     * 根据id获取起售的菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id} and status = 1")
    Dish getOnSaleDishById(Long id);

    /**
     * 动态条件查询菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);
}
