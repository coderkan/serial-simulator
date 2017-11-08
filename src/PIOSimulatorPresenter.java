
public class PIOSimulatorPresenter {
	
	private PIOSimulatorView view = null;
	
	public PIOSimulatorPresenter() {
	}

	public PIOSimulatorPresenter(PIOSimulatorView view) {
		this.view = view;
	}
	
	public void controlAndSend() {
		String slaveAddress = this.view.getTextSlaveAddress().getText().toString().trim();
		if( slaveAddress.length() == 0 ) {
			this.view.showMessage("Please Input Slave Address");
			return;
		}
		String functionValue = this.view.getTextFunctionValue().getText().toString().trim();
		if( functionValue.length() == 0 ) {
			this.view.showMessage("Please Input Function Address");
			return;
		}			
		
		String values = this.view.getTextValues().getText().toString().trim();
		if( values.length() == 0 ) {
			this.view.showMessage("Please Input Values");
			return;
		}			
		
		if(this.view.getCurrentPort() == null || !this.view.getCurrentPort().isOpen()) {
			this.view.showMessage("Serial Port Not Connected");
			return;
		}
			
		String[] bytes = values.split(",");
		int _length = bytes.length + 2;
		byte[] vals = new byte[_length];	
		
		vals[0] = getByte(this.view.getTextSlaveAddress().getText().trim().toString());
		vals[1] = getByte(this.view.getTextFunctionValue().getText().trim().toString());
		for(int i = 2, j = 0; i < _length; i++, j++) {
			String string = bytes[j];
			vals[i] = getByte(string);
			System.out.println("Bytes: " + string + "  ::  " + vals[i] ) ;			
		}
		/*
		char[] vals = new char[_length];
		int _v = 0;
		for(int i = 0; i < _length; i++) {
			String string = bytes[i];
			int _int = Integer.parseInt(string);
			char c = (char)_int;
			vals[i] = c;
			System.out.println("Bytes: " + string + "  ::  " + c ) ;			
		}
		*/
		
		this.view.getCurrentPort().writeBytes(vals, vals.length);
		
	}
	
	public byte getByte(String string) {
		int _int = Integer.parseInt(string);
		byte c = (byte)_int;
		return c;		
	}
	
}
