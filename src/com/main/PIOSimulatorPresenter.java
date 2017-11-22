package com.main;

public class PIOSimulatorPresenter {

	private PIOSimulatorView view = null;

	public PIOSimulatorPresenter() {
	}

	public PIOSimulatorPresenter(PIOSimulatorView view) {
		this.view = view;
	}

	public void controlAndSend() {
		String slaveAddress = this.view.getTextSlaveAddress().getText().toString().trim();
		if (slaveAddress.length() == 0) {
			this.view.showMessage("Please Input Slave Address");
			return;
		}
		String functionValue = this.view.getTextFunctionValue().getText().toString().trim();
		if (functionValue.length() == 0) {
			this.view.showMessage("Please Input Function Address");
			return;
		}

		String values = this.view.getTextValues().getText().toString().trim();
		if (values.length() == 0) {
			this.view.showMessage("Please Input Values");
			return;
		}

		if (this.view.getCurrentPort() == null || !this.view.getCurrentPort().isOpen()) {
			this.view.showMessage("Serial Port Not Connected");
			return;
		}
		
		String[] bytes = values.split(",");
		int _length = bytes.length + 2 + 2; // CRC Check bytes
		byte[] vals = new byte[_length];

		vals[0] = getByte(this.view.getTextSlaveAddress().getText().trim().toString());
		vals[1] = getByte(this.view.getTextFunctionValue().getText().trim().toString());
		for (int i = 2, j = 0; i < _length - 2; i++, j++) {
			String string = bytes[j];
			vals[i] = getByte(string);
			System.out.println("Bytes: " + string + "  ::  " + vals[i]);
		}

		// CrcCheck and send

		CRC16 crc = new CRC16();
		for (int i = 0; i < vals.length - 2; i++) {
			crc.update(vals[i]);
		}

		System.out.println(Integer.toHexString((int) crc.getValue()));
		byte[] byteStr = new byte[2];
		byteStr[0] = (byte) ((crc.getValue() & 0x000000ff));
		byteStr[1] = (byte) ((crc.getValue() & 0x0000ff00) >>> 8);

		System.out.printf("%02X :: %02X\n", byteStr[0], byteStr[1]);
		int len = vals.length;
		vals[len - 2] = byteStr[0];
		vals[len - 1] = byteStr[1];

		for (int i = 0; i < vals.length; i++) {
			System.out.println("::::: " + vals[i]);
		}
		
		long time_millis = 5;
		this.view.getCurrentPort().writeBytes(vals, vals.length);
		try {
			Thread.sleep(time_millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public byte getByte(String string) {
		int _int = Integer.parseInt(string);
		byte c = (byte) _int;
		return c;
	}

	public void sendPacket(Function function) {
		String slaveAddress = this.view.getTextSlaveAddress().getText().toString().trim();
		if (slaveAddress.length() == 0) {
			this.view.showMessage("Please Input Slave Address");
			return;
		}
		
		if (this.view.getCurrentPort() == null || !this.view.getCurrentPort().isOpen()) {
			this.view.showMessage("Serial Port Not Connected");
			return;
		}
		
		
		
		byte[]  vals = new byte[4];
		vals[0] = getByte(this.view.getTextSlaveAddress().getText().trim().toString());
		vals[1] = 0x01;

		// CrcCheck and send
		CRC16 crc = new CRC16();
		for (int i = 0; i < 2; i++) {
			crc.update(vals[i]);
		}
		
		System.out.println(Integer.toHexString((int) crc.getValue()));
		byte[] byteStr = new byte[2];
		byteStr[0] = (byte) ((crc.getValue() & 0x000000ff));
		byteStr[1] = (byte) ((crc.getValue() & 0x0000ff00) >>> 8);

		System.out.printf("%02X :: %02X\n", byteStr[0], byteStr[1]);
		int len = vals.length;
		
		vals[len - 2] = byteStr[0];
		vals[len - 1] = byteStr[1];

		for (int i = 0; i < vals.length; i++) {
			System.out.println("::::: " + vals[i]);
		}
		
		this.view.getCurrentPort().writeBytes(vals, vals.length);
		
	}
	
	public void sendWithPacket(Function function) {
		String slaveAddress = this.view.getTextSlaveAddress().getText().toString().trim();
		if (slaveAddress.length() == 0) {
			this.view.showMessage("Please Input Slave Address");
			return;
		}
		
		if (this.view.getCurrentPort() == null || !this.view.getCurrentPort().isOpen()) {
			this.view.showMessage("Serial Port Not Connected");
			return;
		}
		
		Packet mPacket = new Packet(this.view);
		mPacket.addByte(getByte(this.view.getTextSlaveAddress().getText().trim().toString()));
		if(function == Function.F0x01) {
			mPacket.addByte((byte)0x01);	
		}
		if(function == Function.F0x01_A) {
			mPacket.addByte((byte)0x01);
			PIOSimulator.autoSend = true;
		}else {
			PIOSimulator.autoSend = false;
		}
		mPacket.crc16();
		
		if(PIOSimulator.autoSend) {
			PIOSimulator.autoPacket = mPacket;
		}
		
		this.view.getCurrentPort().writeBytes(mPacket.getByteArray(), mPacket.getByteArray().length);		
	}

}
