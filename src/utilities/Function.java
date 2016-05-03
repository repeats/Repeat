package utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Function<D, R> {
	public abstract R apply(D d);

	public List<R> map(Collection<D> ds) {
		List<R> output = new ArrayList<R>(ds.size());
		for (D d : ds) {
			output.add(this.apply(d));
		}
		return output;
	}

	public List<R> map(D[] ds) {
		List<R> output = new ArrayList<R>(ds.length);
		for (D d : ds) {
			output.add(this.apply(d));
		}
		return output;
	}

	public List<R> mapNotNull(Collection<D> ds) {
		List<R> output = new ArrayList<R>(ds.size());
		for (D d : ds) {
			R r = this.apply(d);
			if (r != null) {
				output.add(r);
			}
		}
		return output;
	}

	public List<R> mapNotNull(D[] ds) {
		List<R> output = new ArrayList<R>(ds.length);
		for (D d : ds) {
			R r = this.apply(d);
			if (r != null) {
				output.add(r);
			}
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

	public static Function<Void, Void> nullFunction() {
		return new Function<Void, Void>() {
			@Override
			public Void apply(Void r) {
				return null;
			}
		};
	}

	public static Function<Void, Boolean> trueFunction() {
		return new Function<Void, Boolean>() {
			@Override
			public Boolean apply(Void r) {
				return true;
			}
		};
	}

	public static Function<Void, Boolean> falseFunction() {
		return new Function<Void, Boolean>() {
			@Override
			public Boolean apply(Void r) {
				return false;
			}
		};
	}
}
