package gb.dom55.bme.smarthome.dashboard.adapters.touchhelpers

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SensorTouchHelperCallback(private val deviceTouchHelperListener: RecyclerViewTouchHelperInterface)
    : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        // val dragFlags = ItemTouchHelper.END or ItemTouchHelper.START
        val dragFlags = 0
        val swipeFlags = ItemTouchHelper.UP
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        deviceTouchHelperListener.onItemDismissed(viewHolder.adapterPosition)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

}