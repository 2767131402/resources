package com.zhenglei.utils.stream;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamJDK8 {

	/**
	 * reduce的使用，第一个参数是种子，如果不传递将以流的第一个数据为种子
	 * 第二个参数是累积器accumulator，迭代对流的数据操作，第三个参数combiner不填默认同第二个参数相同， 第三个参数只在并行流下有意义，当且仅当
	 * combiner.apply(u, accumulator.apply(identity, t)) == accumulator.apply(u, t)
	 * 时，计算结果与串行流相同
	 */
	public static void testReduce() {
		//累加 包括开始不包括结束
		IntStream stream = IntStream.range(1, 100);
		System.out.println(stream.sum());
		stream = IntStream.range(1, 100);
		// 并行计算1-99的和，种子值为1，由于存在并行计算，因此结果将会比预期值大
		System.out.println(stream.parallel().reduce(1, (a, b) -> a + b));
		stream = IntStream.range(1, 100);
		// 种子值为0，与预期相符
		System.out.println(stream.parallel().reduce(0, (a, b) -> a + b));
		// 串行计算
		stream = IntStream.range(1, 100);
		System.out.println(stream.reduce(0, (a, b) -> a + b));

		// 串行计算平方和
		stream = IntStream.range(1, 5);
		System.out.println(stream.reduce(0, (a, b) -> a + b * b));
		// 并行计算平方和，combiner这样写得不到想要的结果
		stream = IntStream.range(1, 5);
		System.out.println(stream.parallel().reduce(0, (a, b) -> a + b * b));
		// 适合计算平方和的combiner
		//(0+1*1)+(0+2*2)+(0+3*3)+(0+4*4)
		System.out.println(Stream.of(1, 2, 3, 4).parallel().reduce(0, (a, b) -> a + b * b, (a, b) -> a + b));
	}

	public static void testParallelStream() {
		int[] arr = IntStream.range(1, 5).toArray();
		new Thread(() -> {
			Arrays.stream(arr).parallel().forEach((v) -> {
				try {
					System.out.println("first:" + v);
					int sum = 0;
					for (long i = 0; i < (1 << 28); i++) {
						sum += i % 2;
					}
					System.out.println("first:" + v + ":" + sum);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}).start();
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		new Thread(() -> {
			Arrays.stream(arr).parallel().forEach((v) -> {
				try {
					System.out.println("first1:" + v);
					int sum = 0;
					for (long i = 0; i < (1 << 30); i++) {
						sum += i % 2;
					}
					System.out.println("first1:" + v + ":" + sum);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}).start();
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Arrays.stream(arr).forEach((v) -> {
			// 此处有断点
			System.out.println("second" + v);
		});
	}

	public static void main(String args[]) {
		//串行和并行比较
		IntStream stream = IntStream.range(1, 999999999);
		long aa = System.currentTimeMillis();
		stream.parallel().reduce(0, (a, b) -> a * b);//并行
		long bb = System.currentTimeMillis();
		System.err.println(bb-aa);
		IntStream stream2 = IntStream.range(1, 999999999);
		long cc = System.currentTimeMillis();
		stream2.reduce(0, (a, b) -> a * b);//串行
		long dd = System.currentTimeMillis();
		System.err.println(dd-cc);
		
		testReduce();
		System.out.println("使用 Java 8: ");
		List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd", "", "jkl");
		System.out.println("列表: " + strings);

		long count = strings.stream().filter(string -> string.isEmpty()).count();
		System.out.println("空字符串数量为: " + count);

		count = strings.stream().filter(string -> string.length() == 3).count();
		System.out.println("字符串长度为 3 的数量为: " + count);

		List<String> filtered = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
		System.out.println("筛选后的列表: " + filtered);

		String mergedString = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.joining(", "));
		System.out.println("合并字符串: " + mergedString);

		List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
		List<Integer> integers = Arrays.asList(1, 2, 13, 4, 15, 6, 17, 8, 19);
		List<Integer> squaresList = numbers.stream().map(i -> i * i).distinct().collect(Collectors.toList());
		System.out.println("Squares List: " + squaresList);
		System.out.println("列表: " + integers);

		IntSummaryStatistics stats = integers.stream().mapToInt((x) -> x).summaryStatistics();

		System.out.println("列表中最大的数 : " + stats.getMax());
		System.out.println("列表中最小的数 : " + stats.getMin());
		System.out.println("所有数之和 : " + stats.getSum());
		System.out.println("平均数 : " + stats.getAverage());
		System.out.println("随机数: ");

		Random random = new Random();
		random.ints().limit(10).sorted().forEach(System.out::println);

		// 并行处理
		count = strings.parallelStream().filter(string -> string.isEmpty()).count();
		System.out.println("空字符串的数量为: " + count);
	}

}
