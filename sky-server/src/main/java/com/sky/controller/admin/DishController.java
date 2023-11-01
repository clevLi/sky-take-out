package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.ReturnNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.ResolutionSyntax;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品管理")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("新增菜品")
    @PostMapping()
    public Result addDish(@RequestBody DishDTO dishDTO){
        dishService.insertDish(dishDTO);
        //清理缓存数据
        String key = "dish_"+dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    @ApiOperation("分页查询菜品")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("删除菜品")
    @DeleteMapping()
    public Result delete(@RequestParam List<Long> ids){
        dishService.delete(ids);
        String key = "dish_"+"*";
        cleanCache(key);
        return Result.success();
    }

    @ApiOperation("修改菜品")
    @PutMapping()
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改的菜品是：{}",dishDTO);
        dishService.update(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }

    @ApiOperation("根据菜品id获取菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id ){

        DishVO dishVO = dishService.getDishById(id);
        return  Result.success(dishVO);
    }

    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result<List> getByCategoryId(Integer categoryId){
        List<Dish> dishes = dishService.getDishByCategoryId(categoryId);
        return Result.success(dishes);
    }

    @ApiOperation("修改菜品起售/禁售")
    @PostMapping("/status/{status}")
    public Result startOrForbid(@PathVariable Integer status, Long id){
        dishService.modifyStatus(status,id);
        cleanCache("dish_*");
        return Result.success();
    }
    /**
     * 删除redis全部缓存分类
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
