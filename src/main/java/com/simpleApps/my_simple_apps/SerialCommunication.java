package com.simpleApps.my_simple_apps;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Scanner;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class SerialCommunication {
	
	private static final int USB_COMMAND_SEND_LD = 0x11;
	private static final int USB_COMMAND_RECEIVE_LD = 0x12;
	private static final int END_OF_DATA = 0xff + 0xfe + 0xff;
	private static final int USB_ESP_OK = 0x16;
	private static final int USB_ESP_ERROR = 0x17;
	
	private static enum Error {ERROR_RECEIVED, ERROR_SEND, ERROR_RECEIVING_OK, ERROR_SEND_TIME, ERROR_ESP_NOT_SEND_OK};
	private static enum Success {SUCCESS_RECEIVED, SUCCESS_SEND};
	
	private String portName;

	public SerialCommunication(String portName) {
		this.portName = portName;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Create a Scanner object to read user input
		
		SerialCommunication communication = new SerialCommunication("COM1");
		
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Choose:\n[1] Sending \n [2] Receiving \n");
        
        int input = scanner.nextInt();
        
        switch (input) {
		case 1:
			communication.send();
			break;
		case 2:
			communication.receive();
			break;

		default:
			break;
		}

	}
	
	private void receive() {
		// TODO Auto-generated method stub
		
	}

	public void send() {
		String fileName = System.getProperty("user.dir") + "/file.ld";
		
		Thread send = new Thread(() -> {
			try {
				System.out.println("Connecting...");
				CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
				SerialPort serialPort = (SerialPort) portIdentifier.open("SerialCommunication", 2000);

				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				File file = new File(fileName);
				FileInputStream fileInputStream = new FileInputStream(file);
				InputStream inputStream = serialPort.getInputStream();
				OutputStream outputStream = serialPort.getOutputStream();
				
				byte[] buffer = new byte[64];
				int len;
				while ((len = fileInputStream.read(buffer)) > 0) {
					try {
						System.out.println("Sending " + len + " bytes");
						outputStream.write(buffer, 0, len);
					} catch (Exception e) {
						// TODO: handle exception
						error(Error.ERROR_SEND);
					}
					
					try {
						wait(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
						error(Error.ERROR_SEND_TIME);
					}
				}
				
				// Send END OF DATA 
				outputStream.write(END_OF_DATA);
				
				try {
					wait(1);
					
				    int availableBytes = inputStream.available();
		            byte[] bufferIn = new byte[availableBytes];
		            int bytesRead = ByteBuffer.wrap(bufferIn).getInt();
		            
		            if (bytesRead == USB_ESP_OK)
		            	success(Success.SUCCESS_SEND);
		            else
		            	error(Error.ERROR_ESP_NOT_SEND_OK);
		            
		            
				} catch (InterruptedException e) {
					e.printStackTrace();
					error(Error.ERROR_RECEIVING_OK);
				}
				
				// Close connection
				fileInputStream.close();
				outputStream.close();
				serialPort.close();

			} catch (NoSuchPortException | PortInUseException | IOException | UnsupportedCommOperationException e) {
				e.printStackTrace();
				error(Error.ERROR_SEND);
			}
		}); // End of thread
		
		// Start thread
		send.start();
	}


	private void error(Error error) {
		//TODO: Error procedure to write
		consoleOutput("error");
		switch (error) {
		case ERROR_SEND:
			System.out.println("Sending error");
			break;
		case ERROR_RECEIVED:
			System.out.println("Receiving error");
			break;
		case ERROR_RECEIVING_OK:
			System.out.println("Receiving OK from the device error");
			break;
		case ERROR_SEND_TIME:
			System.out.println("Wait time after sending packet error");
			break;
		case ERROR_ESP_NOT_SEND_OK:
			System.out.println("File sended but not received OK from the device");
			break;
			
		default:
			break;
		}
	}
	
	private void success(Success success) {
		//TODO: Success procedure to write
//		consoleOutput("success");
		switch (success) {
		case SUCCESS_SEND:
			System.out.println("Succesfully sended");
			break;
		case SUCCESS_RECEIVED:
			System.out.println("Succesfully received");
			break;

		default:
			break;
		}
	}
	
	private static void consoleOutput(String msg) {
//		Mediator.getInstance().outputConsoleMessage(msg);
	}
}
