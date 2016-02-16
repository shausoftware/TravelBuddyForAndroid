package com.shaustuff.shaumapmobile;

import android.app.Service;
import android.content.Intent;

import android.os.IBinder;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Iterator;
import java.util.Date;

import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.os.StrictMode;

import com.shaustuff.shaumapmobile.model.TubeLineStatus;
import com.shaustuff.shaumapmobile.xmlparser.TubeLineParser;

public class TubeLinePullService extends Service {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    @Override
    public void onCreate() {

        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Toast.makeText(getApplicationContext(), "Tube alert service starting", 1).show();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //get tube line status from London datastore
        String tubeLineStatusUrl = "http://cloud.tfl.gov.uk/TrackerNet/LineStatus";

        try {
            final URL url = new URL(tubeLineStatusUrl);
            final URLConnection conn = url.openConnection();
            conn.setReadTimeout(15000);  // timeout for reading the kml data: 15 secs
            conn.connect();

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            XMLReader xr = sp.getXMLReader();
            TubeLineParser parser = new TubeLineParser();

            xr.setContentHandler(parser);
            xr.parse(new InputSource(url.openStream()));

            Map<String, TubeLineStatus> tubeLineStatusMap = parser.getStatusMap();

            Iterator it = tubeLineStatusMap.keySet().iterator();
            while (it.hasNext()) {

                String key = (String) it.next();
                TubeLineStatus tls = tubeLineStatusMap.get(key);
            }

            final ShauMapApplication shauMapApplication = (ShauMapApplication) getApplicationContext();
            shauMapApplication.updateAlertData(tubeLineStatusMap);

            //done update last update
            String now = dateFormat.format(new Date());
            shauMapApplication.setLastAlertUpdate(now);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "Tube alert service stopping", 1).show();
        super.onDestroy();
    }
}
