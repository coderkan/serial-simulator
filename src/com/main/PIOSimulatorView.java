package com.main;

import javax.swing.JTextField;

import com.fazecast.jSerialComm.SerialPort;

public interface PIOSimulatorView {

	SerialPort getCurrentPort();

	void closePort();

	void writeValue();

	void showMessage(String msg);

	JTextField getTextSlaveAddress();

	JTextField getTextFunctionValue();

	JTextField getTextValues();

}
