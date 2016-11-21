package com.smmaeda.swagmanai;

public class DeadlinePassedException extends Exception {
	public DeadlinePassedException()
	{
		super();
	}
	public DeadlinePassedException(String message)
	{
		super(message);
	}
}
