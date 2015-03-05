package utilities;

import java.util.ArrayList;
import java.util.List;

public abstract class Function<D, R> {
	public abstract R apply(D r);

	public List<D> applyList(List<D> ds) {
		List<D> output = new ArrayList<D>();
		for (D d : ds) {
			output.add(d);
		}
		return output;
	}

	public Function<D, D> identity() {
		return new Function<D, D>() {
			@Override
			public D apply(D d) {
				return d;
			}
		};
	}
}
