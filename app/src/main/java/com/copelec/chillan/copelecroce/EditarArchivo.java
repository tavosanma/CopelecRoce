package com.copelec.chillan.copelecroce;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class EditarArchivo extends AppCompatActivity {

    public static String fechaConsultaArchivo;
    public static String addCooordenadasArchivoKMZ="";
    public static String tituloModifyArchivo;
    public static String camionModifyArchivo;
    public static String descripcionModifyArchivo;
    public String linea;
    public String linea3;
    public String personaConsultaArchivo;
    public String camionConsultaArchivo;
    public String nameFile;
    public String personaModifyArchivo;
    public String descripcionConsultaArchivo;
    public String tituloConsultaArchivo;
    public String coordenadasArchivo="";
    public String addCooordenadasArchivo="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_archivo);
        if (!checkPermission()) {
            requestPermissions();
        }
        final EditText usuario = findViewById(R.id.usuario);
        final EditText camion = findViewById(R.id.camion);
        final EditText descripcion = findViewById(R.id.descripcion);
        final EditText titulo = findViewById(R.id.titulo);

        personaConsultaArchivo = getIntent().getStringExtra("persona");
        camionConsultaArchivo = getIntent().getStringExtra("camion");
        fechaConsultaArchivo = getIntent().getStringExtra("fecha");
        tituloConsultaArchivo=getIntent().getStringExtra("titulo");
        descripcionConsultaArchivo = getIntent().getStringExtra("descripcion");

        usuario.setText(personaConsultaArchivo);
        camion.setText(camionConsultaArchivo);
        descripcion.setText(descripcionConsultaArchivo);
        titulo.setText(tituloConsultaArchivo);

        nameFile =  getIntent().getStringExtra("file");


        Button editar = findViewById(R.id.botoneditfile);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personaModifyArchivo=usuario.getText().toString();
                camionModifyArchivo=camion.getText().toString();
                descripcionModifyArchivo=descripcion.getText().toString();
                tituloModifyArchivo=titulo.getText().toString();
                try {
                    File file = new File("/storage/emulated/legacy/COPELEC/ROCE/ARCHIVOS",nameFile+".txt");
                    FileInputStream fileInputStream = new FileInputStream(file);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                    bufferedReader.readLine();
                    while ((coordenadasArchivo=bufferedReader.readLine())!=null){
                        addCooordenadasArchivo+=coordenadasArchivo+"\n";
                        addCooordenadasArchivoKMZ+=coordenadasArchivo+","+"0 ";
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    linea = "" + personaModifyArchivo + ";" + camionModifyArchivo + ";" + fechaConsultaArchivo+";"+tituloModifyArchivo+";"+descripcionModifyArchivo;
                    linea3 = linea+"\n"+ addCooordenadasArchivo;

                    File dir = new File("/storage/emulated/legacy/COPELEC/ROCE/ARCHIVOS/");
                    dir.mkdirs();
                    File ModifyFile = new File(dir, nameFile+".txt");
                    FileOutputStream fileOutputStream = new FileOutputStream(ModifyFile.getAbsolutePath());
                    fileOutputStream.write(linea3.getBytes());
                    fileOutputStream.close();
                    exportMarkersToKMZfile(nameFile);
                    Intent intent = new Intent(EditarArchivo.this,Menu.class);
                    startActivity(intent);
                    Toast.makeText(EditarArchivo.this,"Archivo Editado",Toast.LENGTH_LONG).show();

                }catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
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
        kmlStringBuilder.append("<Placemark>\n");
        kmlStringBuilder.append(String.format("<name>%s</name>\n",tituloModifyArchivo));
        kmlStringBuilder.append(String.format("<description>&quot;%s&quot;\n\nVehículo:&quot;%s&quot;\n\n%s</description>\n",fechaConsultaArchivo,camionModifyArchivo,descripcionModifyArchivo));
        kmlStringBuilder.append("<styleUrl>#transPurpleLineGreenPoly</styleUrl>\n");
        kmlStringBuilder.append("<LineString>\n");
        kmlStringBuilder.append("<coordinates>\n");
        kmlStringBuilder.append(String.format("%s\n", addCooordenadasArchivoKMZ));
        kmlStringBuilder.append("</coordinates>\n");
        kmlStringBuilder.append("</LineString>\n");
        kmlStringBuilder.append("</Placemark>\n");
        kmlStringBuilder.append("</Document>\n");
        kmlStringBuilder.append("</kml>\n");
        return kmlStringBuilder.toString();
    }

    public static void exportMarkersToKMZfile(String nameFile ) {
        try {

            String kmlString = exportMarkersToKML();

            File kmzFolder = new File("/storage/emulated/legacy/COPELEC/ROCE/ARCHIVOS");
            if (!kmzFolder.exists()) {
                kmzFolder.mkdirs();
            }
            File kmzFile = new File(kmzFolder, nameFile+".kml");
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
        ActivityCompat.requestPermissions(EditarArchivo.this,new String[]{WRITE_EXTERNAL_STORAGE}, 1);
    }
}
