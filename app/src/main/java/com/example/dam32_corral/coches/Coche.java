package com.example.dam32_corral.coches;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Coche extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Button bt_aceptar, bt_volver;
    EditText et_matricula, et_marca, et_potencia, et_dni;
    TextView tv_marca, tv_potencia, tv_dni, tv_coches;
    Spinner sp_dni, sp_matricula;
    String tipo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coche);

        bt_aceptar = (Button) findViewById(R.id.bt_aceptar);
        bt_volver = (Button) findViewById(R.id.bt_volver);

        et_matricula = (EditText) findViewById(R.id.et_matricula);
        et_marca = (EditText) findViewById(R.id.et_marca);
        et_potencia = (EditText) findViewById(R.id.et_potencia);
        et_dni = (EditText) findViewById(R.id.et_dni);

        tv_marca = (TextView) findViewById(R.id.tv_marca);
        tv_potencia = (TextView) findViewById(R.id.tv_potencia);
        tv_dni = (TextView) findViewById(R.id.tv_dni);
        tv_coches = (TextView) findViewById(R.id.tv_coches);

        sp_dni = (Spinner) findViewById(R.id.sp_dni);
        sp_matricula = (Spinner) findViewById(R.id.sp_matricula);
        sp_matricula.setOnItemSelectedListener(this);

        tipo = getIntent().getStringExtra("tipo");

        switch (tipo) {
            case "alta":
                sp_matricula.setVisibility(View.INVISIBLE);
                et_dni.setVisibility(View.INVISIBLE);
                bt_aceptar.setText("GRABAR");
                tv_coches.setText("Dar Alta Coche");

                cargarDni();
                break;
            case "baja":
                bt_aceptar.setText("BORRAR");
                tv_coches.setText("Dar Baja Coche");

                et_matricula.setEnabled(false);
                et_marca.setEnabled(false);
                et_potencia.setEnabled(false);
                et_dni.setEnabled(false);
                sp_dni.setVisibility(View.INVISIBLE);

                cargarMatricula();
                break;
            case "consulta":
                bt_aceptar.setVisibility(View.INVISIBLE);
                tv_coches.setText("Consultar Coche");

                et_matricula.setVisibility(View.INVISIBLE);
                et_marca.setEnabled(false);
                et_potencia.setEnabled(false);
                et_dni.setEnabled(false);
                sp_dni.setVisibility(View.INVISIBLE);

                cargarMatricula();
                break;
            case "modificar":
                bt_aceptar.setText("MODIFICAR");
                tv_coches.setText("Modificar Coche");

                et_dni.setVisibility(View.INVISIBLE);
                et_matricula.setVisibility(View.INVISIBLE);

                cargarDni();
                cargarMatricula();
                break;
        }

    }

    private void cargarDni() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "seguros", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        List<String> listaDni = new ArrayList<>();
        Cursor fila = db.rawQuery("SELECT dni FROM propietarios", null);

        if(fila.getCount() <= 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, new String[]{""});
            sp_dni.setAdapter(adapter);
            return;
        }

        fila.moveToFirst();
        boolean primer = true;
        do {
            if(primer) {
                primer = false;
            } else {
                fila.moveToNext();
            }

            String dni = fila.getString(0);
            listaDni.add(dni);
        } while(!fila.isLast());
        fila.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, listaDni);
        sp_dni.setAdapter(adapter);
    }

    private void cargarMatricula() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "seguros", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        List<String> listaMatricula = new ArrayList<>();
        Cursor fila = db.rawQuery("SELECT matricula FROM coches", null);

        if(fila.getCount() <= 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, new String[]{""});
            sp_matricula.setAdapter(adapter);
            return;
        }

        fila.moveToFirst();
        boolean primer = true;
        do {
            if(primer) {
                primer = false;
            } else {
                fila.moveToNext();
            }

            String matricula = fila.getString(0);
            listaMatricula.add(matricula);
        } while(!fila.isLast());
        fila.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, listaMatricula);
        sp_matricula.setAdapter(adapter);

        String matricula_sel = sp_matricula.getSelectedItem().toString();
        Cursor fila_sel = db.rawQuery("SELECT marca, potencia, dni FROM coches WHERE matricula='"+matricula_sel+"'", null);
        if(fila_sel.getCount() > 0 && fila_sel.moveToFirst()) {
            String marca_sel = fila_sel.getString(0);
            String potencia_sel = fila_sel.getString(1);
            String dni_sel = fila_sel.getString(2);

            et_marca.setText(marca_sel);
            et_potencia.setText(potencia_sel);
            et_dni.setText(dni_sel);
        }
        fila_sel.close();

        db.close();
    }

    public void aceptar(View v) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "seguros", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        switch (tipo) {
            case "alta":
                darAlta(db);
                break;
            case "baja":
                darBaja(db);
                break;
            case "consulta":
                consultar(db);
                break;
            case "modificar":
                modificar(db);
                break;
        }

        db.close();
    }

    private void darAlta(SQLiteDatabase db) {
        String matricula = et_matricula.getText().toString();
        String marca = et_marca.getText().toString();
        String potencia = et_potencia.getText().toString();

        if(sp_dni.getSelectedItem() == null) {
            Toast.makeText(this, "No hay propietarios creados!", Toast.LENGTH_SHORT).show();
            return;
        }
        String dni = sp_dni.getSelectedItem().toString();

        if(matricula.length() <= 0) {
            Toast.makeText(this, "Debes rellenar el campo matricula!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(marca.length() <= 0) {
            Toast.makeText(this, "Debes rellenar el campo marca!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(potencia.length() <= 0) {
            Toast.makeText(this, "Debes rellenar el campo potencia!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(dni.length() <= 0) {
            Toast.makeText(this, "Debes seleccionar un dni!", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor fila = db.rawQuery("SELECT matricula FROM coches WHERE matricula='"+matricula+"'", null);
        if(fila.moveToFirst()) {
            Toast.makeText(this, "Ya existe un coche con esa matricula!", Toast.LENGTH_SHORT).show();
            return;
        }
        fila.close();

        ContentValues values = new ContentValues();
        values.put("matricula", matricula);
        values.put("marca", marca);
        values.put("potencia", potencia);
        values.put("dni", dni);
        db.insert("coches", null, values);

        et_matricula.setText("");
        et_marca.setText("");
        et_potencia.setText("");
        cargarDni();

        Toast.makeText(this, "Coche creado!", Toast.LENGTH_SHORT).show();
    }

    private void darBaja(SQLiteDatabase db) {
        if(sp_matricula.getSelectedItem() == null) {
            Toast.makeText(this, "No hay coches creados!", Toast.LENGTH_SHORT).show();
            return;
        }
        String matricula = sp_matricula.getSelectedItem().toString();

        if(matricula.length() <= 0) {
            Toast.makeText(this, "Debes elegir una matricula!", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor fila = db.rawQuery("SELECT matricula FROM coches WHERE matricula='"+matricula+"'", null);
        if(!fila.moveToFirst()) {
            Toast.makeText(this, "No existe ese coche!", Toast.LENGTH_SHORT).show();
            return;
        }
        fila.close();

        db.execSQL("DELETE FROM coches WHERE matricula='"+matricula+"'");
        cargarMatricula();

        Toast.makeText(this, "Coche borrado!", Toast.LENGTH_SHORT).show();
    }

    private void consultar(SQLiteDatabase db) {
        if(sp_matricula.getSelectedItem() == null) {
            Toast.makeText(this, "No hay coches creados!", Toast.LENGTH_SHORT).show();
            return;
        }
        String matricula = sp_matricula.getSelectedItem().toString();

        if(matricula.length() <= 0) {
            Toast.makeText(this, "Debes seleccionar una matricula!", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor fila = db.rawQuery("SELECT marca, potencia, dni FROM coches WHERE matricula='"+matricula+"'", null);
        if(!fila.moveToFirst()) {
            Toast.makeText(this, "No existe ese coche!", Toast.LENGTH_SHORT).show();
            return;
        }

        et_marca.setText(fila.getString(0));
        et_potencia.setText(fila.getString(1));
        et_dni.setText(fila.getString(2));

        fila.close();
    }

    private void modificar(SQLiteDatabase db) {
        if(sp_matricula.getSelectedItem() == null) {
            Toast.makeText(this, "No hay coches creados!", Toast.LENGTH_SHORT).show();
            return;
        }
        String matricula = sp_matricula.getSelectedItem().toString();
        String marca = et_marca.getText().toString();
        String potencia = et_potencia.getText().toString();

        if(sp_dni.getSelectedItem() == null) {
            Toast.makeText(this, "No hay propietarios creados!", Toast.LENGTH_SHORT).show();
            return;
        }
        String dni = sp_dni.getSelectedItem().toString();

        if(matricula.length() <= 0) {
            Toast.makeText(this, "No has elegido una matricula!", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor fila = db.rawQuery("SELECT matricula FROM coches WHERE matricula='"+matricula+"'", null);
        if(!fila.moveToFirst()) {
            Toast.makeText(this, "No existe ese coche!", Toast.LENGTH_SHORT).show();
            return;
        }
        fila.close();

        ContentValues values = new ContentValues();
        values.put("matricula", matricula);

        if(marca.length() > 0) {
            values.put("marca", marca);
        }
        if(potencia.length() > 0) {
            values.put("potencia", potencia);
        }
        if(dni.length() > 0) {
            values.put("dni", dni);
        }

        db.update("coches", values, "matricula='"+matricula+"'", null);
        Toast.makeText(this, "Coche modificado!", Toast.LENGTH_SHORT).show();

        et_marca.setText("");
        et_potencia.setText("");
        cargarDni();
        cargarMatricula();
    }

    public void volver(View v) {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "seguros", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        String matricula_sel = parent.getItemAtPosition(position).toString();
        Cursor fila = db.rawQuery("SELECT marca, potencia, dni FROM coches WHERE matricula='"+matricula_sel+"'", null);

        if(fila.getCount() > 0 && fila.moveToFirst()) {
            String marca_sel = fila.getString(0);
            String potencia_sel = fila.getString(1);
            String dni_sel = fila.getString(2);

            et_marca.setText(marca_sel);
            et_potencia.setText(potencia_sel);
            et_dni.setText(dni_sel);
            for(int i=0; i<sp_dni.getCount(); i++) {
                if(sp_dni.getItemAtPosition(i).toString().equals(dni_sel)) {
                    sp_dni.setSelection(i, true);
                    break;
                }
            }
        }
        fila.close();

        db.close();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
