package com.simpleApps.my_simple_apps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.fazecast.jSerialComm.SerialPort;



public class SerialCommunication {
	
	private static final int USB_COMMAND_SEND_LD = 0x11;
	private static final int USB_COMMAND_RECEIVE_LD = 0x12;
	private static final int END_OF_DATA1 = 0xff;
	private static final int END_OF_DATA2 = 0xfe;
	private static final int END_OF_DATA3 = 0xff;
	private static final int USB_ESP_OK = 0x16;
	private static final int USB_ESP_ERROR = 0x17;
	
	private static enum Error {ERROR_RECEIVING, ERROR_SEND, ERROR_RECEIVING_OK, ERROR_SEND_TIME, ERROR_ESP_NOT_SEND_OK, ERROR_FROM_ESP, ERROR_OPEN_SERIAL};
	private static enum Success {SUCCESS_RECEIVED, SUCCESS_SEND, SUCCESS_RECEIVED_OK};
	
	private String portName;
	private int baudRate;

	public SerialCommunication(String portName) {
		this.portName = portName;
		baudRate = 57600; // Baudrate value
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Create a Scanner object to read user input
		
		SerialCommunication serialConnection = new SerialCommunication("COM4");
		
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Choose:\n1-Sending \n2-Receiving \n");
        
        int input = scanner.nextInt();
        
        switch (input) {
		case 1:
			serialConnection.send();
			break;
		case 2:
			serialConnection.receive();
			break;

		default:
			break;
		}

	}
	
	private void receive() {
		
		Thread serialReceiving = new Thread(() -> {
			try {
				System.out.println("Connecting...");

				SerialPort comPort = SerialPort.getCommPort(portName);
				comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
				comPort.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);

				if (comPort.openPort()) {

					InputStream inputStream = comPort.getInputStream();
					OutputStream outputStream = comPort.getOutputStream();

					// Create a FileOutputStream to save the received file
					FileOutputStream fileOutputStream = new FileOutputStream("received_file.ld");

					// Sending start command to ESP
					outputStream.write(USB_COMMAND_RECEIVE_LD);

					int availableBytes = inputStream.available();
					byte[] bufferIn = new byte[availableBytes];
					int bytesRead = ByteBuffer.wrap(bufferIn).getInt();

					if (bytesRead != USB_ESP_OK)
						error(Error.ERROR_ESP_NOT_SEND_OK);

					// Define a buffer for receiving data
					byte[] buffer = new byte[64];

					// Read the file size first (assuming it's sent as an integer)
					byte[] sizeBuffer = new byte[4];
					inputStream.read(sizeBuffer);
					int fileSize = ByteBuffer.wrap(sizeBuffer).getInt();

					// Read and write the file data
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						System.out.println("Reading " + bytesRead + " bytes");
						fileOutputStream.write(buffer, 0, bytesRead);
						TimeUnit.MILLISECONDS.sleep(1);
					}

					// Close the streams and serial port
					fileOutputStream.close();
					inputStream.close();
					comPort.closePort();
				} else {
					error(Error.ERROR_OPEN_SERIAL);
					throw new IOException();
				}
				System.out.println("File received successfully.");
			} catch (Exception e) {
				e.printStackTrace();
				error(Error.ERROR_RECEIVING);
			}
		}); // End thread
		
		serialReceiving.start();
	}

	public void send() {
		String fileName = System.getProperty("user.dir") + "/blink.ld";
		
		Thread send = new Thread(() -> {
			try {
				System.out.println("Connecting...");

				SerialPort comPort = SerialPort.getCommPort(portName);
				comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
				comPort.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);

				if (comPort.openPort()) {

					File file = new File(fileName);
					FileInputStream fileInputStream = new FileInputStream(file);
					InputStream inputStream = comPort.getInputStream();
					OutputStream outputStream = comPort.getOutputStream();

					System.out.println("File to send: " + file.toString());

					// Sending start command to ESP
					outputStream.write(USB_COMMAND_SEND_LD);

					// USB_ESP_OK
					try {
						
						// Waiting loop for data from ESP
						while (inputStream.available() == 0) {
							System.out.println(inputStream.available());
						}

						int availableBytes = inputStream.available();
						System.out.println("Available bytes: " + availableBytes);
						byte[] bufferIn = new byte[availableBytes];

						System.out.println(inputStream.read(bufferIn));
						System.out.println("Buffer in: " + Arrays.toString(bufferIn));
						int bytesRead = bufferIn[bufferIn.length-2];
						System.out.println(bytesRead);
						
						// If response from ESP is OK than go into sending file block
						if (bytesRead == USB_ESP_OK) {
							success(Success.SUCCESS_RECEIVED_OK);

							// Sending file procedure
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

								TimeUnit.MILLISECONDS.sleep(5);
							}

							// Send END OF DATA
							byte[] endTable = new byte[3];
							endTable[0] = (byte) END_OF_DATA1;
							endTable[1] = (byte) END_OF_DATA2;
							endTable[2] = (byte) END_OF_DATA3;
							outputStream.write(endTable);
							
							// Waiting loop for data from ESP
							while (inputStream.available() == 0) {
								System.out.println(inputStream.available());
							}

							try {

								availableBytes = inputStream.available();
								bufferIn = new byte[availableBytes];
//								bytesRead = ByteBuffer.wrap(bufferIn).getInt();
								inputStream.read(bufferIn);
								bytesRead = bufferIn[1];
								System.out.println(Arrays.toString(bufferIn));

								switch (bytesRead) {
								case USB_ESP_OK:
									success(Success.SUCCESS_SEND);
									break;
								case USB_ESP_ERROR:
									error(Error.ERROR_FROM_ESP);
									break;

								default:
									error(Error.ERROR_ESP_NOT_SEND_OK);
									break;
								}

							} catch (Exception e) {
								e.printStackTrace();
								error(Error.ERROR_RECEIVING_OK);
							}
						} else
							error(Error.ERROR_ESP_NOT_SEND_OK);
					} catch (InterruptedException e) {
						e.printStackTrace();
						error(Error.ERROR_RECEIVING_OK);
					}

					// Close connection
					fileInputStream.close();
					outputStream.close();
					comPort.closePort();

				} else {
					error(Error.ERROR_OPEN_SERIAL);
					throw new IOException();
				}
			} catch (Exception e) {
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
		case ERROR_RECEIVING:
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
		case ERROR_FROM_ESP:
			System.out.println("ESP send an error");
			break;
		case ERROR_OPEN_SERIAL:
			System.out.println("Can not open serial port");
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
		case SUCCESS_RECEIVED_OK:
			System.out.println("Received OK from ESP!");
			break;

		default:
			break;
		}
	}
	
	private static void consoleOutput(String msg) {
//		Mediator.getInstance().outputConsoleMessage(msg);
	}
}
