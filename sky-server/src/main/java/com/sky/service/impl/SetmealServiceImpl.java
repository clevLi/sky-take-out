package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    public SetmealVO getById(long id) {
        log.info("使用套餐id:{}查询套餐",id);
        SetmealVO setmealWithCategory = setmealMapper.getSetmealWithCategoryById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        setmealWithCategory.setSetmealDishes(setmealDishes);
        return setmealWithCategory;
    }

    @Override
    @Transactional
    public void insert(SetmealDTO setmealDTO) {
        log.info("插入套餐：{}",setmealDTO);
        //获取菜品信息，如果没有起售菜品不能插入
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for(SetmealDish setmealDish : setmealDishes){
            Dish dishOnSale = dishMapper.getOnSaleDishById(setmealDish.getDishId());
            if(dishOnSale==null) throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);

        }
        //插入套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        Setmeal byName = setmealMapper.getByName(setmeal.getName());
        if(byName!=null) throw new SetmealEnableFailedException(MessageConstant.SETMEAL_NAME_ALREADY_EXITS);

        setmealMapper.insert(setmeal);

        for(SetmealDish setmealDish : setmealDishes){
            setmealDish.setSetmealId(setmeal.getId());
        }
        //插入套餐菜品关联
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐");
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> setmealVOS = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(setmealVOS.getTotal(),setmealVOS.getResult());
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        // 修改套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        // 根据套餐id查询关联关系并删除
        ArrayList<Long> longs = new ArrayList<>();
        longs.add(setmealDTO.getId());
        setmealDishMapper.deleteBatch(longs);
        //加入新的关联关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDTO.getId());
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public void startOrForbid(Integer status, Long id) {
        log.info("更改套餐起售、停售");
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        log.info("批量删除套餐");
        //删除套餐菜品关联
        ids.forEach(id->{
            SetmealVO setmealWithCategoryById = setmealMapper.getSetmealWithCategoryById(id);
            if(setmealWithCategoryById.getStatus()==1) throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        });
        setmealDishMapper.deleteBatch(ids);

        setmealMapper.deleteBatch(ids);
        //删除套餐
    }
    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

}
