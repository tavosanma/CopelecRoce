package com.copelec.chillan.copelecroce;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CargarMapa extends AppCompatActivity  implements LocationSource, OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private LocationSource.OnLocationChangedListener listener;
    private Location mCurrentLocation;
    private LocationManager mLocationManager;
    public static List<Double> longitud;
    public static List<Double> latitud;
    public static List<GPS> GPS;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_mapa);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean enabledGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabledGPS) {
            Toast.makeText(this, "No hay señal de GPS", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent(CargarMapa.this,Mapa.class);
        startActivity(intent);
    }

    /**
     * gestionamos la respuesta de la petición de permisos
     **/
   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            /** si el permiso fue aceptado, iniciamos el proceso de captura de posiciones **/

           enableMyLocation();
        } else {
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Si realizamos un click sobre su posicion (punto azul), mostraremos información acerca de ese punto
     **/
    @Override
    public void onMyLocationClick(@NonNull final Location location) {
        Toast.makeText(this, "Posición actual:\n" + "Latitude:"+location.getLatitude()+"\n"+ "Longitud:" + location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    /**
     * Dialogo de error para cuando no se acepte el permiso
     **/
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    /**
     * el mapa se encuentra listo, podemos modificar algunas configuraciones
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Bundle b = getIntent().getExtras();
        GPS = (ArrayList<GPS>)getIntent().getSerializableExtra("GPS");
        // GPS = (ArrayList<GPS>) b.getSerializable("GPS");

        for (GPS miGPS : GPS){
            PolylineOptions polylineOptions = new PolylineOptions();
            longitud = (miGPS.getLongitud());
            latitud = (miGPS.getLatitud());

            for (int i =0;i<longitud.size();i++){
                polylineOptions.add(new LatLng(latitud.get(i),longitud.get(i)));
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitud.get(i),longitud.get(i))).title(latitud.get(i)+"/"+longitud.get(i)));
            }
            polylineOptions.geodesic(true).width(10).color(Color.GREEN);
            mMap.addPolyline(polylineOptions);

        }

        /*
        longi = getIntent().getStringArrayListExtra("longitud");
        lat = getIntent().getStringArrayListExtra("latitud");
        longiNo = getIntent().getStringArrayListExtra("longitudNo");
        latNo = getIntent().getStringArrayListExtra("latitudNo");

        Double[] longitud = new Double[longi.size()];
        Double[] latitud = new Double[lat.size()];
        Double[] longitudNo = new Double[longiNo.size()];
        Double[] latitudNo = new Double[latNo.size()];
        for(int i =0;i<longi.size();i++){
            longitud[i] = Double.parseDouble(longi.get(i));
        }
        for(int i =0;i<lat.size();i++){
            latitud[i] = Double.parseDouble(lat.get(i));
        }
        for (int i =0;i<longitud.length;i++){
            coordlist.add(new LatLng(latitud[i],longitud[i]));
        }
        for(int i =0;i<longiNo.size();i++){
            longitudNo[i] = Double.parseDouble(longiNo.get(i));
        }
        for(int i =0;i<latNo.size();i++){
            latitudNo[i] = Double.parseDouble(latNo.get(i));
        }
        for (int i =0;i<longitudNo.length;i++){
            coordlistNo.add(new LatLng(latitudNo[i],longitudNo[i]));
        }*/

        /** Activacion de controles en el mapa **/
        mMap.getUiSettings().setZoomControlsEnabled(true); //control de zoom
        mMap.getUiSettings().setMyLocationButtonEnabled(true); //habilitamos boton para regresar a su posicion
        mMap.getUiSettings().setCompassEnabled(true); //el mapa busca el norte
        mMap.getUiSettings().setZoomControlsEnabled(true);//click sobre la informacion de un marcador

        /** Gestión de algunos eventos**/
        mMap.setOnMyLocationClickListener(this); //click sobre posicion

        /** iniciamos el proceso de captura de posiciones **/
        enableMyLocation();
    }

    @Override
    public void onBackPressed() {
         List<Double> longitud =new ArrayList<>();
         List<Double> latitud = new ArrayList<>();
         List<GPS> GPS = new ArrayList<>();
         Intent intent = new Intent(CargarMapa.this,Menu.class);
         startActivity(intent);
    }

    public void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            /** Si el permiso no fue concedido o no ha sido solicitado, se solicita **/
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            /** Cuando ya tenemos los permisos
             * le decimos al mapa que capture la posicion y
             * modificamos de donde se obtiene la posición,
             * con el objetivo de contralar como y cuando se actualiza **/
            mMap.setMyLocationEnabled(true);
            mMap.setLocationSource(this);

            /** Se le dice de donde se captura la posicion, en este caso el GPS(puede ser desde internet),
             * el intervalo de actualizacion,
             * la distancia minima que debe modificar la posicion para ser requerida una actualizacion
             * y el evento que capura la posicion**/

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 2, new android.location.LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    /** cuando se captura una nueva posicion, se le entrega al evento que fue seteado en el mapa
                     * para que sea consiente de su posicion.
                     * En caso de necesitar tracking de posicion, en este punto se debe el iniciar el SW de trackeo **/

                    CargarMapa.this.listener.onLocationChanged(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });

            /** el mapa es consciente de la posción, pero necesitamos entregarle
             * la primera posicion al mapa para que cambie la vista entregada, siempre
             * y cuando, el telefono haya registrado su posicion antes. **/
            mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (mCurrentLocation != null) {
                this.listener.onLocationChanged(mCurrentLocation);
                /** el objeto Location no es compatible con el mapa, por lo cual debemos crear un objeto
                 * compatible con este (LatLng) **/
                LatLng thisLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                /** movemos el mapa a la posicion obtenida **/
                mMap.moveCamera(CameraUpdateFactory.newLatLng(thisLocation));
                /** y le indicamos que establezca un zoom 18, entre mayor sea mas cerca se mostrará **/
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.listener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        this.listener = null;
    }

    /**
     * Evento que se activa al realizar click sobre un marcador
     **/

    private void generateToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * parser para que obtiene los datos necesarios para crear un objeto Polyline para el mapa
     **/
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.rgb(0, 0, 255));
            }
            if (lineOptions != null) {
                Log.d("ssss", "ruta");
                mMap.addPolyline(lineOptions);
            }
        }
    }

    public class DirectionsJSONParser {

        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<HashMap<String, String>>();

                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }

            return routes;
        }

        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }

}
