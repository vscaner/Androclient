package alfa1.veganscaner.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class main_screen extends ActionBarActivity {
    MyTask mt;
    Button CustomInf;
    TextView tvInfo;
    TextView VEGS;
    TextView VEGETS;
    TextView TestinAnimals;
    TextView Compan;
    String Barcode;
    String CommentTOSHOW = "";
    AlertDialog.Builder newbarcode;
    AlertDialog.Builder erroresponefrommysql;
    ProgressDialog progress;
    Context context;
    private String comment = "";
    String buttonint;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        CustomInf = (Button) findViewById(R.id.button3);
        tvInfo = (TextView) findViewById(R.id.ProductINF);
        VEGS = (TextView) findViewById(R.id.VeganStatus);
        VEGETS = (TextView) findViewById(R.id.VegeterianStatus);
        TestinAnimals = (TextView) findViewById(R.id.testsINanimals);
        Compan = (TextView) findViewById(R.id.CompanyName);
        context = main_screen.this;



        //ТУТ ДИАЛОГ ПРИ ОТСУТСВИИИ СОЕДЕНЕНИЯ
        erroresponefrommysql = new AlertDialog.Builder(context);
        erroresponefrommysql.setTitle("Ой, нет соединения с сервером");
        if(buttonint != "barcodemsg")//ЕСЛИ ВЫПОЛНЯМ POST ОТПРАВКИ ОШИБКИ
        {
            erroresponefrommysql.setMessage("Соединение с интернетом отсутсвет или нестабильно.Сообщение все еще находится в памяти, попробывать еще разок?");
        }
        else
        {
            erroresponefrommysql.setMessage("Соединение с интернетом отсутсвет или нестабильно.Штрихкод все еще находится в памяти, попробывать еще разок?");
        }
        erroresponefrommysql.setPositiveButton("Еще разок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                mt = new MyTask();
                mt.execute();
            }
        });
        erroresponefrommysql.setNegativeButton("Нет,спасибо", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Ничего страшного! Попробуйте позже,когда установите стабильное соединение с интернетом",
                        Toast.LENGTH_LONG).show();
                TextView t = (TextView) findViewById(R.id.BarCodeText);
                t.setText("Штриход: ");
            }

        });

        //ТУТ ДИАЛОГ ПРИ ОБНАРУЖЕНИИ ОТСУТВИЯ ПОЗИЦИИ В БАЗЕ
        newbarcode = new AlertDialog.Builder(context);
        newbarcode.setTitle("Ой, нет упоминания в базе");  // заголовок
        newbarcode.setMessage("Вы можете помочь проекту добавив данный товар в базу, это не займет много времени"); // сообщение
        newbarcode.setPositiveButton("Нет,спасибо", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Ничего страшного! Попробуйте позже, база обновлятся каждую минуту!",
                        Toast.LENGTH_LONG).show();
                TextView t = (TextView) findViewById(R.id.BarCodeText);
                t.setText("Штриход: ");
            }
        });
        newbarcode.setNegativeButton("Да, согласен", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Большое спасибо", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(main_screen.this, new_item1.class);
                myIntent.putExtra("key", Barcode); //Optional parameters
                main_screen.this.startActivity(myIntent);
                TextView t = (TextView) findViewById(R.id.BarCodeText);
                t.setText("Штриход: ");

            }
        });
        newbarcode.setCancelable(true);
        newbarcode.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "Вы ничего не выбрали",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    public static int randInt() {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((99 - 1) + 1) + 1;

        return randomNum;
    }

    //ТУТ ДИАЛОГ ОТПРАВКИ СООБЩЕНИЯ О ОШИБКЕ
    private void commentdialog()
    {

        Toast.makeText(this, "Пожалуйста поясните, почему Вы считаете информацию о товаре не соответсвует действительности", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Сообщение о ошибке в базе");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                comment = input.getText().toString();
                mt = new MyTask();
                progress = ProgressDialog.show(context, "", "Соединение с базой", true);
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

// НАЖАТИЕ КНОПОК

    public void onERRORButtonClick(View view)
    {
        buttonint = "errormsg";

        if(Barcode != null)
        {
            commentdialog();
        }
        else
        {
            Toast.makeText(context, "В начале отсканируйте товар", Toast.LENGTH_LONG).show();
        }
    }


    public void onTESTButtonClick(View view) {
        buttonint = "barcodemsg";
        progress = ProgressDialog.show(this, "", "Соединение с базой", true);
        Barcode = "4601546";
        //Barcode = "4601546" + randInt() + randInt() + randInt() ;
        mt = new MyTask();
        mt.execute();
    }

//ДОПОЛНИТЕЛЬНАЯ ИНФОРМАЦИЯ
    public void onINFButtonClick(View view) {
        if(Barcode != null)
        {
            Toast.makeText(context, CommentTOSHOW, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, "В начале отсканируйте товар", Toast.LENGTH_LONG).show();
        }
    }

    public void onMyButtonClick(View view) {
        //ВЕРНЕМ ШРИФТ НА МЕСТО
        buttonint = "barcodemsg";
        VEGS.setTypeface(null, Typeface.NORMAL);
        VEGETS.setTypeface(null, Typeface.NORMAL);
        TestinAnimals.setTypeface(null, Typeface.NORMAL);
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
        Toast.makeText(this, "Поднесите камеру к штрих-коду", Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {

            Toast.makeText(this, "Получен штрих-код", Toast.LENGTH_SHORT).show();
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            TextView t = (TextView) findViewById(R.id.BarCodeText);
            t.setText("Штриход: " + scanContent);
            Barcode = scanContent;
            progress = ProgressDialog.show(this, "", "Соединение с базой", true);
            mt = new MyTask();
            mt.execute();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Не получен штрих-код", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            byte[] resultb = null;
            String str = "";
            BufferedReader inBuffer = null;
            String url = "http://lumeria.ru/vscaner/";
            if(buttonint != "barcodemsg")//ЕСЛИ ВЫПОЛНЯМ POST ОТПРАВКИ ОШИБКИ
            {
                url = "http://lumeria.ru/vscaner/er.php";
            }
            String result = "fail";
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("bcod", Barcode));
                if(buttonint != "barcodemsg")
                {
                    postParameters.add(new BasicNameValuePair("comment", comment));
                }
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


        protected void onPostExecute(String page) {
            progress.cancel();
            Boolean addbarcode = false;
            //ЕСЛИ POST ВЕРНУЛ "731" ТО ВЫЗЫВАЕМ ПРОЦЕДУРУ ДОБАВЛЕНИЯ В БАЗУ
            try
            {
                int b = Integer.parseInt(page);
                int a = 731;
                if (b == a) {
                    addbarcode = true;
                    newbarcode.show();
                }
                else if(b == 0)
                {
                    addbarcode = true;
               Toast.makeText(context, "Ваше обращение принято! Орномное спасибо за участие в проекте!", Toast.LENGTH_LONG).show();
                }
            }
            catch(Exception e) {};

            //ИНАЧЕ ПРОБУЕМ ОТОБРАЗИТЬ РЕЗУЛЬТАТ
            if (addbarcode == false) {
            try {
                int control = 0;
                String[] parts = page.split("§");
                String vegantext;
                String vegeteriatext;
                String inanimals;
                int j = Integer.parseInt(parts[1]); //ПРОВЕРКА НА ВЕГАНСТВО
                if (j == 0) {
                    vegantext = "ДА";
                    vegeteriatext = "ДА";
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.r1inearLayoutid);
                    relativeLayout.setBackgroundResource(R.drawable.ok);
                    control = 1;
                } else {

                    vegantext = "НЕТ";
                    VEGS.setTypeface(null, Typeface.BOLD);
                    SpannableString contentV = new SpannableString("Веганский: " + vegantext);
                    contentV.setSpan(new UnderlineSpan(), 0, contentV.length(), 0);
                    VEGS.setText(contentV);
                    int j1 = Integer.parseInt(parts[2]);//ПРОВЕРКА НА ВЕГЕТАРИАНСТВО
                    if (j1 == 0) {
                        vegeteriatext = "ДА";
                        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.r1inearLayoutid);
                        relativeLayout.setBackgroundResource(R.drawable.normal);
                        control = 1;
                    } else {
                        vegeteriatext = "НЕТ";

                        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.r1inearLayoutid);
                        relativeLayout.setBackgroundResource(R.drawable.no);
                        VEGETS.setTypeface(null, Typeface.BOLD);
                        SpannableString contentVEGET = new SpannableString("Вегетарианский: " + vegeteriatext);
                        contentVEGET.setSpan(new UnderlineSpan(), 0, contentVEGET.length(), 0);
                        VEGETS.setText(contentVEGET);

                    }
                }


                int j3 = Integer.parseInt(parts[3]);
                if (j3 == 0) {
                    inanimals = "НЕТ";
                } else {
                    inanimals = "ДА";
                }

                tvInfo.setText("Продукт: " + '"' + parts[0] + '"');
                Compan.setText("Производитель: " + '"' + parts[5] + '"');
                VEGS.setText("Веганский:  " + vegantext);
                VEGETS.setText("Вегетарианский:  " + vegeteriatext);
                TestinAnimals.setText("Тесты на животных: " + inanimals);
                CustomInf.setVisibility(View.VISIBLE);
                CommentTOSHOW = parts[6];
                if (j3 == 1)

                {
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.r1inearLayoutid);
                    relativeLayout.setBackgroundResource(R.drawable.no);
                    TestinAnimals.setTypeface(null, Typeface.BOLD);
                    SpannableString content = new SpannableString("Тесты на животных: " + inanimals);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    TestinAnimals.setText(content);
                }

            } catch (ArrayIndexOutOfBoundsException e)
            {


              if (page == "The target server failed to respond") {

                    erroresponefrommysql.show();

                } else
                {
                    erroresponefrommysql.show(); //ПОКА ТОТО ЖЕ САМЫЙ ВЫВОД
                }


                }
            }
            }
        }


    }







