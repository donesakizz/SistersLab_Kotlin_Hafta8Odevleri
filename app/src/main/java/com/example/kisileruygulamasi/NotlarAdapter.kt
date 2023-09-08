package com.example.kisileruygulamasi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference

class NotlarAdapter (private val mContext:Context,
                     private val notlarListe:List<Notlar>,
                     private val refNotlar: DatabaseReference)

    : RecyclerView.Adapter<NotlarAdapter.CardTasarimTutucu>(){

    inner class CardTasarimTutucu(tasarim: View) :RecyclerView.ViewHolder(tasarim) {
        var textViewNotBilgi: TextView
        var imageViewNokta: ImageView

        init {
            textViewNotBilgi = tasarim.findViewById(R.id.textViewKisiBilgi)
            imageViewNokta = tasarim.findViewById(R.id.imageViewNokta)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimTutucu {
        val tasarim = LayoutInflater.from(mContext).inflate(R.layout.not_card_tasarim,parent,false)
        return CardTasarimTutucu(tasarim)
    }

    override fun getItemCount(): Int {
        return notlarListe.size
    }

    override fun onBindViewHolder(holder: CardTasarimTutucu, position: Int) {
        val kisi = notlarListe.get(position)

        holder.textViewNotBilgi.text = "${kisi.not_ad}-${kisi.not_aciklama}"

        holder.imageViewNokta.setOnClickListener {

            val popupMenu = PopupMenu(mContext, holder.imageViewNokta)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_sil -> {
                        Snackbar.make(
                            holder.imageViewNokta,
                            "${kisi.not_ad} silinsin mi?",
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction("EVET") {
                                refNotlar.child(kisi.not_id!!).removeValue()
                            }.show()
                        true
                    }

                    R.id.action_guncelle -> {
                        alertGoster(kisi)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

        fun alertGoster(kisi:Notlar){
            val tasarim = LayoutInflater.from(mContext).inflate(R.layout.alert_tasarim,null)
            val editTextAd = tasarim.findViewById(R.id.editTextAd) as EditText
            val editTextAciklama = tasarim.findViewById(R.id.editTextTel) as EditText

            editTextAd.setText(kisi.not_ad)
            editTextAciklama.setText(kisi.not_aciklama)

            val ad = AlertDialog.Builder(mContext)

            ad.setTitle("Notu Güncelle")
            ad.setView(tasarim)
            ad.setPositiveButton("Güncelle"){ dialogInterface , i ->
                val kisiAd = editTextAd.text.toString().trim()
                val kisiTel = editTextAciklama.text.toString().trim()

                val bilgiler = HashMap<String,Any>()
                bilgiler.put("not_ad",kisiAd)
                bilgiler.put("not_aciklama",kisiTel)

                refNotlar.child(kisi.not_id!!).updateChildren(bilgiler)

                Toast.makeText(mContext, "$kisiAd - $kisiTel" , Toast.LENGTH_SHORT).show()
            }
            ad.setNegativeButton("İptal") {dialogInterface , i ->

            }
            ad.create().show()
        }


    }
