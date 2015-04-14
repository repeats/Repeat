package utilities;

import java.util.ArrayList;
import java.util.List;

public abstract class ExceptableFunction<D,R, E extends Exception> {

	public abstract R apply(D d) throws E;

	public List<R> applyList(List<D> ds) throws E {
		List<R> output = new ArrayList<R>();
		for (D d : ds) {
			output.add(this.apply(d));
		}
		return output;
	}

	public ExceptableFunction<D, D, E> identity() {
		return new ExceptableFunction<D, D, E>() {
			@Override
			public D apply(D d) {
				return d;
			}
		};
	}
}
