package com.copelec.chillan.copelecroce;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;


public class Menu extends AppCompatActivity  {

    public String fecha;
    public String nombrefoto;
    public List<Double> longitud = new ArrayList<>();
    public List<Double> latitud = new ArrayList<>();
    public List<GPS> ListGps = new ArrayList<>();
    public String personaConsultaArchivo;
    public String camionConsultaArchivo;
    public String fechaConsultaArchivo;
    public String a = "a";
    public String descripcionConsultaArchivo;
    public String tituloConsultaArchivo;
    public int contador;
    public  static EditText nombrearchivotext;
    private int VALOR_RETORNO =2;
    private int VALOR_RETORNO2 =3;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        fecha = sdf.format(calendar.getTime());




        Button mapanuevo = findViewById(R.id.mapanuevo);
        mapanuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Mapa.class);
                startActivity(intent);
            }
        });
        Button mapahecho = findViewById(R.id.mapahecho);
        mapahecho.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(Menu.this);
                View mview = getLayoutInflater().inflate(R.layout.activity_consultar_mapa, null);
                nombrearchivotext = mview.findViewById(R.id.nombrearchivotext);
                Button archivoconsultatext = mview.findViewById(R.id.archivoconsultatext);
                Button directorio = mview.findViewById(R.id.dir);
                directorio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        startActivityForResult(Intent.createChooser(intent,"Escoge el archivo"),VALOR_RETORNO);
                    }
                });
                archivoconsultatext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (nombrearchivotext.getText().toString().isEmpty()) {
                            Toast.makeText(Menu.this, "Campo vacio, verifique", Toast.LENGTH_SHORT).show();
                        } else {
                            String fileName = nombrearchivotext.getText().toString();
                            File file = new File("/storage/emulated/legacy/COPELEC/ROCE/ARCHIVOS", fileName + ".txt");
                            try {
                                FileInputStream fileInputStream = new FileInputStream(file);
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                                String line;
                                bufferedReader.readLine();
                                contador = 0;
                                while ((line = bufferedReader.readLine()) != null) {
                                    if (line.equalsIgnoreCase("a")) {

                                        GPS miGPS = new GPS(latitud, longitud);
                                        ListGps.add(miGPS);
                                        latitud = new ArrayList<>();
                                        longitud = new ArrayList<>();

                                    } else {
                                        StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
                                        while ((stringTokenizer.hasMoreTokens())) {

                                            latitud.add(Double.parseDouble(stringTokenizer.nextToken()));
                                            longitud.add(Double.parseDouble(stringTokenizer.nextToken()));
                                        }
                                    }
                                }

                                Intent intent = new Intent(Menu.this, CargarMapa.class);
                                intent.putExtra("GPS", (Serializable) ListGps);

                                    /*intent.putExtra("longitud", (ArrayList<String>)(longitud));
                                    intent.putExtra("latitud", (ArrayList<String>)(latitud));
                                    intent.putExtra("longitudNo", (ArrayList<String>)(longitudNo));
                                    intent.putExtra("latitudNo", (ArrayList<String>)(latitudNo));*/
                                startActivity(intent);
                                Toast.makeText(Menu.this, "Mapa cargado con exito", Toast.LENGTH_LONG).show();

                            } catch (FileNotFoundException e) {
                                Toast.makeText(Menu.this, "No existe el archivo", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mbuilder.setView(mview);
                AlertDialog dialog = mbuilder.create();
                dialog.show();
            }
        });

        Button foto = findViewById(R.id.foto);
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File imagesfolder = new File("/storage/emulated/legacy/COPELEC/ROCE/", "FOTOS");
                imagesfolder.mkdirs();
                nombrefoto = fecha + ".jpg";
                File imagen = new File(imagesfolder, nombrefoto);
                Uri uri = Uri.fromFile(imagen);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 1);
            }
        });
        Button archivo = findViewById(R.id.archivo);
        archivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(Menu.this);
                View mview = getLayoutInflater().inflate(R.layout.activity_consultar_mapa, null);
                nombrearchivotext = mview.findViewById(R.id.nombrearchivotext);
                Button archivoconsultatext = mview.findViewById(R.id.archivoconsultatext);
                Button directorio = mview.findViewById(R.id.dir);
                directorio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        startActivityForResult(Intent.createChooser(intent,"Escoge el archivo"),VALOR_RETORNO2);
                    }
                });
                archivoconsultatext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (nombrearchivotext.getText().toString().isEmpty()) {
                            Toast.makeText(Menu.this, "Campo vacio, verifique", Toast.LENGTH_SHORT).show();
                        } else {
                            String fileName = nombrearchivotext.getText().toString();
                            File file = new File("/storage/emulated/legacy/COPELEC/ROCE/ARCHIVOS", fileName + ".txt");
                            try {
                                FileInputStream fileInputStream = new FileInputStream(file);
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                                String line;
                                line = bufferedReader.readLine();
                                StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
                                personaConsultaArchivo = stringTokenizer.nextToken();
                                camionConsultaArchivo = stringTokenizer.nextToken();
                                fechaConsultaArchivo = stringTokenizer.nextToken();
                                tituloConsultaArchivo = stringTokenizer.nextToken();
                                descripcionConsultaArchivo = stringTokenizer.nextToken();


                                Intent intent = new Intent(Menu.this, EditarArchivo.class);
                                intent.putExtra("persona", personaConsultaArchivo);
                                intent.putExtra("camion", camionConsultaArchivo);
                                intent.putExtra("fecha", fechaConsultaArchivo);
                                intent.putExtra("titulo", tituloConsultaArchivo);
                                intent.putExtra("descripcion", descripcionConsultaArchivo);
                                intent.putExtra("file", fileName);
                                startActivity(intent);
                                Toast.makeText(Menu.this, "Edite su archivo", Toast.LENGTH_SHORT).show();

                            } catch (FileNotFoundException e) {
                                Toast.makeText(Menu.this, "No existe el archivo", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mbuilder.setView(mview);
                AlertDialog dialog = mbuilder.create();
                dialog.show();
            }
        });

        Button correo = findViewById(R.id.correo);
        correo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isNetWorkAvailable()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Menu.this);
                    alertDialog.setTitle("Error de conexión!!");
                    alertDialog.setMessage("Intenta conectarte a internet si quieres enviar un correo");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
                    AlertDialog alert = alertDialog.create();
                    alert.show();
                } else {
                    Intent intent = new Intent(Menu.this, Email.class);
                    startActivity(intent);
                    Toast.makeText(Menu.this, "Envía tu correo", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case 1:
                    Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/legacy/COPELEC/ROCE/FOTOS/"+nombrefoto);
                    break;
                case 2:
                    Uri uri = data.getData();
                    File file = new File(uri.getPath());
                    nombrearchivotext.setText(file.getName().split("\\.", 2)[0]);
                    break;
                case 3:
                    Uri uri2 = data.getData();
                    File file2 = new File(uri2.getPath());
                    nombrearchivotext.setText(file2.getName().split("\\.", 2)[0]);
                    break;
            }
        }
    }

    private boolean isNetWorkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo !=null;
    }

}

