package com.smarthome.mobile.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MultipartUtility {
    private static final String LINE_FEED = "\r\n";
    private final String boundary;
    private final HttpURLConnection httpConn;
    private final OutputStream os;
    private final PrintWriter pw;

    public MultipartUtility(String requestURL, String charset) throws IOException {
        boundary = "===" + System.currentTimeMillis() + "===";
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        os = httpConn.getOutputStream();
        pw = new PrintWriter(new OutputStreamWriter(os, charset), true);
    }

    public void addFormField(String name, String value) {
        pw.append("--").append(boundary).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED)
                .append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED).append(LINE_FEED)
                .append(value).append(LINE_FEED);
        pw.flush();
    }

    public void addFilePart(String fieldName, File fileUpload) throws IOException {
        String fileName = fileUpload.getName();
        pw.append("--").append(boundary).append(LINE_FEED);
        pw.append("Content-Disposition: form-data; name=\"").append(fieldName)
                .append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED);
        pw.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        pw.append(LINE_FEED);
        pw.flush();

        FileInputStream inputStream = new FileInputStream(fileUpload);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        inputStream.close();

        pw.append(LINE_FEED);
        pw.flush();
    }

    public List<String> finish() throws IOException {
        pw.append("--").append(boundary).append("--").append(LINE_FEED);
        pw.close();

        List<String> response = new ArrayList<>();
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
        return response;
    }
}
