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
    private int ultimoParcial = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_cronometro);

        tiempo = findViewById(R.id.tiempo);
        listaParcial = new ArrayList<>();
        ListView listaParcial = findViewById(R.id.listaParcial);

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.listaParcial);
        listaParcial.setAdapter(adaptador);

        if (savedInstanceState != null) {
            segundos = savedInstanceState.getInt("segundos");
            this.listaParcial = savedInstanceState.getStringArrayList("tiempoParcial");
            contadorParcial = savedInstanceState.getInt("contadorParcial");
            actualizarCronometro();
        }

        Button botonInicio = findViewById(R.id.botonInicio);
        Button botonAlto = findViewById(R.id.botonFinal);
        Button botonParcial = findViewById(R.id.botonParcial);
        botonParcial.setEnabled(false);
        Button botonReinicio = findViewById(R.id.botonReset);

        botonInicio.setOnClickListener(v -> {startTimer(); botonParcial.setEnabled(true);});
        botonAlto.setOnClickListener(v -> detenerCronometro());
        botonParcial.setOnClickListener(v -> registrarParcial());
        botonReinicio.setOnClickListener(v -> {resetearCronometro(); botonParcial.setEnabled(false);});
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

    private void resetearCronometro() {
        activo = false;
        segundos = 0;
        contadorParcial = 0;
        ultimoParcial = 0;
        listaParcial.clear();

        actualizarCronometro();
        adaptador.notifyDataSetChanged();
    }

    private void registrarParcial() {
        if (contadorParcial < 5) {
            int diferencia = segundos - ultimoParcial;
            int minutos = diferencia / 60;
            int segundos = diferencia % 60;
            String tiempoParcial = String.format("Parcial %d: %02d:%02d", contadorParcial + 1, minutos, segundos);
            listaParcial.add(tiempoParcial);
            contadorParcial++;
            ultimoParcial = this.segundos;
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
        adaptador.notifyDataSetChanged();
        actualizarCronometro();
    }
}