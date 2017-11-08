package rxtx;

import java.io.IOException;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class Serial232 {
	
	public void connect(String portName, String baudRate) {
	     CommPortIdentifier portIdentifier = null;
	     portName = "COM3";
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		} catch (NoSuchPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	        
	        if (portIdentifier.isCurrentlyOwned()) {  
	            System.out.println("Port in use!");  
	        } else {  
	            // points who owns the port and connection timeout  
	            SerialPort serialPort = null;
				try {
					serialPort = (SerialPort) portIdentifier.open("Serial232", 2000);
				} catch (PortInUseException e) {
					e.printStackTrace();
				}  
	              
	            // setup connection parameters  
	            try {
	            	int baud = Integer.parseInt(baudRate);
					serialPort.setSerialPortParams(  
					    baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				} catch (UnsupportedCommOperationException e) {
					e.printStackTrace();
				}  
	   
	            // setup serial port writer  
	            try {
					CommPortSender.setWriterStream(serialPort.getOutputStream());
					new CommPortReciever(serialPort.getInputStream()).start();
				} catch (IOException e) {
					e.printStackTrace();
				}  
		}	
	}
   
}
