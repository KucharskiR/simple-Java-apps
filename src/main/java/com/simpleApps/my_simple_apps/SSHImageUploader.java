package com.simpleApps.my_simple_apps;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SSHImageUploader {

	public SSHImageUploader() {
	}

    public static void main(String[] args) {
        String localImagePath = "/path/to/your/local/image.jpg";
        String remoteFolderPath = "/path/to/your/remote/folder/";
        String host = "your.hostname.com";
        String user = "your-ssh-username";
        String password = "your-ssh-password";

        // Create a scheduled executor service
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Schedule the image upload task to run every minute
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Create JSch session
                JSch jsch = new JSch();
                Session session = jsch.getSession(user, host, 22);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(password);
                session.connect();

                // Upload image file
                uploadFile(session, localImagePath, remoteFolderPath);

                // Disconnect session
                session.disconnect();

                System.out.println("Image uploaded successfully!");
            } catch (JSchException | SftpException e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MINUTES); // Upload image every minute

        // Keep the main thread alive
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void uploadFile(Session session, String localFilePath, String remoteFolderPath)
            throws JSchException, SftpException {
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();

        // Check if remote folder exists, create if not
        try {
            channelSftp.stat(remoteFolderPath);
        } catch (Exception e) {
            channelSftp.mkdir(remoteFolderPath);
        }

        // Upload the image file
        Path localPath = new File(localFilePath).toPath();
        String remoteFilePath = remoteFolderPath + localPath.getFileName();
        channelSftp.put(localFilePath, remoteFilePath);

        channelSftp.disconnect();
    }
}
