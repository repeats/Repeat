package frontEnd;

public class BlankClass {

	private final String name;

	public BlankClass(String name) {
		this.name = name;
	}

	public static void main(String[] args) {
		new BlankClass("asd") {
			@Override
			public void hello() {
				System.out.println("yolo");
			}
		}.hello();
	}

	public void hello() {
		System.out.println(name);
	}
}
