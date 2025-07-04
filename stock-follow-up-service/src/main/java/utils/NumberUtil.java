package utils;

import cn.hutool.core.util.RandomUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: lottery
 * @description:
 * @author: Jesson
 * @create: 2021-09-07 17:46
 **/
public class NumberUtil {

    /**
     * 校验字符串是否为数字
     *
     * @author Jesson
     * @param: str
     * @updateTime 2021/9/7 17:47
     * @return: boolean
     * @throws
     */
    public static boolean isNumber(String str) {
        Pattern compile = Pattern.compile("^-?\\d+(\\.\\d+)?$");
        Matcher isnum = compile.matcher(str);
        if(!isnum.matches()){
            return false;
        }
        return true;
    }


    public static String repeat(int min, int max, int size){

        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            integers.add(RandomUtil.randomInt(min, max));
        }

        Collections.sort(integers);

        StringJoiner stringJoiner = new StringJoiner(" ");
        integers.forEach(one->{
            stringJoiner.add(one + "");
        });
        return stringJoiner.toString();
    }
}
