package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和功能口味
     *
     * @param dishDTO
     */
    public void saveWithFlavour(DishDTO dishDTO);

    /**
     * 新增菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 分组批量删除
     * @return
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品和对应的口味数据
     * @return
     */
    DishVO getByIdWithFlavor(long id);

    /**
     * 根据id修改菜品基本信息和对应的口味信息
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);
}

