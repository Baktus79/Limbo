package no.vestlandetmc.limbo.handler;

public interface Callback<T> {

	void execute(T response);

}
