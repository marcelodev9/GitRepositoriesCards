package com.example.windows7.gitrepositoriescards.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.windows7.gitrepositoriescards.R;
import com.example.windows7.gitrepositoriescards.Repositore;
import java.util.ArrayList;

/**
 * Created by MARCELO on 08/02/2018.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardAdapterViewHolder>{
    private ArrayList<Repositore> arrayRepositores;
    private Context cxt;

    public CardAdapter(ArrayList<Repositore> arrayRepositores, Context cxt) {
        this.arrayRepositores = arrayRepositores;
        this.cxt = cxt;
    }

    /**
     * Possibilita inflar o layout desejado que sera posteriormente exibido nos itens do card
     * @param parent
     * @param viewType
     * @return viewholder
     */
    @Override
    public CardAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card, parent, false);
        CardAdapterViewHolder cardAdapterViewHolder = new CardAdapterViewHolder(cardView);
        return cardAdapterViewHolder;
    }

    /**
     * Possibilita o manipulação das views dos itens do card
     * @param holder view dos itens que serão exibidos
     * @param position
     */
    @Override
    public void onBindViewHolder(CardAdapterViewHolder holder, final int position) {
      holder.txtName.setText(arrayRepositores.get(position).getName());
      holder.txtDesciption.setText(arrayRepositores.get(position).getDescription());
      holder.txtUrl.setText(arrayRepositores.get(position).getUrl());

      holder.btnAccess.setOnClickListener(new ISetOnClickListeenerUrlAccess(arrayRepositores.get(position).getName(),
              arrayRepositores.get(position).getUrl()));
    }

    @Override
    public int getItemCount() {
        return arrayRepositores.size();
    }


    public class CardAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtDesciption, txtUrl;
        private Button btnAccess;

        public CardAdapterViewHolder(View view) {
            super(view);
            this.txtName = (TextView) view.findViewById(R.id.txtName);
            this.txtDesciption = (TextView) view.findViewById(R.id.txtDescription);
            this.txtUrl = (TextView) view.findViewById(R.id.txtUrl);
            this.btnAccess = (Button) view.findViewById(R.id.btnAccess);

            txtName.setTypeface(null, Typeface.BOLD);
            txtDesciption.setTypeface(null, Typeface.ITALIC);
        }
    }

    /**
     * Exibe um dialogalert para que o escolhe escolha se deseja abrir o repositore pelo navegador
     * @param nome do repositore
     * @param url do repositore
     */
    private void accessRepositore(String nome, final String url){
        final AlertDialog alert;
        final AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
        builder.setTitle(nome);
        builder.setMessage("Deseja acessar o repositório utilizando seu navegador?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                cxt.startActivity(browserIntent);
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        alert = builder.create();
        alert.show();
    }

    private class ISetOnClickListeenerUrlAccess implements View.OnClickListener{
        private String nome;
        private String url;

        public ISetOnClickListeenerUrlAccess(String nome, String url){
            this.nome = nome;
            this.url = url;
        }

        @Override
        public void onClick(View v) {
            accessRepositore(nome, url);
        }
    }
}
