package utilities;

public class Pair<A, B> {

	private final A a;
	private final B b;

	public static<A,B> Pair<A, B> of(A a, B b) {
		return new Pair<A, B>(a, b);
	}

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}
}
