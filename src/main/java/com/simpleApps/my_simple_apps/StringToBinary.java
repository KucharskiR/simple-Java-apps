package com.simpleApps.my_simple_apps;

/******************************************************************************
 * 
 * Online Java Compiler. Code, Compile, Run and Debug java program online. Write
 * your code in this editor and press "Run" button to execute it.
 * 
 *******************************************************************************/

public class StringToBinary {
	private static final int USB_COMMAND_INIT_READ_LD = 0x30;

	public static void main(String[] args) {

		String string0 = Integer.toBinaryString(8);
		String string1 = Integer.toBinaryString(USB_COMMAND_INIT_READ_LD);
		String string2 = string0 + string1;

		byte byte0 = 1;

		String binaryStr = "0001";

		String output = String.format("%8s", binaryStr).replace(' ', '0');

		System.out.println(output);
		System.out.println("string2: " + string2);
		System.out.println(Integer.toBinaryString(8));
		System.out.println(Integer.toBinaryString(USB_COMMAND_INIT_READ_LD));
		System.out.println(Integer.toBinaryString(8 + USB_COMMAND_INIT_READ_LD));
		System.out.println(Integer.toBinaryString(byte0));
		System.out.println(Integer.parseInt(string0, 10));
//System.out.println(USB_COMMAND_INIT_READ_LD.byteValue());

	}
}
