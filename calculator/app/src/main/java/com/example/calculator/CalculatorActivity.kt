import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.calculator.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.calculator.CalculatorViewModel
import com.example.calculator.R

class CalculatorActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalculatorViewModel by viewModels()

    private val numberButtons = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNumberButtons()
        setupOperators()
        setupEqualButton()
        setupApiButton()
        setupClearButton()
        observeViewModel()

        binding.display.text = ""
    }

    private fun setupNumberButtons() {
        for (i in 0..9) {
            val button = binding.root.findViewWithTag<Button>(numberButtons[i])
            button.setOnClickListener {
                viewModel.onNumberPressed(numberButtons[i])
            }
        }
    }

    private fun setupOperators() {
        binding.sum.setOnClickListener { viewModel.saveOperation(getString(R.string.symbolSum)) }
        binding.subtraction.setOnClickListener { viewModel.saveOperation(getString(R.string.symbolSubtraction)) }
        binding.multiplication.setOnClickListener { viewModel.saveOperation(getString(R.string.symbolMultiplication)) }
        binding.division.setOnClickListener { viewModel.saveOperation(getString(R.string.symbolDivision)) }
    }

    private fun setupEqualButton() {
        binding.equal.setOnClickListener {
            binding.display.text = viewModel.displayText.value
            viewModel.calculate()
            binding.visible.setTextResult("")
        }
    }

    private fun setupApiButton() {
        binding.api.setOnClickListener {
            viewModel.calculationApi()
        }
    }

    private fun setupClearButton() {
        binding.clear.setOnClickListener {
            viewModel.resetAll()
        }
    }

    private fun observeViewModel() {
        viewModel.apiError.observe(this, Observer { error ->
            binding.display.text  = error
        })

        viewModel.displayText.observe(this, Observer { text ->
            binding.display.text = text
        })

        viewModel.resultText.observe(this, Observer { text ->
            binding.visible.setTextResult(text)
        })

        viewModel.onError.observe(this, Observer { isError ->
            binding.visible.setOnError(isError)
        })
    }
}
