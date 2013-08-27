package com.sun.squawk.microedition.io;

import javax.microedition.io.Connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileConnection implements Connection
{
	private final File file;
	
	private FileOutputStream out;
	private FileInputStream in;
	
	public FileConnection(final File file)
	{
		this.file = file;
		open = true;
	}
	
	private boolean open;
	
	public void create() throws IOException
	{
		System.out.println(file.getAbsolutePath());
		file.createNewFile();
	}
	
	public void delete()
	{
		file.delete();
	}
	
	public boolean exists()
	{
		return file.exists();
	}
	
	public long fileSize()
	{
		return file.length();
	}
	
	public String getName()
	{
		return file.getName();
	}
	
	public String getPath()
	{
		return file.getPath();
	}
	
	public String getURL()
	{
		return "file://" + getPath();
	}
	
	public boolean isDirectory()
	{
		return file.isDirectory();
	}
	
	public boolean isOpen()
	{
		return open;
	}
	
	public OutputStream openOutputStream(long offset) throws IOException
	{
		if(open)
		{
			if(out == null)
			{
				out = new FileOutputStream(file, true);
				out.getChannel().position(offset);
				return out;
			}
			else
			{
				throw new IOException("The file is already open for writing");
			}
		}
		else
		{
			throw new IOException("Connection closed");
		}
	}
	
	public OutputStream openOutputStream() throws IOException
	{
		if(open)
		{
			if(out == null)
			{
				out = new FileOutputStream(file);
				return out;
			}
			else
			{
				throw new IOException("The file is already open for writing");
			}	
		}
		else
		{
			throw new IOException("Connection closed");
		}
	}
	
	public InputStream openInputStream() throws IOException
	{
		if(open)
		{
			if(in == null)
			{
				in = new FileInputStream(file);
				return in;
			}
			else
			{
				throw new IOException("The file is already open for reading");
			}
		}
		else
		{
			throw new IOException("Connection closed");
		}
	}
	
	public DataInputStream openDataInputStream() throws IOException
	{
		return new DataInputStream(openInputStream());
	}
	
	public DataOutputStream openDataOutputStream() throws IOException
	{
		return new DataOutputStream(openOutputStream());
	}
	
	public void close()
	{
		if(in != null)
		{
			try
			{
				in.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		if(out != null)
		{
			try
			{
				out.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		open = false;
	}
}
