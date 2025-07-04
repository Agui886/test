package annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
/**
 * 请求频繁限制，在控制上加@RequestLimit，会触发该方法
 */
public @interface RequestLimit {
	public long limitMillisecond() default 2000;
	
	public String errMsg() default "操作太频繁，请您稍等会儿";
}
