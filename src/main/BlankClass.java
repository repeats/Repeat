package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlankClass {

	private static final Pattern KEY_EVENT = Pattern.compile("^K,T:([A-Z]),K:((\\-)?[0-9]+)$");
	private static final Pattern MOUSE_MOVE_EVENT = Pattern.compile("^M,T:M,X:((\\-)?[0-9]+),Y:((\\-)?[0-9]+)$");
	private static final Pattern MOUSE_BUTTON_EVENT = Pattern.compile("^M,T:([A-Z]),B:((\\-)?[0-9]+)$");

	public static void main(String[] args) throws InterruptedException {
		Matcher m;
//		m = KEY_EVENT.matcher("K,T:E,K:-3");
//		System.out.println(m.find());
//		System.out.println(m.group(1));
//		System.out.println(m.group(2));

		m = MOUSE_MOVE_EVENT.matcher("M,T:M,X:123,Y:-456");
		System.out.println(m.find());
		System.out.println(m.group(1));//123
		System.out.println(m.group(3));//-456

//		m = MOUSE_BUTTON_EVENT.matcher("M,T:P,B:2");
//		System.out.println(m.find());
//		System.out.println(m.group(1));//P
//		System.out.println(m.group(2));//2
	}
}
