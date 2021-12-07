package com.example.android.hellocoffee

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.hellocoffee.databinding.ActivityMainBinding
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val INITIAL_CUPS_OF_COFFEE = 1
        const val MIN_CUPS_OF_COFFEE = 1
        const val MAX_CUPS_OF_COFFEE = 100
        const val COFFEE_BASE_PRICE = 5
        const val WHIPPED_CREAM_TOPPING_PRICE = 1
        const val CHOCOLATE_TOPPING_PRICE = 2
    }

    private lateinit var binding: ActivityMainBinding
    private var numberOfCoffees = 1
    private var hasWhippedCreamTopping: Boolean = false
    private var hasChocolateTopping: Boolean = false
    private var orderPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        displayQuantity(INITIAL_CUPS_OF_COFFEE)
        displayPrice(calculateOrderPrice(INITIAL_CUPS_OF_COFFEE))

        binding.cbWhippedTopping.setOnCheckedChangeListener { buttonView, _ ->
            checkTopping(buttonView)
        }
        binding.cbChocolateTopping.setOnCheckedChangeListener { buttonView, _ ->
            checkTopping(buttonView)
        }
        binding.tvIncreaseQuantity.setOnClickListener { incrementQuantityCupsCoffee() }
        binding.tvDecreaseQuantity.setOnClickListener { decrementQuantityCupsCoffee() }
        binding.orderButton.setOnClickListener {
            displayOrderSummary(
                binding.edtClientNameInput.text.toString().trim(),
                numberOfCoffees
            )
            openEmailOrderSummary(
                arrayOf("pablopato@example.com"),
                getString(R.string.email_subject),
                composeEmailSubject(
                    binding.edtClientNameInput.text.toString(),
                    numberOfCoffees,
                    orderPrice)
            )
        }

        setContentView(binding.root)
    }

    private fun displayQuantity(quantity: Int) {
        binding.quantityTextView.text = quantity.toString()
    }

    private fun displayPrice(price: Int) {
        binding.tvPrice.text =
            NumberFormat.getCurrencyInstance(Locale("ES", "ES")).format(price)
    }

    private fun checkTopping(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
            when (view.id) {
                R.id.cbWhippedTopping -> {
                    hasWhippedCreamTopping = checked
                }
                R.id.cbChocolateTopping -> {
                    hasChocolateTopping = checked
                }
            }
            displayPrice(calculateOrderPrice(numberOfCoffees))
        }
    }

    private fun incrementQuantityCupsCoffee() {
        if (numberOfCoffees < MAX_CUPS_OF_COFFEE) {
            numberOfCoffees++
            displayQuantity(numberOfCoffees)
            displayPrice(calculateOrderPrice(numberOfCoffees))
        } else {
            Toast.makeText(this, getString(R.string.max_cups_of_coffee_warning), Toast.LENGTH_SHORT).show()
        }
    }

    private fun decrementQuantityCupsCoffee() {
        if (numberOfCoffees > MIN_CUPS_OF_COFFEE) {
            numberOfCoffees--
            displayQuantity(numberOfCoffees)
            displayPrice(calculateOrderPrice(numberOfCoffees))
        } else {
            Toast.makeText(this, getString(R.string.min_cups_of_coffee_warning), Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateOrderPrice(quantity: Int): Int {
        orderPrice = calculateOneCoffeePrice() * quantity
        return orderPrice
    }

    private fun calculateOneCoffeePrice(): Int {
        var coffeeBasePrice = COFFEE_BASE_PRICE
        if (hasWhippedCreamTopping) {
            coffeeBasePrice += WHIPPED_CREAM_TOPPING_PRICE
        }
        if (hasChocolateTopping) {
            coffeeBasePrice += CHOCOLATE_TOPPING_PRICE
        }
        return coffeeBasePrice
    }

    private fun displayOrderSummary(clientName: String, numberOfCoffees: Int) {
        binding.tvClientNameOrderSummary.text = getString(R.string.client_name, clientName)
        binding.tvToppingsOrderSummary.text =
            getString(R.string.toppings, toppingsTextRepresentation())
        binding.tvQuantityOrderSummary.text = resources.getQuantityString(
            R.plurals.number_of_coffees,
            numberOfCoffees,
            numberOfCoffees
        )
        binding.tvPriceOrderSummary.text =
            getString(
                R.string.order_total_price,
                NumberFormat.getCurrencyInstance(Locale("ES", "ES")).format(orderPrice)
            )
        binding.llyContainerOrderSummary.visibility = View.VISIBLE
    }

    private fun toppingsTextRepresentation(): String {
        var toppingsText = ""
        if (hasWhippedCreamTopping) {
            toppingsText += getString(R.string.whipped_cream_topping) + ", "
        }
        if (hasChocolateTopping) {
            toppingsText += getString(R.string.chocolate_topping)
        }
        return toppingsText
    }

    private fun openEmailOrderSummary(emailTo: Array<String>, subject: String, emailBody: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, emailTo)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, emailBody)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun composeEmailSubject(
        clientName: String,
        numberOfCoffees: Int,
        orderPrice: Int): String {
        return """
            |${getString(R.string.client_name, clientName)}
            |${getString(R.string.toppings, toppingsTextRepresentation())}
            |${resources.getQuantityString(R.plurals.number_of_coffees, numberOfCoffees, numberOfCoffees)}
            |${getString(R.string.order_total_price, NumberFormat.getCurrencyInstance(Locale("ES", "ES")).format(orderPrice))}
        """.trimMargin()
    }
}