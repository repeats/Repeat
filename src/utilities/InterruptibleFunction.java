package utilities;

import java.util.ArrayList;
import java.util.List;

public abstract class InterruptibleFunction<D,R> {

	public abstract R apply(D d) throws InterruptedException;

	public List<R> applyList(List<D> ds) throws InterruptedException {
		List<R> output = new ArrayList<R>();
		for (D d : ds) {
			output.add(this.apply(d));
		}
		return output;
	}

	public InterruptibleFunction<D, D> identity() {
		return new InterruptibleFunction<D, D>() {
			@Override
			public D apply(D d) {
				return d;
			}
		};
	}
}
