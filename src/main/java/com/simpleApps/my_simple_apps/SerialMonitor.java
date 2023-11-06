package com.simpleApps.my_simple_apps;

import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import com.fazecast.jSerialComm.SerialPort;

public class SerialMonitor {

	private static final byte[] USB_ESP_OK = new byte[] { (byte) 0xff, (byte) 0x16, (byte) 0xff };
	private static final byte[] USB_ESP_ERROR = new byte[] { (byte) 0xff, (byte) 0x17, (byte) 0xff };

	private static final int USB_COMMAND_SEND_LD = 0x11;
	private static final int USB_COMMAND_RECEIVE_LD = 0x12;

	public SerialMonitor() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			System.out.println("Connecting...");

			SerialPort comPort = SerialPort.getCommPort("COM4");
			comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
			comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);

			if (comPort.openPort()) {

				InputStream inputStream = comPort.getInputStream();
				OutputStream outputStream = comPort.getOutputStream();


				// Sending start command to ESP
				outputStream.write(USB_COMMAND_RECEIVE_LD);
				System.out.println("Command receive sent to esp");

				
				// Monitor
				while (true) {
					serialMonitor(comPort);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private synchronized static void serialMonitor(SerialPort serialPort) {
	       try {
               InputStream input = serialPort.getInputStream();
               int availableBytes = input.available();
               byte[] data = new byte[availableBytes];
               input.read(data, 0, availableBytes);
               
               Toolkit.getDefaultToolkit().beep();
               System.out.print(new String(data));
           } catch (IOException e) {
               e.printStackTrace();
           }

	}

}
