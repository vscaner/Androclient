package veganscanner.androclient.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import veganscanner.androclient.R;


public class ProductAdditionActivity extends ActionBarActivity {

    MyTask mt;
    TextView barcode;
    EditText ProductName;
    EditText CompanyName;
    CheckBox animaltests;
    CheckBox meat;
    CheckBox milk;
    CheckBox gmobox;
    String barcodfrom1;
    ProgressDialog progress;
    private String comment = "";
    final Context context = this;
    final Context contextforload = this;





    public void onMyButtonClicks(View view)
    {
        ProductName = (EditText) findViewById(R.id.editText2);
        CompanyName = (EditText) findViewById(R.id.editText3);
        String ProdeuctnameS = String.valueOf(ProductName.getText());
        String CompanynameS =  String.valueOf(CompanyName.getText());
       if(ProdeuctnameS.length() < 3)
        {
            Toast.makeText(context, "Пожалуйста, заполните поле с названием товара, в названии должно быть не менее 3х символов" ,Toast.LENGTH_LONG).show();
        }
        else if (CompanynameS.length() < 3)
        {
           Toast.makeText(context, "Пожалуйста, заполните поле с наименованием производителя товара, в названии должно быть не менее 3х символов" ,Toast.LENGTH_LONG).show();
        }
        else{

        String tovar = "";
        if(gmobox.isChecked() == true) {if(tovar != ""){tovar = " и ";} tovar = tovar + " содержит ГМО";}
        if(animaltests.isChecked()== true) {if(tovar != ""){tovar = " и ";}tovar = tovar + " тестировался на животных";}
        if(meat.isChecked()== true) {if(tovar != ""){tovar = " и ";}tovar = tovar + " не вегеарианский";}
        if(milk.isChecked()== true) {if(tovar != ""){tovar = " и ";}tovar = tovar + " не веганский";}
        if(tovar == ""){tovar = " полностью этичен";};
       Toast.makeText(context, "Пожалуйста, укажите причину по который Вы считаете что данный товар" + tovar + "." ,Toast.LENGTH_LONG).show();
        commentdialog();
       }


    }

    private void noconectiondialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Ой, нет соединения с сервером");
        builder.setMessage("Соединение с интернетом отсутсвет или нестабильно.Штрихкод все еще находится в памяти, попробывать еще разок?");
        builder.setPositiveButton("Еще разок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent intent = getIntent();
                barcodfrom1 = intent.getStringExtra("key");
                mt = new MyTask();
                mt.execute();
            }
        });
        builder.setNegativeButton("Нет,спасибо", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(contextforload, "Ничего страшного! Попробуйте позже,когда установите стабильное соединение с интернетом",
                        Toast.LENGTH_LONG).show();

            }

        });
        builder.show();
    }
    private void okdialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Огромное спасибо!");
        builder.setMessage("Товар " + ProductName.getText() + " добавлен в базу данных. Вы очень помогли проекту!");
        builder.setPositiveButton("Не за что", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                finish();
            }
        });

        builder.show();
    }
    private void commentdialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Прокоментируйте Ваш выбор");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Готово", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                comment = input.getText().toString();
                mt = new MyTask();
                progress = ProgressDialog.show(contextforload, "", "Соединение с базой", true);
                mt.execute();
            }
        });
        builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        builder.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_addition_activity_layout);
        Intent intent = getIntent();
        barcodfrom1 = intent.getStringExtra("key");
        animaltests = (CheckBox) findViewById(R.id.checkBox3);
        meat = (CheckBox) findViewById(R.id.checkBox);
        milk = (CheckBox) findViewById(R.id.checkBox2);
        gmobox = (CheckBox) findViewById(R.id.checkBox4);


    }

    class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... params) {

            byte[] resultb = null;
            String str = "";
            BufferedReader inBuffer = null;
            String url = "http://lumeria.ru/vscaner/tobase.php";
            String result = "fail";
            ProductName = (EditText) findViewById(R.id.editText2);
            CompanyName = (EditText) findViewById(R.id.editText3);

            String gmopr = "0";
            if(gmobox.isChecked() == true) {gmopr = "1";}
            String animals = "0";
            if(animaltests.isChecked()== true) {animals = "1";}
            String vegst = "0";
            String vegetst = "0";
            if(meat.isChecked()== true) {vegetst= "1";vegst= "1";}
            if(milk.isChecked()== true) {vegst= "1";}
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("bcod", barcodfrom1));
                String Prodeuctname = String.valueOf(ProductName.getText());
                postParameters.add(new BasicNameValuePair("name", Prodeuctname.replace('"',' ')));
                String Companyname =  String.valueOf(CompanyName.getText());
                postParameters.add(new BasicNameValuePair("companyname",Companyname.replace('"',' ')));
                postParameters.add(new BasicNameValuePair("veganstatus", vegst));
                postParameters.add(new BasicNameValuePair("vegetstatus", vegetst));
                postParameters.add(new BasicNameValuePair("gmo", gmopr));
                postParameters.add(new BasicNameValuePair("animals", animals));
                postParameters.add(new BasicNameValuePair("comment", comment.replace('"',' ')));
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters, "UTF-8");
                request.setEntity(formEntity);
                httpClient.execute(request);
                HttpResponse response = httpClient.execute(request);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    resultb = EntityUtils.toByteArray(response.getEntity());
                    str = new String(resultb, "UTF-8");
                }

                result = str;

            } catch (Exception e) {
                // Do something about exceptions
                result = e.getMessage();
            } finally {
                if (inBuffer != null) {
                    try {
                        inBuffer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String page)
        {

            progress.cancel();
            Boolean respnorm = false;

            try {
                if (Integer.parseInt(page) == 0) {
                    //Toast.makeText(context, "Большое спасибо.Товар добавлен в базу!", Toast.LENGTH_LONG).show();
                    respnorm = true;
                    okdialog();
                    //finish();
                }
            }
            catch(Exception e){}
              if (respnorm == false)
                {
                    noconectiondialog();
                }
                }
            }
        }



