package sk.alival.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import sk.alival.app.databinding.ActivityMainBinding
import sk.alival.crutch.logging.Logs
import sk.alival.crutch.logging.dm
import sk.alival.crutch.logging.dt
import sk.alival.crutch.logging.em
import sk.alival.crutch.logging.et
import sk.alival.crutch.logging.wm
import sk.alival.crutch.logging.wt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logs.init(true, null)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button1.setOnClickListener {
            Logs.dm(tag = "tag-dm") { "dmMessage" }
            Logs.em { "dmMessage" }
            Logs.wm(tag = "tag-wm") { "dmMessage" }
            Logs.dt { IllegalStateException("dtMessage") }
            Logs.et(tag = "tag-et") { IllegalStateException("etMessage") }
            Logs.wt(tag = "tag-wt") { IllegalStateException("wtMessage") }
        }
    }
}
