package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import com.sun.squawk.microedition.io.FileConnection;

public class Connector
{
	//TODO: these are ignored by everything
	public static final int READ = 1;
	public static final int READ_WRITE = 2;
	public static final int WRITE = 3;
	
	private Connector() throws IllegalAccessException { throw new IllegalAccessException("Cannot instantiate Connector"); }
	
	public static Connection open(String name)
	{
		URI uri = URI.create(name);
		
		if (uri.getScheme().equals("file")){
				return new FileConnection(new File(uri.getHost()+uri.getPath()));
		}
		else {
				throw new IllegalArgumentException("Unknown scheme: " + uri.getScheme());
		}
	}
	
	public static Connection open(String name, int mode)
	{
		return open(name);
	}
	
	public static Connection open(String name, int mode, boolean  timeouts)
	{
		return open(name);
	}
	
	/*
	 * Connection implementations should implement open*Stream such that the returned streams
	 * can be closed by themselves so open*Stream methods in Connector can work without causing
	 * resource leaks.
	 */
	
	public static DataInputStream openDataInputStream(String name) throws IOException
	{
		return open(name).openDataInputStream();
	}
	
	public static DataOutputStream openDataOutputStream(String name) throws IOException
	{
		return open(name).openDataOutputStream();
	}
	
	public static InputStream openInputStream(String name) throws IOException
	{
		return open(name).openInputStream();
	}
	
	public static OutputStream openOutputStream(String name) throws IOException
	{
		return open(name).openOutputStream();
	}
}
