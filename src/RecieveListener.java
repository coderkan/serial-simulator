import java.awt.Event;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;

public class RecieveListener implements SerialPortPacketListener {

	private SerialPort port = null;
	
	public RecieveListener(SerialPort port) {
		this.port = port;
	}
	
	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
	}

	@Override
	public void serialEvent(SerialPortEvent arg0) {
		
		byte[] newData = new byte[1024];
		int numRead = port.readBytes(newData, newData.length);
		System.out.println("Read " + numRead + " bytes.");
		
		/*
		byte[] newData = arg0.getReceivedData();
		System.out.println("Recieved data of size : " + newData.length);
		for (int i = 0; i < newData.length; ++i)
			System.out.print((char)newData[i]);
		System.out.println("\n");
		*/
	}

	@Override
	public int getPacketSize() {
		return 100;
	}

}
