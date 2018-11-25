/**
 * File : CompressedTransmissionActivity.java
 *
 * Authors : Jee Mathieu, Kopp Olivier, Silvestri Romain
 *
 * Date : 25.11.2018
 *
 * This activity allow users to send compressed texts to a server, to receive a compressed response
 * and to display it after decompressed it. In our case, the data that is sent is "hello world!"
 */
package com.example.mathieu.sym_labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

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
            public boolean handleServerResponse(byte[] response) {
                if (response != null) {
                    return displayServerResponse(response);
                }
                return false;
            }
        }, headers, compressedData).execute("http://sym.iict.ch/rest/txt", SymComManager.TEXT);
    }

    private boolean displayServerResponse(final byte[] response) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    receivedData.setText(new String(decompressData(response)));
                } catch (DataFormatException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    /**
     * inspiré de https://www.programcreek.com/java-api-examples/java.util.zip.DeflaterOutputStream
     * @param data
     * @return
     * @throws IOException
     */
    private byte[] compressData(byte[] data) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] ret = new byte[data.length];

        try {
            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, true);
            DeflaterOutputStream zip = new DeflaterOutputStream(stream, deflater);
            zip.write(data);
            zip.close();
            deflater.end();
            ret = stream.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * inspiré de http://www.javased.com/index.php?api=java.util.zip.Inflater
     * @param data
     * @return
     * @throws DataFormatException
     * @throws IOException
     */
    private String decompressData(byte[] data) throws DataFormatException, IOException {
        Inflater inflater=new Inflater(true);
        inflater.setInput(data);
        ByteArrayOutputStream baos=new ByteArrayOutputStream(data.length);
        byte[] buf=new byte[1024];
        while (!inflater.finished()) {
            int count=inflater.inflate(buf);
            baos.write(buf,0,count);
        }
        baos.close();
        return baos.toString();

    }

}
