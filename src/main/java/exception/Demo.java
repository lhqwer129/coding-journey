package exception;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author lihui
 * @create 2023-10-27 14:20
 **/
public class Demo {

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();

        try {
            list.add(0);
            list.add(1);
            list.add(2);
            list.add(3);
            throw new RuntimeException("手动抛出异常");

        } catch (Exception e) {

        }

        System.out.println(list);
    }


}
