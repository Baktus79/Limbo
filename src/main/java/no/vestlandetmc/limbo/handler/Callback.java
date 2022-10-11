package no.vestlandetmc.limbo.handler;

public interface Callback<T> {

	public void execute(T response);

}
