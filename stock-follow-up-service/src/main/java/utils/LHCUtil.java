package utils;

import cn.hutool.core.math.Combination;
import config.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class LHCUtil {

    private static List<String> generaList(int min, int max, int group, String options) {

        String[] split = options.split(",");
        if (split.length >= min && split.length <= max) {

            return generaList2(group, options);

        } else {
            throw new ServiceException("数据格式错误");
        }
    }

    private static List<String> generaList2(int group, String options) {
        String[] split = options.split(",");
        Combination combination = new Combination(split);
        List<String[]> rs = combination.select(group);//这个是待组合个数

        ArrayList<String> strings = new ArrayList<>();
        rs.stream().forEach(one -> {
            StringJoiner stringJoiner = new StringJoiner(",");
            for (String s : one) {
                stringJoiner.add(s);
            }
            strings.add(stringJoiner.toString());
        });


        return strings;
    }


    public static List<String> method(int itemDefId, String option) {

        List<String> strings = new ArrayList<>();
        switch (itemDefId) {

            // 三中二
            case 1040:
                strings = LHCUtil.generaList(3, 10, 3, option);
                break;
            // 二中特
            case 1042:
                strings = LHCUtil.generaList(2, 10, 2, option);
                break;
            case 1044:
                strings = LHCUtil.generaList(3, 10, 3, option);
                break;
            // 二全中
            case 1045:
                strings = LHCUtil.generaList(2, 10, 2, option);
                break;
            // 特串
            case 1046:
                strings = LHCUtil.generaList(2, 10, 2, option);
                break;
            // 四中一
            case 1047:
                strings = LHCUtil.generaList(4, 10, 4, option);
                break;
            // 二连肖
            case 1101:
                strings = LHCUtil.generaList(2, 8, 2, option);
                break;
            // 三连肖
            case 1103:
                strings = LHCUtil.generaList(3, 8, 3, option);
                break;
            // 四连肖
            case 1105:
                strings = LHCUtil.generaList(4, 8, 4, option);
                break;
            // 二连尾
            case 1107:
                strings = LHCUtil.generaList(2, 8, 2, option);
                break;
            // 三连尾
            case 1109:
                strings = LHCUtil.generaList(3, 8, 3, option);
                break;
            // 四连尾
            case 1111:
                strings = LHCUtil.generaList(4, 8, 4, option);
                break;
            // 五不中
            case 1113:
                strings = LHCUtil.generaList(5, 10, 5, option);
                break;
            // 六不中
//            case 1114:
//                strings = LHCUtil.generaList(6, 10, 6, option);
//                break;
            // 七不中
//            case 1115:
//                strings = LHCUtil.generaList(7, 10, 7, option);
//                break;
//            // 八不中
//            case 1116:
//                strings = LHCUtil.generaList(8, 10, 8, option);
//                break;
//            // 九不中
//            case 1117:
//                strings = LHCUtil.generaList(9, 10, 9, option);
//                break;
//            // 十不中
//            case 1118:
//                strings = LHCUtil.generaList(4, 10, 4, option);
//                break;

            default:


                List<String> strings1 = new ArrayList<>();
                strings1.add(option);
                strings = strings1;
                break;

        }

        return strings;
    }


    public static void main(String[] args) {

        StringJoiner stringJoiner = new StringJoiner(",");


        for (int i = 0; i <= 2; i++) {
            stringJoiner.add(i + "");
        }


        List<String> method = method(1102, stringJoiner.toString());
        for (String s : method) {
            System.out.println(s);
        }

    }


    public static boolean isZeroWei(String string){
        String[] split = string.split(",");
        for (String s : split) {
            if (s.equals("0尾")) {
                return true;
            }
        }
        return false;
    }

    public static boolean beGoodAt(String string){
        String[] split = string.split(",");
        for (String s : split) {
            if (s.equals("龙")) {
                return true;
            }
        }
        return false;
    }
}
