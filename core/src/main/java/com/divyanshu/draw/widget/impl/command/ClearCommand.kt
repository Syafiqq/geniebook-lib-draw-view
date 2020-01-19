package com.divyanshu.draw.widget.impl.command

import com.divyanshu.draw.widget.contract.IMode
import com.divyanshu.draw.widget.contract.design.command.ICommand

class ClearCommand(private val container: ArrayList<IMode>) : ICommand {
    private val holder = ArrayList<IMode>()
    override fun up() {
        with(container) {
            holder.addAll(this)
            clear()
        }
    }

    override fun down() {
        with(holder) {
            container.addAll(this)
            clear()
        }
    }
}