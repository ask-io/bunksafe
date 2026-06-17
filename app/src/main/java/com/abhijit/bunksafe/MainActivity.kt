package com.abhijit.bunksafe

// Pre-built tools from android and kotlin libraries eg: Button or text view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.ceil

/* class MainActivity : (Defines a blueprint for main screen)

: AppCompatActivity(): The colon means inheritance.
It means our MainActivity inherits all the standard behaviors of a modern,
backward-compatible Android screen from Google's library. */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val totalInput = findViewById<EditText>(R.id.inputTotal)
        val attendedInput = findViewById<EditText>(R.id.inputAttended)
        val requiredInput = findViewById<EditText>(R.id.inputRequired)
        val calcButton = findViewById<Button>(R.id.btnCalculate)
        val resultText = findViewById<TextView>(R.id.tvResult)
        val resetButton = findViewById<Button>(R.id.btnReset)

        calcButton.setOnClickListener {
            val total = totalInput.text.toString().toIntOrNull()
            val attended = attendedInput.text.toString().toIntOrNull()
            val required = requiredInput.text.toString().toDoubleOrNull()

            if (total == null || attended == null || required == null) {
                resultText.text = "Fill in all fields."
                return@setOnClickListener
            }

            if (attended > total) {
                resultText.text = "Attended cannot exceed total."
                return@setOnClickListener
            }

            val current = (attended.toDouble() / total) * 100
            val result = calculate(total, attended, required)

            when {
                result > 0 -> {
                    resultText.setTextColor(android.graphics.Color.parseColor("#FF6B6B"))
                    resultText.text = """
                        Current: %.1f%%
                        
                        You need to attend %d more class(es) to reach %.0f%%.
                    """.trimIndent().format(current, result, required)
                }

                result < 0 -> {
                    resultText.setTextColor(android.graphics.Color.parseColor("#4BB543"))
                    resultText.text = """
                        Current: %.1f%%
                        
                        You can skip %d class(es) and stay above %.0f%%.
                    """.trimIndent().format(current, -result, required)
                }

                else -> {
                    resultText.setTextColor(android.graphics.Color.YELLOW)
                    resultText.text = """
                        Current: %.1f%%
                        
                        You are exactly at %.0f%%. Don't skip anything.
                    """.trimIndent().format(current, required)
                }
            }
        }

        resetButton.setOnClickListener {
            totalInput.text.clear()
            attendedInput.text.clear()
            requiredInput.text.clear()
            resultText.text = ""
        }
    }

    private fun calculate(total: Int, attended: Int, required: Double): Int {
        val current = (attended.toDouble() / total) * 100
        return if (current >= required) {
            var skip = 0
            while (true) {
                val newPct = (attended.toDouble() / (total + skip + 1)) * 100
                if (newPct < required) break
                skip++
            }
            -skip
        } else {
            ceil((required * total - 100.0 * attended) / (100.0 - required)).toInt()
        }
    }
}