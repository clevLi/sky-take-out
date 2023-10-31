package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> query(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 通过id查询
     * @param id
     * @return
     */
    @Select("select * from category where id = #{id};")
    Category selectById(Long id);


    /**
     * 修改
     * @param category
     */
    @AutoFill(value = OperationType.UPDATE)
    void Update(Category category);

    /**
     * 插入新菜品分类
     * @param category
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into category (type,name,sort,status,create_time,update_time,create_user,update_user)"
            +" values "
            +"(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Category category);

    @Delete("delete from category where id = #{id}")
    void delete(Long id);

    /**
     * 获取可用或不可用分类
     * @param type
     * @return
     */
    List<Category> getCategory(Integer type);
}
