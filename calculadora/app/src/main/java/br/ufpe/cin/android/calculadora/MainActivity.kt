package br.ufpe.cin.android.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast

//Import all of the widgets
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.NumberFormatException


// Creathe the class as an OnClick Listener
class MainActivity : AppCompatActivity() , View.OnClickListener{
    var writtenText:String = ""
    var resultText:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add on click listeners for all the buttons
        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_Add.setOnClickListener(this);
        btn_Clear.setOnClickListener(this);
        btn_Divide.setOnClickListener(this);
        btn_Dot.setOnClickListener(this);
        btn_Equal.setOnClickListener(this);
        btn_LParen.setOnClickListener(this);
        btn_Multiply.setOnClickListener(this);
        btn_Power.setOnClickListener(this);
        btn_RParen.setOnClickListener(this);
        btn_Subtract.setOnClickListener(this);

    }

    override fun onClick(v: View?) {
        when (v) {
            // Botoões que somente escrevem
            btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_Add, btn_Divide, btn_Dot, btn_LParen, btn_Multiply, btn_Power, btn_RParen, btn_Subtract -> {
                writtenText = writtenText + (v as Button).text.toString();
                text_calc.setText(writtenText)
            }

            //Botão e Apaga
            btn_Clear -> {
                if (writtenText != "") {
                    writtenText = writtenText.substring(0, writtenText.length - 1);
                    text_calc.setText(writtenText)
                }
            }

            //Botão de Resultado
            btn_Equal -> {
                print("EQUAL")
                // Try Catch to see if the expression is valid
                try{
                    // If it is, show it!
                    resultText = eval(writtenText).toString()
                    text_info.text = resultText
                }
                catch (e: Throwable) {
                    // If not, show a Toast
                    Toast.makeText(this, "Expressão Inválida", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {
            }
        }
    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }


//    Save and restore Instance States
    override fun onSaveInstanceState(outState: Bundle?) {
        // Save the user's current game state
        outState?.run {
            putString(WRITTEN_TEXT, writtenText)
            putString(RESULT, resultText)
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState)

        // Restore state members from saved instance
        savedInstanceState?.run {
            writtenText = getString(WRITTEN_TEXT)!!
            resultText = getString(RESULT)!!
        }
    }

    companion object {
        val WRITTEN_TEXT = ""
        val RESULT = ""
    }
}
