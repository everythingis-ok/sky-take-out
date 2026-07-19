package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {

    /**
     * 新增菜品和功能口味
     * @param dishDTO
     */
    public void saveWithFlavour(DishDTO dishDTO);
}
