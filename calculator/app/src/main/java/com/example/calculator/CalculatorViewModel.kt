package com.example.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calculator.RetrofitClient.calculateApi
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException


class CalculatorViewModel : ViewModel() {
    val displayText: MutableLiveData<String> = MutableLiveData("")
    val resultText: MutableLiveData<String> = MutableLiveData("")
    val onError: MutableLiveData<Boolean> = MutableLiveData(false)
    val apiError: MutableLiveData<String> = MutableLiveData("")

    private var firstNumberText: String = ""
    private var secondNumberText: String = ""
    private var firstNumber = 0
    private var secondNumber = 0
    private var operation: String = ""
    private var hasFirstNumber = false

    fun onNumberPressed(number: String) {
        if (hasFirstNumber) {
            secondNumberText += number
            saveSecondNumber(secondNumberText.toInt())
        } else {
            firstNumberText += number
            saveFirstNumber(firstNumberText.toInt())
        }
    }

    fun saveFirstNumber(number: Int) {
        firstNumber = number
        displayText.value = firstNumber.toString()
    }

    fun saveOperation(operation: String) {
        this.operation = operation
        displayText.value = "${displayText.value} $operation"
        hasFirstNumber = true
    }

    fun saveSecondNumber(number: Int) {
        secondNumber = number
        displayText.value = "${displayText.value} $number"
        showPreviousResult()
    }

    fun calculate() {
        val result = when (operation) {
            SUM -> sum(firstNumber, secondNumber)
            SUBTRACT -> subtract(firstNumber, secondNumber)
            MULTIPLY -> multiply(firstNumber, secondNumber)
            DIVIDE -> divide(firstNumber, secondNumber)
            else -> 0
        }

        if (hasFirstNumber) {
            firstNumber = result
            displayText.value = firstNumber.toString()
            resultText.value = result.toString()
        }

        secondNumber = 0
        secondNumberText = ""
        operation = ""
    }

    fun calculationApi() {
        calculateApi.getTotalValues()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { values ->
                    val sumResult = values?.values?.let { sumApi(it) } ?: run {
                        apiError.value = "A API retornou um valor nulo."
                        return@subscribe
                    }
                    displayText.value = sumResult.toString()
                },
                { error -> handleApiError(error) }
            )
    }

    private fun handleApiError(error: Throwable) {
        when (error) {
            is HttpException -> {
                val errorCode = error.code()
                val errorMessage = when (errorCode) {
                    300 -> "Erro 300: A solicitação tem mais de uma resposta possível"
                    400 -> "Erro 400: Requisição inválida."
                    500 -> "Erro 500: Erro interno do servidor."
                    else -> "Erro desconhecido: $errorCode"
                }
                apiError.value = errorMessage
            }
            is IOException -> {
                apiError.value = "Sem conexão com a internet. Verifique sua conexão e tente novamente."
            }
            else -> {
                apiError.value = "Erro desconhecido: ${error.message}"
            }
        }
    }

    private fun sumApi(values: List<Int>): Int {
        if (values.isEmpty()) {
            apiError.value = "A lista está vazia."
            return 0
        }
        var result = 0
        values.forEach { number ->
            result += number
        }
        return result
    }

    private fun sum(firstNumber: Int, secondNumber: Int): Int = firstNumber + secondNumber

    private fun subtract(firstNumber: Int, secondNumber: Int): Int = firstNumber - secondNumber

    private fun multiply(firstNumber: Int, secondNumber: Int): Int = firstNumber * secondNumber

    private fun divide(firstNumber: Int, secondNumber: Int): Int {
        return if (secondNumber != 0) {
            firstNumber / secondNumber
        } else {
            onError.value = true
            0
        }
    }

    private fun showPreviousResult() {
        calculate()
    }

    fun resetAll() {
        displayText.value = ""
        resultText.value = ""
        onError.value = false
        firstNumber = 0
        secondNumber = 0
        hasFirstNumber = false
        firstNumberText = ""
        secondNumberText = ""
    }

    companion object {
        const val SUM = "+"
        const val SUBTRACT = "-"
        const val MULTIPLY = "X"
        const val DIVIDE = "/"
    }
}