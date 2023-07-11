package utils;

public class Tuple3<A, B, C> implements java.io.Serializable{
	public final A a;
	public final B b;
	public final C c;
	
	public Tuple3(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public Tuple3(){
		this.a = null;
		this.b = null;
		this.c = null;
	}
}
