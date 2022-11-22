package utilities.natives.processes;

import java.util.Collections;
import java.util.logging.Logger;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.WindowByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import utilities.Function;
import utilities.OSIdentifier;

/**
 * Provides interaction with Linux processes via X11 interface.
 * Reference C code: https://gist.github.com/kui/2622504
 */
public class X11NativeProcessUtil {

	private static final Logger LOGGER = Logger.getLogger(X11NativeProcessUtil.class.getName());

	public static NativeProcessUtil.NativeWindowInfo getActiveWindowInfo() {
		final X11 x11 = X11.INSTANCE;
		final X11Extended xlib = X11Extended.INSTANCE;

		Display display = xlib.XOpenDisplay(null);
		if (display == null) {
			LOGGER.info("No X11 display found.");
			return NativeProcessUtil.NativeWindowInfo.of("", "");
		}
		WindowByReference currentWindowReference = new WindowByReference();
		IntByReference revertToReturn = new IntByReference();
	    xlib.XGetInputFocus(display, currentWindowReference, revertToReturn);
	    X11.Window window = currentWindowReference.getValue();
	    if (window == null) {
			LOGGER.info("No input focus window found. " + revertToReturn.getValue());
			return NativeProcessUtil.NativeWindowInfo.of("", "");
		}

		long topWindow = findTopWindow(x11, display, currentWindowReference);
		X11.XTextProperty name = new X11.XTextProperty();
		x11.XGetWMName(display, new X11.Window(topWindow), name);

	    return NativeProcessUtil.NativeWindowInfo.of(name.value == null ? "" : name.value, "");
	}

	private static long findTopWindow(X11 x11, Display display, X11.WindowByReference current) {
		X11.Window window = current.getValue();
	    X11.WindowByReference parentRef = current;
	    X11.WindowByReference rootRef = new X11.WindowByReference();
	    PointerByReference childrenRef = new PointerByReference();
	    IntByReference childCountRef = new IntByReference();

	    while (parentRef.getValue().longValue() != rootRef.getValue().longValue()) {
	    	window = new X11.Window(parentRef.getValue().longValue());

			int res = x11.XQueryTree(display, window, rootRef, parentRef, childrenRef, childCountRef);
			if (res != 0) {
				x11.XFree(childrenRef.getValue());
			}
		}
	    return window.longValue();
	}

	/**
	 * Prints all windows on display.
	 */
	@SuppressWarnings("unused")
	private static void printAllUnderRoot(X11 x11, Display display) {
		X11.Window root = x11.XDefaultRootWindow(display);
		recurse(x11, display, root, 0);
	}

	/**
	 * Prints all windows under a root.
	 */
	private static void recurse(X11 x11, Display display, X11.Window root, int depth) {
	    X11.WindowByReference windowRef = new X11.WindowByReference();
	    X11.WindowByReference parentRef = new X11.WindowByReference();
	    PointerByReference childrenRef = new PointerByReference();
	    IntByReference childCountRef = new IntByReference();

	    x11.XQueryTree(display, root, windowRef, parentRef, childrenRef, childCountRef);
	    if (childrenRef.getValue() == null) {
	        return;
	    }

	    long[] ids;

	    if (Native.LONG_SIZE == Long.BYTES) {
	        ids = childrenRef.getValue().getLongArray(0, childCountRef.getValue());
	    } else if (Native.LONG_SIZE == Integer.BYTES) {
	        int[] intIds = childrenRef.getValue().getIntArray(0, childCountRef.getValue());
	        ids = new long[intIds.length];
	        for (int i = 0; i < intIds.length; i++) {
	            ids[i] = intIds[i];
	        }
	    } else {
	        throw new IllegalStateException("Unexpected size for Native.LONG_SIZE" + Native.LONG_SIZE);
	    }

	    for (long id : ids) {
	        if (id == 0) {
	            continue;
	        }
	        X11.Window window = new X11.Window(id);
	        X11.XTextProperty name = new X11.XTextProperty();
	        x11.XGetWMName(display, window, name);

        	System.out.println("Depth=" + depth + " (" + id + "):" + String.join("", Collections.nCopies(depth, "  ")) + name.value);
	        x11.XFree(name.getPointer());

	        recurse(x11, display, window, depth + 1);
	    }
	}

	public interface X11Extended extends X11 {
	    X11Extended INSTANCE = new Function<Void, X11Extended>(){
	    	@Override
			public X11Extended apply(Void d) {
				if (!OSIdentifier.IS_LINUX) {
					return null;
				}
				return Native.load("X11", X11Extended.class);
	    	}
	    }.apply(null);

	    void XGetInputFocus(Display display, WindowByReference focusReturn, IntByReference revertToReturn);
	}
}
