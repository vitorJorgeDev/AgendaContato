package com.cursoandroid.agendacontato.agendacontato;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.cursoandroid.agendacontato.agendacontato.app.MessageBox;
import com.cursoandroid.agendacontato.agendacontato.dataBase.Database;
import com.cursoandroid.agendacontato.agendacontato.dominio.RepositorioContato;
import com.cursoandroid.agendacontato.agendacontato.dominio.entidades.Contato;

public class ActContato extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageButton btnAdicionar;
    private EditText edtPesquisa;
    private ListView lstContatos;
    private Database database;
    private SQLiteDatabase conn;
    private ArrayAdapter<Contato> adpContatos;
    private RepositorioContato repositorioContato;
    private FiltraDados filtraDados;

    public static final String PAR_CONTATO = "CONTATO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_contato);

        btnAdicionar = (ImageButton) findViewById(R.id.btnAdcionar);
        edtPesquisa = (EditText) findViewById(R.id.edtPesquisaId);
        lstContatos = (ListView) findViewById(R.id.lstContatos);

        btnAdicionar.setOnClickListener(this);
        lstContatos.setOnItemClickListener(this);

        try{
            database = new Database(this);
            conn = database.getWritableDatabase();
            repositorioContato = new RepositorioContato(conn);
            adpContatos = repositorioContato.buscaContatos(this);
            lstContatos.setAdapter(adpContatos);

            filtraDados = new FiltraDados(adpContatos);
            edtPesquisa.addTextChangedListener(filtraDados);

        }catch (Exception e ){

            MessageBox.show(this, "Erro", "Erro ao criada Banco!!!" + e.getMessage() );
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (conn != null){

            conn.close();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        filtraDados.setArrayAdapter(adpContatos);
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, ActCadContatos.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        adpContatos = repositorioContato.buscaContatos(this);
        filtraDados.setArrayAdapter(adpContatos);
        lstContatos.setAdapter(adpContatos);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Contato contato = adpContatos.getItem(i);
        Intent intent = new Intent(this, ActCadContatos.class);
        intent.putExtra(PAR_CONTATO, contato);
        startActivityForResult(intent, 0);
    }

    private class FiltraDados implements TextWatcher{

        private  ArrayAdapter<Contato> arrayAdapter;
        private FiltraDados(ArrayAdapter<Contato> arrayAdapter){

            this.arrayAdapter = arrayAdapter;

        }

        public void setArrayAdapter(ArrayAdapter<Contato> arrayAdapter){

            this.arrayAdapter = arrayAdapter;
        }


        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            arrayAdapter.getFilter().filter(charSequence);

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
