package com.example.windows7.gitrepositoriescards.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.windows7.gitrepositoriescards.R;

public class LoginActivity extends AppCompatActivity {

    /**
     * Created by MARCELO on 08/02/2018.
     */

    private final static String USUARIO = "app@app.com";
    private final static String SENHA = "teste123";

    private EditText etEmail, etSenha;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etSenha = (EditText) findViewById(R.id.etSenha);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new ISetOnClickListennerLogin());
    }

    /**
     * Verifica os dados fornecidos para o USUARIO e caso estejam corretos reedireciona paga a pagina principal
     */
    private void checkCredencials(){
        if(checkInputs()){
            if(etEmail.getText().toString().replace(" ", "").equals(USUARIO) && etSenha.getText().toString().equals(SENHA)){
                redirectToMain();
            }
            else{
                Toast.makeText(getApplicationContext(), "Email ou senha invalido(s)!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Verifica a entrada de dados do USUARIO
     * Se os campos estiverem vazio e ou a SENHA conter menos de 6 caracteres e ou
     * e ou for fornecido um email invalido retorna false impossibilitando o login
     * @return bool
     */
    private boolean checkInputs(){
        if (!etEmail.getText().toString().equals("") && !etSenha.getText().toString().equals("")) {
            if(!etEmail.getText().toString().contains("@")){
                Toast.makeText(getApplicationContext(), "Informe um email valido!", Toast.LENGTH_LONG).show();
                return false;
            }
            if(etSenha.getText().toString().length() < 6){
                Toast.makeText(getApplicationContext(), "A senha deve conter no minimo seis caracteres!", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
        Toast.makeText(getApplicationContext(), "Não deixe nenhum campo vazio!", Toast.LENGTH_LONG).show();
        return false;
    }

    /**
     * Reedireciona o USUARIO a tela principal
     */
    private void redirectToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Classe que implementa a interface onclicklistener que é utilizada como callback
     * quando o evento de click no botão login for disparado
     */
    private class ISetOnClickListennerLogin implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            checkCredencials();
        }
    }
}
