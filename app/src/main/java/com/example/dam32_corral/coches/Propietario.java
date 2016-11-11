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

public class Propietario extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button bt_aceptar, bt_volver;
    EditText et_nombre, et_dni, et_edad;
    TextView tv_nombre, tv_edad, tv_propietarios;
    Spinner sp_dni;
    String tipo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propietario);

        bt_aceptar = (Button) findViewById(R.id.bt_aceptar);
        bt_volver = (Button) findViewById(R.id.bt_volver);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        et_dni = (EditText) findViewById(R.id.et_dni);
        et_edad = (EditText) findViewById(R.id.et_edad);

        tv_nombre = (TextView) findViewById(R.id.tv_nombre);
        tv_edad = (TextView) findViewById(R.id.tv_edad);
        tv_propietarios = (TextView) findViewById(R.id.tv_propietarios);

        sp_dni = (Spinner) findViewById(R.id.sp_dni);
        sp_dni.setOnItemSelectedListener(this);

        tipo = getIntent().getStringExtra("tipo");

        switch (tipo) {
            case "alta":
                sp_dni.setVisibility(View.INVISIBLE);
                bt_aceptar.setText("GRABAR");
                tv_propietarios.setText("Dar Alta Propietario");
                break;
            case "baja":
                bt_aceptar.setText("BORRAR");
                tv_propietarios.setText("Dar Baja Propietario");
                et_dni.setVisibility(View.INVISIBLE);
                et_nombre.setEnabled(false);
                et_edad.setEnabled(false);
                cargarDni();
                break;
            case "consulta":
                tv_propietarios.setText("Consultar Propietario");
                bt_aceptar.setVisibility(View.INVISIBLE);

                et_nombre.setEnabled(false);
                et_edad.setEnabled(false);
                et_dni.setVisibility(View.INVISIBLE);
                cargarDni();
                break;
            case "modificar":
                tv_propietarios.setText("Modificar Propietario");
                bt_aceptar.setText("MODIFICAR");
                et_dni.setVisibility(View.INVISIBLE);
                cargarDni();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, listaDni);
        sp_dni.setAdapter(adapter);

        if(sp_dni.getSelectedItem() != null) {
            String dni = sp_dni.getSelectedItem().toString();

            Cursor filaSelect = db.rawQuery("SELECT nombre, edad FROM propietarios WHERE dni="+dni, null);
            if(filaSelect.moveToFirst()) {
                String nombre_sel = filaSelect.getString(0);
                String edad_sel = filaSelect.getString(1);

                et_nombre.setText(nombre_sel);
                et_edad.setText(edad_sel);
            }
            filaSelect.close();
        }

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
            case "modificar":
                modificar(db);
                break;
        }

        db.close();
    }

    private void darAlta(SQLiteDatabase db) {
        String nombre = et_nombre.getText().toString();
        String dni = et_dni.getText().toString();
        String edad = et_edad.getText().toString();

        if(nombre.length() <= 0) {
            Toast.makeText(this, "Debes rellenar el campo nombre!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(dni.length() <= 0) {
            Toast.makeText(this, "Debes rellenar el campo dni!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(edad.length() <= 0) {
            Toast.makeText(this, "Debes rellenar el campo edad!", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor fila = db.rawQuery("SELECT nombre FROM propietarios WHERE dni='"+dni+"'", null);
        if(fila.moveToFirst()) {
            Toast.makeText(this, "Ya existe un propietario con ese DNI!", Toast.LENGTH_SHORT).show();
            return;
        }
        fila.close();

        ContentValues content = new ContentValues();
        content.put("nombre", nombre);
        content.put("dni", dni);
        content.put("edad", edad);
        db.insert("propietarios", null, content);

        et_nombre.setText("");
        et_dni.setText("");
        et_edad.setText("");

        Toast.makeText(this, "Propietario creado!", Toast.LENGTH_SHORT).show();
    }

    private void darBaja(SQLiteDatabase db) {
        if(sp_dni.getSelectedItem() == null) {
            Toast.makeText(this, "No hay propietarios creados!", Toast.LENGTH_SHORT).show();
            return;
        }
        String dni = sp_dni.getSelectedItem().toString();

        if(dni.length() <= 0) {
            Toast.makeText(this, "Debes elegir un dni!", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor fila = db.rawQuery("SELECT nombre FROM propietarios WHERE dni='"+dni+"'", null);
        if(!fila.moveToFirst()) {
            Toast.makeText(this, "No existe ese propietario!", Toast.LENGTH_SHORT).show();
            et_dni.setText("");
            return;
        }
        fila.close();

        Cursor filaCoche = db.rawQuery("SELECT matricula FROM coches WHERE dni='"+dni+"'", null);
        if(filaCoche.getCount() > 0) {
            Toast.makeText(this, "Este propietario tiene coches!", Toast.LENGTH_SHORT).show();
            return;
        }
        filaCoche.close();

        db.execSQL("DELETE FROM propietarios WHERE dni='"+dni+"'");
        db.execSQL("DELETE FROM coches WHERE dni='"+dni+"'");

        Toast.makeText(this, "Propietario eliminado!", Toast.LENGTH_SHORT).show();

        cargarDni();
    }

    private void modificar(SQLiteDatabase db) {
        if(sp_dni.getSelectedItem() == null) {
            Toast.makeText(this, "No hay propietarios creados!", Toast.LENGTH_SHORT).show();
            return;
        }
        String dni = sp_dni.getSelectedItem().toString();
        String nombre = et_nombre.getText().toString();
        String edad = et_edad.getText().toString();

        if(dni.length() <= 0) {
            Toast.makeText(this, "Debes elegir el dni!", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor fila = db.rawQuery("SELECT nombre FROM propietarios WHERE dni='"+dni+"'", null);
        if(!fila.moveToFirst()) {
            Toast.makeText(this, "No existe ese propietario!", Toast.LENGTH_SHORT).show();
            et_dni.setText("");
            return;
        }
        fila.close();

        ContentValues values = new ContentValues();

        values.put("dni", dni);
        if(nombre.length() > 0) {
            values.put("nombre", nombre);
        }
        if(edad.length() > 0) {
            values.put("edad", edad);
        }

        db.update("propietarios", values, "dni='" + dni + "'", null);

        Toast.makeText(this, "Propietario editado!", Toast.LENGTH_SHORT).show();

        cargarDni();
        et_nombre.setText("");
        et_edad.setText("");
    }

    public void volver(View v) {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "seguros", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        String dni = parent.getItemAtPosition(position).toString();
        Cursor fila = db.rawQuery("SELECT nombre, edad FROM propietarios WHERE dni='"+dni+"'", null);
        if(fila.getCount() > 0 && fila.moveToFirst()) {
            String nombre_sel = fila.getString(0);
            String edad_sel = fila.getString(1);

            et_nombre.setText(nombre_sel);
            et_edad.setText(edad_sel);
        }
        fila.close();

        db.close();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
