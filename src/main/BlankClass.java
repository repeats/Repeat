package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlankClass {
	public static void main(String[] args) {
		List<Integer> l1 = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
		List<Integer> l2 = new ArrayList<>(l1);
		l1.add(5);
		System.out.println(l1.size());
		System.out.println(l2.size());
	}
}
