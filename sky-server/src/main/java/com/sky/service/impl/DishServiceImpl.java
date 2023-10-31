package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.FlavorMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private FlavorMapper flavorMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional
    public void insertDish(DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors==null||flavors.size()==0) return;
        Long dishId = dish.getId();
        for(DishFlavor flavor : flavors){
            flavor.setDishId(dishId);
        }
        flavorMapper.insertBatch(flavors);

    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("正在分页查询菜品:{}",dishPageQueryDTO);
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> dishes = dishMapper.get(dishPageQueryDTO);

        long total = dishes.getTotal();
        List<DishVO> result = dishes.getResult();
        return new PageResult(total,result);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        log.info("正在删除菜品");
        // 是否关联套餐
        for(long id :ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus()== StatusConstant.ENABLE)
            {
                log.error(MessageConstant.DISH_ON_SALE);
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        Integer setmealCount = setmealMapper.getCountByDishId(ids);
        if(setmealCount > 0)
        {
            log.error(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        dishMapper.deleteBatch(ids);
        flavorMapper.deleteBatch(ids);

    }

    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        log.info("更新菜品：{}",dishDTO);
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors ==null|| flavors.size()==0) return;
       //删除与菜品关联的原口味记录
        flavorMapper.deleteByDish(dish.getId());
        flavors.forEach(dishFlavor -> {dishFlavor.setDishId(dish.getId());});
        flavorMapper.insertBatch(flavors);
    }

    @Override
    public DishVO getDishById(Long id) {
        log.info("根据菜品id:{}查询菜品",id);
        DishVO dishvo = dishMapper.getByDishIdWithCategory(id);
        List<DishFlavor> flavors = flavorMapper.getByDishID(id);
        if(flavors!=null&&flavors.size()!=0)
            dishvo.setFlavors(flavors);
        return dishvo;
    }

    @Override
    public List<Dish> getDishByCategoryId(Integer categoryId) {
        log.info("根据分类id:{}查询菜品",categoryId);
        List<Dish> dishes = dishMapper.getByCategoryId(categoryId);
        return dishes;

    }

    @Override
    public void modifyStatus(Integer status, Long id) {
        log.info("修改菜品状态为:{}",status);
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
