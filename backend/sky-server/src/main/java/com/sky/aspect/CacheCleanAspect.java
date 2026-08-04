package com.sky.aspect;

import com.sky.annotation.CacheClean;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 自定义切面，实现缓存自动清理
 * 在目标方法成功返回后，根据 @CacheClean 注解指定的 pattern 清理 Redis 缓存
 */
@Aspect
@Component
@Slf4j
public class CacheCleanAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 切入点：拦截所有带 @CacheClean 注解的方法
     */
    @Pointcut("@annotation(com.sky.annotation.CacheClean)")
    public void cacheCleanPointCut() {
    }

    /**
     * 后置返回通知：目标方法成功执行后，清理 Redis 缓存
     */
    @AfterReturning("cacheCleanPointCut()")
    public void cleanCache(JoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取 @CacheClean 注解
        CacheClean cacheClean = method.getAnnotation(CacheClean.class);

        // 解析 SpEL 表达式，得到实际的 Redis key 匹配模式
        String pattern = parseSpel(cacheClean.pattern(), method, joinPoint.getArgs());

        log.info("AOP缓存清理：pattern = {}", pattern);

        // 查找匹配的所有 key 并删除
        Set keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            Long deleted = redisTemplate.delete(keys);
            log.info("AOP缓存清理：共清理 {} 条缓存数据", deleted);
        } else {
            log.info("AOP缓存清理：没有匹配的缓存数据");
        }
    }

    /**
     * 解析 SpEL 表达式
     * 例如 pattern = "'dish_' + #dto.categoryId" 会将方法参数 dto.categoryId 的值拼入
     */
    private String parseSpel(String spel, Method method, Object[] args) {
        // 获取方法参数名
        LocalVariableTableParameterNameDiscoverer discoverer =
                new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);

        // 构造 SpEL 上下文
        EvaluationContext context = new StandardEvaluationContext();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        // 解析并返回
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(spel);
        return expression.getValue(context, String.class);
    }
}