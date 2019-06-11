package com.copelec.chillan.copelecroce;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class GuardarMapa extends AppCompatActivity {

    public static String descripcion;
    public static String titulo;
    public static  String archivo;
    public static String camion;
    public static String resultTemp="";
    public static int contadorPausa;
    public static List<String> ArrayCoordenadas;
    public static String fecha;
    public String nombrefoto;
    public String persona;
    public String linea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar_mapa);
        if (!checkPermission()) {
            requestPermissions();
        }
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        fecha = sdf.format(calendar.getTime());
        final EditText mPersona = findViewById(R.id.personacargo);
        final EditText mCamion = findViewById(R.id.camioncargo);
        final EditText mArchivo = findViewById(R.id.nombrearchivo);
        final EditText mdescripcion = findViewById(R.id.descripcion);
        final EditText mtitulo = findViewById(R.id.titulo);
        Button foto = findViewById(R.id.botonfoto);


        Button mguardartodo = findViewById(R.id.guardartodo);

        mguardartodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPersona.getText().toString().isEmpty() || mCamion.getText().toString().isEmpty() ||
                        mArchivo.getText().toString().isEmpty()|| mdescripcion.getText().toString().isEmpty()||
                        mtitulo.getText().toString().isEmpty()) {
                    Toast.makeText(GuardarMapa.this, "Campos vacios, verifique", Toast.LENGTH_SHORT).show();
                } else {
                    persona = mPersona.getText().toString();
                    camion = mCamion.getText().toString();
                    archivo = mArchivo.getText().toString();
                    descripcion=mdescripcion.getText().toString();
                    titulo=mtitulo.getText().toString();
                    linea = "" + persona + ";" + camion + ";" + fecha+";"+titulo+";"+descripcion;
                    try {
                        String lineTemp="";
                        File temp = new File("/storage/emulated/legacy/COPELEC/ROCE/ARCHIVOS","temp.txt");
                        FileInputStream fileInputStream = new FileInputStream(temp);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                        while ((lineTemp=bufferedReader.readLine())!=null){
                            resultTemp = resultTemp + lineTemp +"\n";
                        }
                        resultTemp = linea +"\n"+resultTemp;
                        temp.delete();
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    try {

                         ArrayCoordenadas = getIntent().getStringArrayListExtra("arraykmz");
                         contadorPausa = getIntent().getIntExtra("contadorPausa",0);

                        File dir = new File("/storage/emulated/legacy/COPELEC/ROCE/ARCHIVOS");
                        dir.mkdirs();
                        File file = new File(dir, archivo + ".txt");
                        if(file.exists()){
                            Toast.makeText(GuardarMapa.this,"Ya existe un archivo con este nombre",Toast.LENGTH_SHORT).show();
                        }else {
                            FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());
                            fileOutputStream.write(resultTemp.getBytes());
                            fileOutputStream.close();
                            exportMarkersToKMZfile(archivo);
                            AlertDialog.Builder altdial = new AlertDialog.Builder(GuardarMapa.this);
                            altdial.setMessage("¿Tienes internet y deseas enviar los archivos por correo?").setCancelable(false)
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(GuardarMapa.this,Email.class);
                                            startActivity(intent);
                                            linea="";
                                            contadorPausa=0;
                                            resultTemp="";
                                            ArrayCoordenadas= new ArrayList<>();
                                        }
                                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(GuardarMapa.this,Menu.class);

                                    startActivity(intent);
                                    linea="";
                                    contadorPausa=0;
                                    resultTemp="";
                                    ArrayCoordenadas= new ArrayList<>();
                                    Toast.makeText(GuardarMapa.this,"Archivo guardado en tu teléfono",Toast.LENGTH_LONG).show();
                                }
                            });
                            AlertDialog alert = altdial.create();
                            alert.setTitle("Envía los datos por Gmail");
                            alert.show();
                        }
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

        });

       foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
               File imagesfolder = new File("/storage/emulated/legacy/COPELEC/ROCE/","FOTOS");
               imagesfolder.mkdirs();
               nombrefoto = mArchivo.getText().toString()+".jpg";
               File imagen = new File(imagesfolder,nombrefoto);
               Uri uri = Uri.fromFile(imagen);
               intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
               startActivityForResult(intent,1);
               Toast.makeText(GuardarMapa.this,"Fotografía tomada con éxito",Toast.LENGTH_SHORT);
           }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (resultCode==RESULT_OK){
                switch (requestCode){
                    case 1:
                       Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/legacy/COPELEC/ROCE/FOTOS/"+nombrefoto);
                       ImageView textimage = findViewById(R.id.textimage);
                       textimage.setImageBitmap(bitmap);

                       break;
                }
            }
        }

    public static String exportMarkersToKML() {
        StringBuilder kmlStringBuilder = new StringBuilder(9999);

        kmlStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        kmlStringBuilder.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        kmlStringBuilder.append("<Document>\n");
        kmlStringBuilder.append("<name>Copelec Roces</name>\n");
        kmlStringBuilder.append("<Style id=\"transPurpleLineGreenPoly\">\n");
        kmlStringBuilder.append("<LineStyle>\n");
        kmlStringBuilder.append("<color>#7d00ff00</color>\n");
        kmlStringBuilder.append("<width>4</width>\n");
        kmlStringBuilder.append("</LineStyle>\n");
        kmlStringBuilder.append("<PolyStyle>\n");
        kmlStringBuilder.append("<color>#7d00ff00</color>\n");
        kmlStringBuilder.append("</PolyStyle>\n");
        kmlStringBuilder.append("</Style>\n");
        for(int i = 0;i<=contadorPausa;i++) {
            kmlStringBuilder.append("<Placemark>\n");
            kmlStringBuilder.append(String.format("<name>%s</name>\n", titulo));
            kmlStringBuilder.append(String.format("<description>&quot;%s&quot;\n\nVehículo:&quot;%s&quot;\n\n%s</description>\n", fecha, camion, descripcion));
            kmlStringBuilder.append("<styleUrl>#transPurpleLineGreenPoly</styleUrl>\n");
            kmlStringBuilder.append("<LineString>\n");
            kmlStringBuilder.append("<coordinates>\n");
            kmlStringBuilder.append(String.format("%s\n", ArrayCoordenadas.get(i)));
            kmlStringBuilder.append("</coordinates>\n");
            kmlStringBuilder.append("</LineString>\n");
            kmlStringBuilder.append("</Placemark>\n");
        }
        kmlStringBuilder.append("</Document>\n");
        kmlStringBuilder.append("</kml>\n");
        return kmlStringBuilder.toString();
    }

    public static void exportMarkersToKMZfile(String archivo ) {
        try {
            String kmlString = exportMarkersToKML();
            File kmzFolder = new File("/storage/emulated/legacy/COPELEC/ROCE/ARCHIVOS");
            if (!kmzFolder.exists()) {
                kmzFolder.mkdirs();
            }
            File kmzFile = new File(kmzFolder, archivo+".kml");
            FileOutputStream fileOutputStream = new FileOutputStream(kmzFile);
            fileOutputStream.write(kmlString.getBytes());
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(GuardarMapa.this,new String[]{WRITE_EXTERNAL_STORAGE}, 1);
    }

}