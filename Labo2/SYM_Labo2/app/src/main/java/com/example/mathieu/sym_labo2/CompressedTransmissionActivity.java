package com.example.mathieu.sym_labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.TextView;

import org.jdom2.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class CompressedTransmissionActivity extends AppCompatActivity {

    private TextView sentData;
    private TextView receivedData;

    private static final String SENT_DATA = "HelloWorld!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compressed_transmission);

        sentData     = findViewById(R.id.CompressedTransmissionSentData);
        receivedData = findViewById(R.id.CompressedTransmissionReceivedData);
        byte[] compressedData = null;
        sentData.setText(SENT_DATA);

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(new Pair<>("X-Network", "CSD"));
        headers.add(new Pair<>("X-Content-Encoding", "deflate"));

        try {
           compressedData = compressData(SENT_DATA.getBytes("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        new AsyncSendRequest(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                if (response != null) {
                    return displayServerResponse(response);
                }
                return false;
            }
        }, headers).execute("http://sym.iict.ch/rest/txt", compressedData.toString(), SymComManager.TEXT);
    }

    private boolean displayServerResponse(final String response) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                receivedData.setText(response);
            }
        });
        System.out.println(response);
        return true;
    }

    private byte[] compressData(byte[] data) throws IOException {
        /*final int SIZE = data.length;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(SIZE);
        byte[] result = null;
        byte[] buffer = new byte[1024];

        Deflater compresser = new Deflater();
        compresser.setInput(data);
        compresser.finish();

        while (!compresser.finished()) {
            int count = compresser.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }

        outputStream.close();
        result = outputStream.toByteArray();

        return result;*/
        final int SIZE = data.length;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(SIZE);
        Deflater compresser = new Deflater(Deflater.DEFAULT_STRATEGY, true);
        DeflaterOutputStream dOut = new DeflaterOutputStream(outputStream, compresser);
        dOut.write(data);
        dOut.close();

        return outputStream.toByteArray();
    }

    private byte[] decompressData(byte[] data) throws DataFormatException, IOException {
        /*final int SIZE = data.length;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(SIZE);
        byte[] result = null;
        byte[] buffer = new byte[1024];

        Inflater decompresser = new Inflater();
        decompresser.setInput(data);

        while(!decompresser.finished()) {
            int count = decompresser.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }

        outputStream.close();
        result = outputStream.toByteArray();

        return result;*/
        final int SIZE = data.length;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        InflaterInputStream in = new InflaterInputStream(inputStream);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream(512);
        int b;
        while ((b = in.read()) != -1) {
            bOut.write(b);
        }
        in.close();
        bOut.close();
        return bOut.toByteArray();
    }

}
