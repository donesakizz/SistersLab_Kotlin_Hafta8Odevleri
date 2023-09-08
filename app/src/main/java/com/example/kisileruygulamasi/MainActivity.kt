package com.example.kisileruygulamasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kisileruygulamasi.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() ,SearchView.OnQueryTextListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var notlarListe : ArrayList<Notlar>
    private lateinit var adapter:NotlarAdapter
    private lateinit var refNotlar: DatabaseReference //burayı ekled

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Not Uygulaması"
        setSupportActionBar(binding.toolbar)

        binding.rv.setHasFixedSize(true)
        binding.rv.layoutManager = LinearLayoutManager(this)

        val db = FirebaseDatabase.getInstance()

        refNotlar = db.getReference("Notlar")

        notlarListe = ArrayList()



        adapter = NotlarAdapter(this,notlarListe,refNotlar)
        binding.rv.adapter = adapter

        tumKisiler()

        binding.fab.setOnClickListener {
            alertGoster()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)

        val item = menu?.findItem(R.id.action_ara)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

        fun alertGoster(){
            val tasarim = LayoutInflater.from(this).inflate(R.layout.alert_tasarim,null)
            val editTextAd = tasarim.findViewById(R.id.editTextAd) as EditText
            val editTextAciklama = tasarim.findViewById(R.id.editTextTel) as EditText

            val ad = AlertDialog.Builder(this)

            ad.setTitle("Not Ekle")
            ad.setView(tasarim)
            ad.setPositiveButton("Ekle"){ dialogInterface , i ->
                val not_ad = editTextAd.text.toString().trim()
                val not_aciklama = editTextAciklama.text.toString().trim()

                val not = Notlar("",not_ad,not_aciklama)//

                refNotlar.push().setValue(not)//

                Toast.makeText(applicationContext, "$not_ad - $not_aciklama" , Toast.LENGTH_SHORT).show()
            }
            ad.setNegativeButton("İptal") {dialogInterface , i ->

            }
            ad.create().show()
        }

    override fun onQueryTextSubmit(query: String): Boolean {
        aramaYap(query)
        Log.e("Gönderilen arama ",query.toString())
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        aramaYap(newText)
        Log.e("Harf Girdikçe",newText.toString())
        return true
    }

    fun tumKisiler(){
        refNotlar.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notlarListe.clear()

                for (c in snapshot.children){
                    val not = c.getValue(Notlar::class.java)

                    if (not != null){
                        not.not_id = c.key
                        notlarListe.add(not)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun aramaYap(aramaKelime:String){
        refNotlar.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notlarListe.clear()

                for (c in snapshot.children){
                    val not = c.getValue(Notlar::class.java)

                    if (not != null){
                        if (not.not_ad!!.contains(aramaKelime)){
                            not.not_id = c.key
                            notlarListe.add(not)
                        }

                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}
