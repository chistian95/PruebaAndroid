package com.example.dam32_corral.coches;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {

    RadioButton rb_propietarios, rb_coches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rb_coches = (RadioButton) findViewById(R.id.rb_coches);
        rb_propietarios = (RadioButton) findViewById(R.id.rb_propietarios);
    }

    public void alta(View v) {
        if(rb_coches.isChecked()) {
            Intent i = new Intent(this, Coche.class);
            i.putExtra("tipo", "alta");
            startActivity(i);
        } else if(rb_propietarios.isChecked()) {
            Intent i = new Intent(this, Propietario.class);
            i.putExtra("tipo", "alta");
            startActivity(i);
        }
    }

    public void baja(View v) {
        if(rb_coches.isChecked()) {
            Intent i = new Intent(this, Coche.class);
            i.putExtra("tipo", "baja");
            startActivity(i);
        } else if(rb_propietarios.isChecked()) {
            Intent i = new Intent(this, Propietario.class);
            i.putExtra("tipo", "baja");
            startActivity(i);
        }
    }

    public void consulta(View v) {
        if(rb_coches.isChecked()) {
            Intent i = new Intent(this, ListViewCoches.class);
            startActivity(i);
        } else if(rb_propietarios.isChecked()) {
            Intent i = new Intent(this, Propietario.class);
            i.putExtra("tipo", "consulta");
            startActivity(i);
        }
    }

    public void modificar(View v) {
        if(rb_coches.isChecked()) {
            Intent i = new Intent(this, Coche.class);
            i.putExtra("tipo", "modificar");
            startActivity(i);
        } else if(rb_propietarios.isChecked()) {
            Intent i = new Intent(this, Propietario.class);
            i.putExtra("tipo", "modificar");
            startActivity(i);
        }
    }
}
