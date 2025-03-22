package com.yuhao.yupicturebackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class YuPictureBackendApplicationTests {

    @Test
    void contextLoads() {
    }
    @Test
    void test() {

                // 创建一个ArrayList集合
                Collection<String> collection = new ArrayList<>();

                // 添加元素
                collection.add("Apple");
                collection.add("Banana");
                collection.add("Cherry");
                System.out.println("添加元素后: " + collection); // 输出: [Apple, Banana, Cherry]

                // 添加另一个集合的元素
                Collection<String> anotherCollection = new ArrayList<>();
                anotherCollection.add("Dragonfruit");
                anotherCollection.add("Elderberry");
                collection.addAll(anotherCollection);
                System.out.println("添加另一个集合后: " + collection); // 输出: [Apple, Banana, Cherry, Dragonfruit, Elderberry]

                // 删除元素
                collection.remove("Banana");
                System.out.println("删除'Banana'后: " + collection); // 输出: [Apple, Cherry, Dragonfruit, Elderberry]

                // 移除所有指定集合中的元素
                collection.removeAll(anotherCollection);
                System.out.println("移除所有另一个集合中的元素后: " + collection); // 输出: [Apple, Cherry]

                // 使用removeIf移除满足条件的元素
                collection.removeIf(s -> s.startsWith("A"));
                System.out.println("移除以'A'开头的元素后: " + collection); // 输出: [Cherry]

                // 仅保留指定集合中的元素
                collection.addAll(anotherCollection);
                collection.retainAll(anotherCollection);
                System.out.println("仅保留另一个集合中的元素后: " + collection); // 输出: [Dragonfruit, Elderberry]

                // 清空集合
                collection.clear();
                System.out.println("清空集合后: " + collection); // 输出: []

                // 重新添加元素以进行后续操作
                collection.add("Apple");
                collection.add("Banana");
                collection.add("Cherry");

                // 检查元素
                System.out.println("包含'Banana': " + collection.contains("Banana")); // 输出: true
                System.out.println("包含另一个集合的所有元素: " + collection.containsAll(anotherCollection)); // 输出: false

                // 检查集合是否为空
                System.out.println("集合是否为空: " + collection.isEmpty()); // 输出: false

                // 获取集合大小
                System.out.println("集合的大小: " + collection.size()); // 输出: 3

                // 转换为数组
                Object[] array = collection.toArray();
                System.out.println("从集合转换的数组: " + java.util.Arrays.toString(array)); // 输出: [Apple, Banana, Cherry]

                // 使用指定的数组类型转换
                String[] stringArray = collection.toArray(new String[0]);
                System.out.println("从集合转换的字符串数组: " + java.util.Arrays.toString(stringArray)); // 输出: [Apple, Banana, Cherry]

                // 迭代器
                System.out.print("遍历集合: ");
                Iterator<String> iterator = collection.iterator();
                while (iterator.hasNext()) {
                    System.out.print(iterator.next() + " ");
                }
                System.out.println(); // 输出: 遍历集合: Apple Banana Cherry

                // 流操作
                List<String> uppercaseList = collection.stream().map(String::toUpperCase).collect(Collectors.toList());
                System.out.println("从集合生成的全大写列表: " + uppercaseList); // 输出: [APPLE, BANANA, CHERRY]

                // 并行流操作
                List<String> uppercaseParallelList = collection.parallelStream().map(String::toUpperCase).collect(Collectors.toList());
                System.out.println("从集合生成的并行全大写列表: " + uppercaseParallelList); // 输出: [APPLE, BANANA, CHERRY]

                // 比较集合
                Collection<String> sameCollection = new ArrayList<>();
                sameCollection.add("Apple");
                sameCollection.add("Banana");
                sameCollection.add("Cherry");
                System.out.println("集合是否与sameCollection相等: " + collection.equals(sameCollection)); // 输出: true

                // 获取哈希码
                System.out.println("集合的哈希码: " + collection.hashCode()); // 输出: 哈希码值，具体值取决于元素和顺序
            }
    @Test
    void binary(){
                int[] nums = {1,2,3,4,5,6,7,8,9,10};
                int target = 9 ;
                System.out.println(nums.length);
            }

}
