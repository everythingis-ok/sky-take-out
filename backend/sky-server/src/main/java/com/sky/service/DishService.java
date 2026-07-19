package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
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
}

