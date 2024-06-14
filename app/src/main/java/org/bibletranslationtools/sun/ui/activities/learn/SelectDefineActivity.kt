package org.bibletranslationtools.sun.ui.activities.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import org.bibletranslationtools.sun.adapter.DefineListAdapter
import org.bibletranslationtools.sun.data.dao.CardDAO
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ActivitySelectDefineBinding

class SelectDefineActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySelectDefineBinding.inflate(layoutInflater) }
    private val cardDAO by lazy { CardDAO(this) }
    lateinit var cardList: List<Card> //list of card

    private lateinit var defineListAdapter: DefineListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val id = intent.getStringExtra("id")
        cardList = cardDAO.getCardsByFlashCardId(id)
        binding.defineRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.defineRv.setHasFixedSize(true)
        defineListAdapter = DefineListAdapter(cardList)
        binding.defineRv.adapter = defineListAdapter
        defineListAdapter.notifyDataSetChanged()


    }

    //get card define from cardList
    private fun getCardDefine(cardList: List<Card>, num: Int): List<Card> {
        val cardDefineList = mutableListOf<Card>()
        for (i in 0 until num) {
            cardDefineList.add(cardList[i])
        }
        return cardDefineList

    }
}