package com.sky.controller.admin;

import com.alibaba.druid.sql.PagerUtils;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@Api(tags = "套餐管理接口")
@Slf4j
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @ApiOperation("根据id查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @ApiOperation("新增套餐")
    @PostMapping()
    public Result insert(@RequestBody SetmealDTO setmealDTO){
        setmealService.insert(setmealDTO);
        return Result.success();
    }

    @ApiOperation("分页查询套餐")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("修改套餐")
    @PutMapping()
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @ApiOperation("套餐起售、停售")
    @PostMapping("/status/{status}")
    public Result startOdForbid(@PathVariable Integer status,Long id){
        setmealService.startOrForbid(status,id);
        return Result.success();
    }

    @ApiOperation("批量删除套餐")
    @DeleteMapping()
    public Result deleteBatch(@RequestParam List<Long> ids){
        setmealService.deleteBatch(ids);
        return Result.success();

    }
}
