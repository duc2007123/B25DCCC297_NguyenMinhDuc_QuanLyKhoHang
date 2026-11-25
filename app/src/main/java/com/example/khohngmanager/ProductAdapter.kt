package com.example.khohngmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.text.DecimalFormat

class ProductAdapter(
    private val context: Context,
    private var productList: List<Product>
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val decimalFormat = DecimalFormat("#,###")

    fun updateData(newList: List<Product>) {
        this.productList = newList
        notifyDataSetChanged()
    }

    override fun getCount(): Int = productList.size

    override fun getItem(position: Int): Product = productList[position]

    override fun getItemId(position: Int): Long = productList[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_product, parent, false)
            holder = ViewHolder(
                name = view.findViewById(R.id.tvItemName),
                category = view.findViewById(R.id.tvItemCategory),
                price = view.findViewById(R.id.tvItemPrice),
                quantity = view.findViewById(R.id.tvItemQuantity),
                status = view.findViewById(R.id.tvItemStatus),
                lowStockBadge = view.findViewById(R.id.tvLowStockBadge)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val product = getItem(position)

        holder.name.text = product.name
        holder.category.text = product.category
        holder.price.text = "Giá bán: ${decimalFormat.format(product.price)} đ"
        holder.quantity.text = "SL: ${product.quantity}"

        if (product.active == 1) {
            holder.status.text = "Đang kinh doanh"
            holder.status.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
        } else {
            holder.status.text = "Ngừng kinh doanh"
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
        }

        if (product.quantity < 5) {
            holder.lowStockBadge.visibility = View.VISIBLE
            holder.quantity.setTextColor(ContextCompat.getColor(context, R.color.error_red))
        } else {
            holder.lowStockBadge.visibility = View.GONE
            holder.quantity.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
        }

        return view
    }

    private class ViewHolder(
        val name: TextView,
        val category: TextView,
        val price: TextView,
        val quantity: TextView,
        val status: TextView,
        val lowStockBadge: TextView
    )
}
