package com.cursoandroid.agendacontato.agendacontato;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.NumberKeyListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.cursoandroid.agendacontato.agendacontato.app.MessageBox;
import com.cursoandroid.agendacontato.agendacontato.app.ViewHelper;
import com.cursoandroid.agendacontato.agendacontato.dataBase.Database;
import com.cursoandroid.agendacontato.agendacontato.dominio.RepositorioContato;
import com.cursoandroid.agendacontato.agendacontato.dominio.entidades.Contato;
import com.cursoandroid.agendacontato.agendacontato.util.DateUtils;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActCadContatos extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtTelefone, edtEndereco, edtDatasEspeciais, edtGrupos;
    private Spinner spnTipoEmail, spnTipoTelefone, spnTipoEndereco, spnTipoDatasEspeciais;
    private ArrayAdapter<String> adtTipoEmail, adtTipoTelefone, adtTipoEndereco, adtTipoDatasEspeciais;
    private SQLiteDatabase conn;
    private ArrayAdapter<String> adpContatos;
    private RepositorioContato repositorioContato;
    private Database database;
    private Contato contato;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cad_contatos);

        edtNome = (EditText)findViewById(R.id.edtNome);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtTelefone = (EditText)findViewById(R.id.edtTelefone);
        edtEndereco = (EditText)findViewById(R.id.edtEndereco);
        edtDatasEspeciais = (EditText)findViewById(R.id.edtDatasEspeciais);
        edtGrupos = (EditText)findViewById(R.id.edtGrupo);

        // Definindo mascaras
        SimpleMaskFormatter simpleMaskFormatterTelefone = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher maskTelefone = new MaskTextWatcher(edtTelefone, simpleMaskFormatterTelefone);
        edtTelefone.addTextChangedListener(maskTelefone);
        //----------------------------

        spnTipoEmail = (Spinner) findViewById(R.id.spnTipoEmail);
        spnTipoTelefone = (Spinner) findViewById(R.id.spnTipoTelefone);
        spnTipoEndereco = (Spinner) findViewById(R.id.spnTipoEndereco);
        spnTipoDatasEspeciais = (Spinner) findViewById(R.id.spnTipoDatasEspeciais);

        adtTipoEmail = ViewHelper.createArrayAdapter(this, spnTipoEmail);
        adtTipoTelefone = ViewHelper.createArrayAdapter(this, spnTipoTelefone);
        adtTipoEndereco = ViewHelper.createArrayAdapter(this, spnTipoEndereco);
        adtTipoDatasEspeciais = ViewHelper.createArrayAdapter(this, spnTipoDatasEspeciais);

        adtTipoEmail.add("Casa");
        adtTipoEmail.add("Trabalho");
        adtTipoEmail.add("Outros");

        adtTipoTelefone.add("Celular");
        adtTipoTelefone.add("Trabalho");
        adtTipoTelefone.add("Casa");
        adtTipoTelefone.add("Principal");
        adtTipoTelefone.add("Fax Trabalho");
        adtTipoTelefone.add("Fax Casa");
        adtTipoTelefone.add("Pager");
        adtTipoTelefone.add("Outros");

        adtTipoEndereco.add("Casa");
        adtTipoEndereco.add("Trabalho");
        adtTipoEndereco.add("Outros");

        adtTipoDatasEspeciais.add("Aniversario");
        adtTipoDatasEspeciais.add("Data Comemorativa");
        adtTipoDatasEspeciais.add("Outros");

        ExibeDataListener listener = new ExibeDataListener();

        edtDatasEspeciais.setOnClickListener(new ExibeDataListener());
        edtDatasEspeciais.setOnFocusChangeListener(listener);
        edtDatasEspeciais.setKeyListener(null);

        Bundle bundle =  getIntent().getExtras();

        // Verificando se é um Contato novo ou se é uma alteraçao
        if ((bundle != null) && (bundle.containsKey(ActContato.PAR_CONTATO))){

            contato = (Contato) bundle.getSerializable(ActContato.PAR_CONTATO);
            preencheDados();

        }else {

            contato = new Contato();

        }
        // -------------------------------------------------------


        try{
            database = new Database(this);
            conn = database.getWritableDatabase();
            repositorioContato = new RepositorioContato(conn);

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

    // Criando um Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_act_cad_contatos, menu);

        if (contato.getId() != 0){

            menu.getItem(1).setVisible(true);
            menu.getItem(0).setTitle("Atualizar");
        }

        return  super.onCreateOptionsMenu(menu);

    }
    // ------------------------------------------------------

    // Verificando em qual item foi do menu foi clicado
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nni_ACAO1:

                salvar();
                finish();
            break;

            case R.id.nni_ACAO2:
                //item.setTitle("Atualizar");
                excluir();
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
    // -----------------------------------------------------


    private void preencheDados(){

        edtNome.setText(contato.getNome());
        edtTelefone.setText(contato.getTelefone());
        spnTipoTelefone.setSelection(Integer.parseInt(contato.getTipoTelefone()));
        edtEmail.setText(contato.getEmail());
        spnTipoEmail.setSelection(Integer.parseInt(contato.getTipoEmail()));
        edtEndereco.setText(contato.getEndereco());
        spnTipoEndereco.setSelection(Integer.parseInt(contato.getTipoEndereco()));

            DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
            String dt = format.format(contato.getDatasEspeciais());
            edtDatasEspeciais.setText(dt);

        spnTipoDatasEspeciais.setSelection(Integer.parseInt(contato.getTipoDatasEspeciais()));
        edtGrupos.setText(contato.getGrupos());

    }


    private void excluir(){

        try{

            repositorioContato.excluir(contato.getId());

        }catch (Exception e){

            MessageBox.show(this, "Erro", "Erro ao excluir os dados!!!" + e.getMessage() );

         }

    }

    private void salvar(){

        try{

            contato.setNome(edtNome.getText().toString());
            contato.setTelefone(edtTelefone.getText().toString());
            contato.setEmail(edtEmail.getText().toString());
            contato.setEndereco(edtEndereco.getText().toString());
            contato.setGrupos(edtGrupos.getText().toString());

            contato.setTipoTelefone( String.valueOf(spnTipoTelefone.getSelectedItemPosition()));
            contato.setTipoEmail(String.valueOf(spnTipoEmail.getSelectedItemPosition()));
            contato.setTipoEndereco(String.valueOf(spnTipoEndereco.getSelectedItemPosition()));
            contato.setTipoDatasEspeciais(String.valueOf(spnTipoDatasEspeciais.getSelectedItemPosition()));

            if (contato.getId() == 0){
                repositorioContato.inserir(contato);
            }else {
                repositorioContato.alterar(contato);
            }


        }catch (Exception e){

            MessageBox.show(this, "Erro", "Erro ao salvar os dados!!!" + e.getMessage() );

        }

    }


    private class ExibeDataListener implements View.OnClickListener, View.OnFocusChangeListener{


        @Override
        public void onClick(View view) {

            exibeData();
        }

        @Override
        public void onFocusChange(View view, boolean b) {

          if (hasWindowFocus()){
             // exibeData();
          }

        }
    }

    private void exibeData(){

        Calendar calendar =  Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(calendar.DAY_OF_MONTH);


        DatePickerDialog dlg = new DatePickerDialog(this, new SelecionaDataListener(), ano, mes, dia);
        dlg.show();
    }


    private class SelecionaDataListener implements DatePickerDialog.OnDateSetListener{

        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {


            String dt = DateUtils.dateToString(year, monthOfYear, dayOfMonth);
            Date data = DateUtils.getDate(year, monthOfYear,dayOfMonth);

            edtDatasEspeciais.setText(dt);
            contato.setDatasEspeciais(data);

        }
    }
}
