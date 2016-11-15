package com.example.dam32_corral.coches;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListViewCoches extends AppCompatActivity {
    private ListView lv_coches;
    private EditText etBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_coches);
        lv_coches = (ListView) findViewById(R.id.listView);
        etBuscar = (EditText) findViewById(R.id.etBuscar);

        List<String> coches = new ArrayList<>();
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "seguros", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("SELECT matricula, marca, potencia, dni FROM coches", null);
        if(fila.moveToFirst()) {
            boolean primera = true;
            do {
                if(!primera) {
                    fila.moveToNext();
                }

                String texto = "Matricula: "+fila.getString(0)+", Marca: "+fila.getString(1)+", Potencia: "+fila.getString(2)+", DNI: "+fila.getString(3);
                coches.add(texto);

                if(primera) {
                    primera = false;
                }
            } while(!fila.isLast());
        }

        db.close();

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, coches);
        lv_coches.setAdapter(adaptador);
    }

    public void buscar(View view) {
        String texto = etBuscar.getText().toString();
        if(texto.length() <= 0) {
            return;
        }

        for(int i=0; i<lv_coches.getCount(); i++) {
            if(lv_coches.getItemAtPosition(i).toString().contains(texto)) {
                lv_coches.setSelection(i);
            }
        }
    }

    public void salir(View view) {
        finish();
    }
}
