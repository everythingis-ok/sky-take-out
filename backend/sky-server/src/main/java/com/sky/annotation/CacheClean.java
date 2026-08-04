package com.sky.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标记方法执行后需要清理 Redis 缓存
 * pattern 支持 SpEL 表达式，可以引用方法参数动态构造缓存 key
 *
 * 示例：
 *   @CacheClean(pattern = "'dish_*'")                     // 静态模式，删除所有 dish_ 开头的 key
 *   @CacheClean(pattern = "'dish_' + #dto.categoryId")    // 动态模式，删除指定分类的缓存
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheClean {

    /**
     * Redis key 匹配模式，支持 SpEL 表达式
     */
    String pattern();
}