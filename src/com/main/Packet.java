package com.main;

import java.util.ArrayList;
import java.util.List;

public class Packet {
	
	private List<Byte> byteList = new ArrayList<Byte>(); 
	private PIOSimulatorView view = null;

	
	public Packet() {
		
	}
	
	public Packet(PIOSimulatorView view) {
		this.view = view;
	}
	
	public List<Byte> getByteList(){
		return this.byteList;
	}
	
	public void addByte(byte _byte) {
		Byte b = _byte;
		this.byteList.add(b);
	}
	
	public void crc16() {
		CRC16 crc = new CRC16();
		byte[] bytes = getByteArray();
		
		for (int i = 0; i < bytes.length; i++) {
			crc.update(bytes[i]);
		}
		
		System.out.println(Integer.toHexString((int) crc.getValue()));
		byteList.add((byte) ((crc.getValue() & 0x000000ff))); // low 
		byteList.add((byte) ((crc.getValue() & 0x0000ff00) >>> 8)); // high
	}
	
	public void crc16_2() {
		CRC16 crc = new CRC16();
		byte[] bytes = getByteArray();
		int _len = bytes.length;
		for (int i = 0; i < bytes.length -2; i++) {
			crc.update(bytes[i]);
		}
		System.out.println(Integer.toHexString((int) crc.getValue()));
		byteList.add(_len-2, (byte) ((crc.getValue() & 0x000000ff))); // low 
		byteList.add(_len-1, (byte) ((crc.getValue() & 0x0000ff00) >>> 8)); // high
	}
	
	public void setByteList(List<Byte> byteList) {
		this.byteList.clear();
		this.byteList.addAll(byteList);
	}
	
	public byte[] getByteArray() {
		int len = byteList.size();
		byte[] bytes = new byte[len];
		for(int i = 0; i < len; i++) {
			bytes[i] = byteList.get(i).byteValue();
		}
		return bytes;
	}
	
	public void send() {
		if(this.view == null)
			return;
		this.view.getCurrentPort().writeBytes(getByteArray(), getByteArray().length);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
