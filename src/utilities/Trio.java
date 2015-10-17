package utilities;

public class Trio<A, B, C> {

	private final A a;
	private final B b;
	private final C c;

	public Trio(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	public C getC() {
		return c;
	}
}
