package com.main;


import java.awt.EventQueue;
import com.fazecast.jSerialComm.*;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.DropMode;

public class PIOSimulator implements PIOSimulatorView {

	private PIOSimulatorPresenter presenter = null;
	private JFrame frame;
	private SerialPort[] serialPorts = null;
	private boolean isConnected = false;
	private SerialPort mCurrentPort = null;
	private JTextField textSlaveAddress;
	private JTextField textFunctionValue;
	private JTextField textValues;
	private JCheckBox chckbxStatus;
	private String recievedData;
	private JTextArea textFieldRecievedData = null;
	public static boolean autoSend = false;
	public static Packet autoPacket = null;
	private JComboBox<String> comboBaudRate = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PIOSimulator window = new PIOSimulator();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PIOSimulator() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 593, 406);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);

		if (presenter == null)
			presenter = new PIOSimulatorPresenter(this);

		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setBounds(475, 47, 89, 20);
		frame.getContentPane().add(comboBox);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshPorts(comboBox);
			}
		});
		btnRefresh.setBounds(475, 109, 89, 22);
		frame.getContentPane().add(btnRefresh);

		JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				closePort();
			}
		});
		btnDisconnect.setBounds(475, 176, 89, 23);
		frame.getContentPane().add(btnDisconnect);

		chckbxStatus = new JCheckBox("Status");
		chckbxStatus.setBounds(475, 17, 89, 23);
		chckbxStatus.setEnabled(false);
		frame.getContentPane().add(chckbxStatus);

		comboBaudRate = new JComboBox<String>();
		comboBaudRate.setBounds(475, 78, 89, 20);
		frame.getContentPane().add(comboBaudRate);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int index = comboBox.getSelectedIndex();
				System.out.println("Index: " + index);
				
				if(index != -1 && isConnected) {
					mCurrentPort.removeDataListener();
					if (!isConnected) {
						chckbxStatus.setSelected(isConnected);
						return;
					}
					
					mCurrentPort.setBaudRate(Integer.parseInt(comboBaudRate.getSelectedItem().toString()));
					mCurrentPort.addDataListener(dataListener);
					chckbxStatus.setSelected(isConnected);
					return;
				}
				
				
				if (index != -1 && !isConnected) {
					SerialPort port = serialPorts[index];
					isConnected = port.openPort();
					if (!isConnected) {
						chckbxStatus.setSelected(isConnected);
						return;
					}
					mCurrentPort = port;
					
					mCurrentPort.setBaudRate(Integer.parseInt(comboBaudRate.getSelectedItem().toString()));
					mCurrentPort.addDataListener(dataListener);
					chckbxStatus.setSelected(isConnected);
				}
				
			}
		});
		btnConnect.setBounds(475, 142, 89, 23);
		frame.getContentPane().add(btnConnect);

		chckbxStatus.setEnabled(true);
		chckbxStatus.setSelected(isConnected);

		refreshPorts(comboBox);
		
		textSlaveAddress = new JTextField();
		textSlaveAddress.setBounds(104, 18, 152, 20);
		frame.getContentPane().add(textSlaveAddress);
		textSlaveAddress.setColumns(10);
		textSlaveAddress.addKeyListener(onlyNumberAdapter);
		textSlaveAddress.setText("4");

		JLabel lblSlaveAddress = new JLabel("Slave Address:");
		lblSlaveAddress.setBounds(10, 21, 89, 14);
		frame.getContentPane().add(lblSlaveAddress);

		JLabel lblFunctionValue = new JLabel("Function Value: ");
		lblFunctionValue.setBounds(10, 50, 89, 14);
		frame.getContentPane().add(lblFunctionValue);

		textFunctionValue = new JTextField();
		textFunctionValue.setColumns(10);
		textFunctionValue.setBounds(104, 47, 152, 20);
		textFunctionValue.addKeyListener(onlyNumberAdapter);
		textFunctionValue.setText("12");
		frame.getContentPane().add(textFunctionValue);

		JLabel lblValues = new JLabel("Values: ");
		lblValues.setBounds(10, 81, 89, 14);
		frame.getContentPane().add(lblValues);

		textValues = new JTextField();
		textValues.setColumns(10);
		textValues.setBounds(104, 78, 152, 20);
		textValues.addKeyListener(notOnlyAdapter);
		textValues.setText("48,49,50,55,211");
		frame.getContentPane().add(textValues);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.controlAndSend();
			}
		});
		btnSend.setBounds(104, 109, 152, 23);
		frame.getContentPane().add(btnSend);

		textFieldRecievedData = new JTextArea();
		textFieldRecievedData.setEditable(false);

		JScrollPane scroll = new JScrollPane(textFieldRecievedData);
		scroll.setBounds(21, 160, 235, 160);
		frame.getContentPane().add(scroll);

		JLabel lblNewLabel = new JLabel("Recieved Data");
		lblNewLabel.setBounds(21, 146, 106, 14);
		frame.getContentPane().add(lblNewLabel);

		JButton btnNewButton = new JButton("Clear");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				recievedData = "";
				textFieldRecievedData.setText(recievedData);
			}
		});
		btnNewButton.setBounds(104, 331, 152, 23);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnPacket_1 = new JButton("F0x01");
		btnPacket_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				presenter.sendWithPacket(Function.F0x01);
			}
		});
		btnPacket_1.setBounds(266, 157, 89, 23);
		frame.getContentPane().add(btnPacket_1);
		
		JButton btnFxauto = new JButton("F0x01_A");
		btnFxauto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!autoSend)
					presenter.sendWithPacket(Function.F0x01_A);
				else
					autoSend = false;
			}
		});
		btnFxauto.setBounds(266, 188, 89, 23);
		frame.getContentPane().add(btnFxauto);

		String[] baudRates = new String[] { "1200", "9600", "19200", "38400", "57600", "115200", "921600" };
		for (String string : baudRates) {
			comboBaudRate.addItem(string);
		}
		comboBaudRate.setSelectedIndex(1);
	}

	private KeyAdapter notOnlyAdapter = new KeyAdapter() {
		public void keyTyped(KeyEvent e) {
			char c = e.getKeyChar();
			if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)
					|| (c == ','))) {
				e.consume();
			}
		}
	};
	private KeyAdapter onlyNumberAdapter = new KeyAdapter() {
		public void keyTyped(KeyEvent e) {
			char c = e.getKeyChar();
			if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
				e.consume();
			}
		}
	};

	private void refreshPorts(JComboBox<String> comboBox) {
		serialPorts = SerialPort.getCommPorts();
		comboBox.removeAllItems();
		for (int i = 0; i < serialPorts.length; i++) {
			comboBox.addItem(serialPorts[i].getSystemPortName());
		}
	}

	@Override
	public void writeValue() {

	}

	@Override
	public SerialPort getCurrentPort() {
		return this.mCurrentPort;
	}

	@Override
	public void closePort() {
		if (this.mCurrentPort != null && this.mCurrentPort.isOpen()) {
			try {
				Thread.sleep(1000);
				this.mCurrentPort.removeDataListener();
				if (this.mCurrentPort.closePort()) {
					chckbxStatus.setSelected(false);
					isConnected = false;
				}
			} catch (Exception e) {
				System.out.println("Close Exception: " + e.getMessage().toString());
			}

		}
	}

	@Override
	public JTextField getTextSlaveAddress() {
		return this.textSlaveAddress;
	}

	@Override
	public JTextField getTextFunctionValue() {
		return this.textFunctionValue;
	}

	@Override
	public JTextField getTextValues() {
		return this.textValues;
	}

	@Override
	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(frame, msg);
	}

	private SerialPortDataListener dataListener = new SerialPortDataListener() {

		private int val = 0;
		private Packet mPacket = new Packet();
		private int counter = 0;

		@Override
		public int getListeningEvents() {
			return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
		}

		@Override
		public void serialEvent(SerialPortEvent arg0) {

			if (arg0.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
				System.out.println("Available");
			}

			if (arg0.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
				int mbytes = 0;
				byte[] newData = new byte[1024];
				if ((mbytes = arg0.getSerialPort().bytesAvailable()) != 0) {
					System.out.println("Bytes available... " + mbytes);
					int numRead = arg0.getSerialPort().readBytes(newData, newData.length);
					System.out.println("Read " + numRead + " bytes.");
					String str = "";
					for (int i = 0; i < numRead; i++) {
						str += " : " + newData[i];
						mPacket.addByte(newData[i]);
					}
			
					str += "\n";
					recievedData += str;
					textFieldRecievedData.setText(recievedData);
					if(recievedData.length() > 1200)
						recievedData = "";
					
					if(mPacket.getByteArray().length >= 5) {
						if(PIOSimulator.autoSend) {
							if(mPacket.getByteArray()[0] == PIOSimulator.autoPacket.getByteArray()[0]) {
								
								/*Byte bt = PIOSimulator.autoPacket.getByteList().get(0);
								byte btByte = bt.byteValue();
								btByte +=1;
								if(btByte > 0x06) {
									btByte = 0x04;
								}
								Byte b = btByte;
								PIOSimulator.autoPacket.getByteList().add(0, b);
								PIOSimulator.autoPacket.crc16_2();
								*/
								/*
								if(counter++ > 5) {
									PIOSimulator.autoSend = false;
									counter = 0;
								}
								*/
	
								PIOSimulator.autoPacket.send();
								//mPacket.send();
							}
						}
						mPacket.getByteList().clear();
					}
				}
				return;
			}

			if (arg0.getEventType() == SerialPort.LISTENING_EVENT_DATA_WRITTEN) {
				System.out.println("Written");
			}
		}
	};
}
