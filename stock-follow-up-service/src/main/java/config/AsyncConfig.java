//package config;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.ThreadPoolExecutor;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import task.VisiableThreadPoolTaskExecutor;
//
//@Configuration
//public class AsyncConfig {
//
//    @Bean("stockTradeAsyncExecutor")
//    public Executor asyncExecutor(){
//        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
//        executor.setCorePoolSize(100);
//        executor.setMaxPoolSize(200);
//        executor.setQueueCapacity(400);
//        executor.setThreadNamePrefix("stock-trade-async-");
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        executor.initialize();
//        return executor;
//    }
//}
