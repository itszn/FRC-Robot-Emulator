package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Connection
{
	public void close();
	
	public OutputStream openOutputStream() throws IOException;
	public InputStream openInputStream() throws IOException;
	
	public DataInputStream openDataInputStream() throws IOException;
	public DataOutputStream openDataOutputStream() throws IOException;
}
