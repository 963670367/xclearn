package com.xuecheng.order.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.Task;

import java.util.concurrent.Executor;

// 异步任务配置类
@Configuration
@EnableScheduling
public class AsyncTaskConfig implements SchedulingConfigurer, AsyncConfigurer {

    // 线程池线程容量
    private int corePoolSize=5;
    @Bean
    public ThreadPoolTaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize(); // 初始化线程池
        scheduler.setPoolSize(corePoolSize);
        return scheduler;
    }

    /**
     * The {@link Executor} instance to be used when processing async
     * method invocations.
     */
    @Override
    public Executor getAsyncExecutor() {
        Executor executor = taskScheduler();
        return executor;
    }

    /**
     * The {@link AsyncUncaughtExceptionHandler} instance to be used
     * when an exception is thrown during an asynchronous method execution
     * with {@code void} return type.
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

    /**
     * Callback allowing a {@link TaskScheduler
     * TaskScheduler} and specific {@link Task Task}
     * instances to be registered against the given the {@link ScheduledTaskRegistrar}
     *
     * @param taskRegistrar the registrar to be configured.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
    }
}
