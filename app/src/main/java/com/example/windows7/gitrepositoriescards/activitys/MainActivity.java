package com.example.windows7.gitrepositoriescards.activitys;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.windows7.gitrepositoriescards.utils.CardAdapter;
import com.example.windows7.gitrepositoriescards.utils.EndPoints;
import com.example.windows7.gitrepositoriescards.R;
import com.example.windows7.gitrepositoriescards.Repositore;
import com.example.windows7.gitrepositoriescards.utils.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.support.v7.widget.SearchView;

public class MainActivity extends AppCompatActivity {

    /**
     * Created by MARCELO on 08/02/2018.
     */

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private ArrayList<Repositore> arrayRepositores, arrayRepositoresAll;
    private ProgressBar progressBar;
    private TextView txtResultSearch;

    private Handler handlerUpdateUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtResultSearch = (TextView) findViewById(R.id.txtResultSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayRepositores = new ArrayList<>();
        arrayRepositoresAll = new ArrayList<>();

        cardAdapter = new CardAdapter(arrayRepositores, this);

        recyclerView.setAdapter(cardAdapter);

        consumeWSRepositores();

        createHandlerToUpdateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView;
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint("Digite o nome do repositório");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                searchRepositore(s);
                return false;
            }
        });
        return true;
    }

    /**
     * Tratanto os eventos de selecão de item do menu
     * @param item
     * @return bool
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logoff:
                redirectToMain();
                break;
             //...
        }
        return true;
        }

    /**
     * Exibe uma caso não tenha sido encontrado nenhum repositorio através da procura
     * @param found bool
     */
    private void displayMessageOfResultSearch(boolean found){
        txtResultSearch.setVisibility(found ? View.GONE : View.VISIBLE);
    }

    /**
     * Raeliza uma procura nos repositorios carregados através de um nome fornecido pelo usuario
     * Utiliza thread background para fazer a procura de repositorios para não sobrecarregar a main thread
     * se caso a lista seja muito extensa e devido a interação com o método ser constante por ser chamado
     * a cada disparo do evento de nova letra inserida pelo usuario na pesquisa
     * @param name do repositore
     */
    private void searchRepositore(final String name){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized(this) {
                        if (checkInputSearch(name)) {
                            arrayRepositores.clear();
                            boolean found = false;
                            for (Repositore repositore : arrayRepositoresAll) {
                                if (repositore.getName().toLowerCase().contains(name.toLowerCase())) {
                                    arrayRepositores.add(repositore);
                                    found = true;
                                }
                            }
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("found", found);
                            Message message = new Message();
                            message.setData(bundle);
                            //envia uma mensagem ao handler para que atualize a UI
                            handlerUpdateUI.sendMessage(message);
                        }
                    }
                }
            }).start();
    }

    /**
     * Se o valor de entrada na pesquisa conter menos de quatro caracteres retorna false, se estiver vazio reseta
     * a lista e retorna false
     */
    private boolean checkInputSearch(String name){
        if(name.length() == 0) {
            resetCards();
            return false;
        }
        else return name.length() >= 4;
    }

    /**
     * Reset a lista para exibir todos os cards do carregamento inicial
     */
    private void resetCards(){
        arrayRepositores.clear();
        arrayRepositores.addAll(arrayRepositoresAll);
        Bundle bundle = new Bundle();
        bundle.putBoolean("found", true);
        Message message = new Message();
        message.setData(bundle);
        handlerUpdateUI.sendMessage(message);
    }

    /**
     * Para a exibição do progressbar
     */
    private void stopProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Instancia o handler que sera utilizado para atualizar a UI através da main thread
     */
    private void createHandlerToUpdateUI(){
        handlerUpdateUI = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                displayMessageOfResultSearch(msg.getData().getBoolean("found"));
                cardAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    /**
     * Realiza uma chamada a api do github para retornar os repositorios do github utilizando a lib volley
     */
    public void consumeWSRepositores(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, EndPoints.API_GITHUB_REPOSITORES_LIST, new Response.Listener<String>() {
            //caso a request obtenha uma resposta bem sucedida
            @Override
            public void onResponse(String response) {
                try {
                    //cria um array json através da string json obtida
                    JSONArray jsonArray = new JSONArray(response);
                    //percorre toda a array json
                    for(int i = 0; i < jsonArray.length(); i++){
                        //recupera cada objeto json da array
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        //cria um novo objeto repositore e atribui os valores do repositore encontrado
                        Repositore repositore = new Repositore();
                        repositore.setName(jsonObject.getString("name"));
                        repositore.setDescription(jsonObject.getString("description"));
                        repositore.setUrl(jsonObject.getString("url"));
                        //adiciona o objeto repositore a arraylist
                        arrayRepositores.add(repositore);
                    }
                    arrayRepositoresAll.addAll(arrayRepositores);
                    cardAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.i(getClass().getName(), e.getMessage().toString());
                }
                stopProgressBar();
            }
            //caso ocorra um erro na request
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String mensagemErro = "O servidor se encontra em manutenção. Tente novamente mais tarde.";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        mensagemErro = "Tempo do aguardo de resposta do servidor se esgotou.";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        mensagemErro = "Falha na conexão com o servidor.";
                    }
                }
                Toast.makeText(getApplicationContext(), mensagemErro, Toast.LENGTH_LONG).show();
                stopProgressBar();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    /**
     * Reedireciona o usuario a tela de login
     */
    private void redirectToMain(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
