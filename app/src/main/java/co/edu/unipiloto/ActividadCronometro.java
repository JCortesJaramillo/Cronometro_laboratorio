package co.edu.unipiloto;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ActividadCronometro extends AppCompatActivity {

    private final Handler handler = new Handler();
    private int segundos = 0;
    private boolean activo = false;
    private TextView tiempo;
    private ArrayList<String> listaParcial;
    private ArrayAdapter<String> adaptador;
    private int contadorParcial = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_cronometro);

        tiempo = findViewById(R.id.tiempo);
        listaParcial = new ArrayList<>();
        ListView lapsListView = findViewById(R.id.listaParcial);

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaParcial);
        lapsListView.setAdapter(adaptador);

        if (savedInstanceState != null) {
            segundos = savedInstanceState.getInt("segundos");
            listaParcial = savedInstanceState.getStringArrayList("tiemposParciales");
            contadorParcial = savedInstanceState.getInt("contadorParcial");
            actualizarCronometro();
        }

        Button startButton = findViewById(R.id.botonInicio);
        Button stopButton = findViewById(R.id.botonFinal);
        Button lapButton = findViewById(R.id.botonParcial);

        startButton.setOnClickListener(v -> startTimer());
        stopButton.setOnClickListener(v -> detenerCronometro());
        lapButton.setOnClickListener(v -> registrarParcial());
    }

    private void startTimer() {
        if (!activo) {
            activo = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (activo) {
                        segundos++;
                        actualizarCronometro();
                        handler.postDelayed(this, 1000);
                    }
                }
            }, 1000);
        }
    }

    private void detenerCronometro() {
        activo = false;
    }

    private void registrarParcial() {
        if (contadorParcial < 5) {
            int minutes = segundos / 60;
            int remainingSeconds = segundos % 60;
            String tiempoParcial = String.format("Parcial %d: %02d:%02d", contadorParcial + 1, minutes, remainingSeconds);
            listaParcial.add(tiempoParcial);
            contadorParcial++;
            adaptador.notifyDataSetChanged();
        }

        if (contadorParcial == 5) {
            detenerCronometro();
            mostrarTiemposParciales();
        }
    }

    private void mostrarTiemposParciales() {
        StringBuilder reporteParcial = new StringBuilder("Parciales:\n");
        for (String tiempoParcial : listaParcial) {
            reporteParcial.append(tiempoParcial).append("\n");
        }
        System.out.println(reporteParcial);
    }

    private void actualizarCronometro() {
        int minutos = segundos / 60;
        int segundosFaltantes = segundos % 60;

        String time = String.format("%02d:%02d", minutos, segundosFaltantes);
        tiempo.setText(time);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("segundos", segundos);
        outState.putStringArrayList("tiempoParcial", listaParcial);
        outState.putInt("contadorParcial", contadorParcial);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        segundos = savedInstanceState.getInt("segundos");
        listaParcial = savedInstanceState.getStringArrayList("tiempoParcial");
        contadorParcial = savedInstanceState.getInt("contadorParcial");
        actualizarCronometro();
    }
}